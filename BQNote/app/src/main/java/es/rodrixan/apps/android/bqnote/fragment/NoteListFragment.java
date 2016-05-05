package es.rodrixan.apps.android.bqnote.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;

import net.vrallev.android.task.TaskResult;

import java.util.ArrayList;
import java.util.List;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.activity.HandwritingNoteActivity;
import es.rodrixan.apps.android.bqnote.task.CreateNewNoteTask;
import es.rodrixan.apps.android.bqnote.task.DeleteNoteTask;
import es.rodrixan.apps.android.bqnote.task.FindNotesTask;
import es.rodrixan.apps.android.bqnote.task.GetNoteHtmlTask;
import es.rodrixan.apps.android.bqnote.util.EvernoteUtils;
import es.rodrixan.apps.android.bqnote.util.Utils;

/**
 * Shows a list of the notes found in the given Evernote account
 */
public class NoteListFragment extends Fragment {

    private static final String SAVED_FILTER = "filter";
    private static final String DIALOG_NEW_NOTE = "dialog new note";
    private static final int REQUEST_NEW_NOTE = 0;
    private static final int REQUEST_HANDWRITING_NOTE = 1;

    private static final int MAX_NOTES = 20;

    /**
     * Callbacks for the activity to implement
     */
    public interface Callbacks {
        /**
         * Actions to do when a note is selected
         *
         * @param html html string with the content of the note
         * @param task task where the result come from
         */
        void onNoteSelected(final String html, final GetNoteHtmlTask task);

        /**
         * @param toolbar toolbar to set in the parent activity
         */
        void setToolBar(Toolbar toolbar);
    }

    private Callbacks mCallbacks;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyView;
    private CoordinatorLayout mCoordinatorLayout;

    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private List<NoteRef> mNoteRefList;

    private NoteFilter mNoteFilter = null;

    private int mLastOffset = MAX_NOTES;

    /**
     * @return new instance of  this fragment
     */
    public static Fragment newInstance() {
        return new NoteListFragment();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mCallbacks = (Callbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_note_list, container, false);

        wireToolbar(v);
        wireSwipeLayout(v);
        wireRecyclerView(v);
        wireFloatingButton(v);
        wireCoordinatorLayout(v);

        restoreData(savedInstanceState);
        loadDataFromEvernote();

        updateUI();

        /* after refreshing the UI, set the adapter*/
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }


    /**
     * Attach a toolbar to the activity
     *
     * @param v root view
     * @return the toolbar attached
     */
    private Toolbar wireToolbar(final View v) {
        final Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mCallbacks.setToolBar(toolbar);
        return toolbar;
    }

    private void wireSwipeLayout(final View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
    }

    /**
     * Setup of the RecyclerView
     *
     * @param v root view
     */
    private void wireRecyclerView(final View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mEmptyView = (TextView) v.findViewById(R.id.empty_list_text_view);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Set up of the floating button
     *
     * @param v root view
     */
    private void wireFloatingButton(final View v) {
        final FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                launchNewNoteDialog();
            }
        });
    }

    /**
     * Setup of the coordinatorLayout
     *
     * @param v root view
     */
    private void wireCoordinatorLayout(final View v) {
        mCoordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinator_layout);
    }

    /**
     * Launches the new note dialog
     */
    private void launchNewNoteDialog() {
        Log.i(this.getClass().getName(), "New note dialog requested");
        final FragmentManager manager = getActivity().getSupportFragmentManager();
        final NewNoteDialogFragment dialog = NewNoteDialogFragment.newInstance();
        dialog.setTargetFragment(this, REQUEST_NEW_NOTE);
        dialog.show(manager, DIALOG_NEW_NOTE);
    }

    /**
     * Restores the data lost across state changes
     *
     * @param savedInstanceState data to restore
     */
    private void restoreData(final Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            Log.i(this.getClass().getName(), "Restoring data");
            mNoteFilter = (NoteFilter) savedInstanceState.getSerializable(SAVED_FILTER);
        }
        mNoteRefList = new ArrayList<>();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(this.getClass().getName(), "Saving data");
        outState.putSerializable(SAVED_FILTER, mNoteFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * Fetchs new notes from the server
     */
    private void loadDataFromEvernote() {
        Log.i(this.getClass().getName(), "Loading notes");
        forceRefresh();
        new FindNotesTask(0, mLastOffset, mNoteFilter).start(this, "find");
        mLastOffset += MAX_NOTES;
    }

    /**
     * Shows the swipe refreshing animation
     */
    private void forceRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    /**
     * Refresh the GUI
     */
    public void updateUI() {
        updateAdapter();
        updateSubtitle();
        updateEmptyView();
    }

    /**
     * Loads the adapter with new data
     */
    private void updateAdapter() {
        if (isAdded()) {
            if (mAdapter == null) {
                Log.d(this.getClass().getName(), "Updating adapter from zero");
                mAdapter = new NoteAdapter(mNoteRefList);
            } else {
                Log.d(this.getClass().getName(), "Updating adapter from setNotes method");
                mAdapter.setNotes(mNoteRefList);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Keeps the count of total notes
     */
    private void updateSubtitle() {
        final String subtitle = createSubtitle();
        setSubtitleText(subtitle);
    }

    /**
     * @return text for the subtitle, according to number of notes
     */
    private String createSubtitle() {
        final int nNotes = mNoteRefList.size();
        return getResources().getQuantityString(R.plurals.subtitle_plurals, nNotes, nNotes);
    }

    /**
     * Sets the number of notes under the activity title
     *
     * @param subtitle text to set
     */
    private void setSubtitleText(final String subtitle) {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    /**
     * Feedback for the user when there's no data:
     * If the adapter is empty and is not loading, show a textView, informing of that state
     */
    private void updateEmptyView() {
        if (mNoteRefList.size() == 0) {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                Log.i(this.getClass().getName(), "No notes to show");
                mEmptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when the loading progress is done. Charges the inner list of notes for the adapter.
     *
     * @param noteRefList list of notes
     */
    @TaskResult(id = "find")
    public void onFindNotes(final List<NoteRef> noteRefList) {
        Log.i(this.getClass().getName(), "Notes loaded");
        if (noteRefList == null || noteRefList.isEmpty()) {
            Snackbar.make(mCoordinatorLayout, R.string.no_notes, Snackbar.LENGTH_SHORT).show();
        } else {
            mNoteRefList = noteRefList;
        }
        onItemsLoadComplete();
    }

    /**
     * Refreshes the UI, reporting it to the swipeLayout
     */
    void onItemsLoadComplete() {
        updateUI();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                Log.i(this.getClass().getName(), "Logout from evernote");
                EvernoteUtils.logoutEvernote(getActivity());
                return true;
            case R.id.action_order_create:
                createNoteFilterSort(NoteSortOrder.CREATED, false);
                Log.i(this.getClass().getName(), "Sorting by modification");
                loadDataFromEvernote();
                return true;
            case R.id.action_order_title:
                createNoteFilterSort(NoteSortOrder.TITLE, true);
                Log.i(this.getClass().getName(), "Sorting by title");
                loadDataFromEvernote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates a NoteFilter, used for sorting the note list
     *
     * @param order     tpye of sorting for the filter (CREATED,UPDATED,TITLE...)
     * @param ascending if list must be sorted ascending or descending
     */
    private void createNoteFilterSort(final NoteSortOrder order, final boolean ascending) {
        mNoteFilter = new NoteFilter();
        mNoteFilter.setOrder(order.getValue());
        mNoteFilter.setAscending(ascending);
    }

    /**
     * Called when the swipeLyout is refreshing. Loads new data
     */
    private void refreshItems() {
        loadDataFromEvernote();
    }


    /**
     * Holder for a Evernote note in the RecyclerView
     * Implements OnClickListener in order to select a note
     */
    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView mTitleTextView;

        private NoteRef mNoteRef;

        public NoteHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            wireComponents(itemView);
        }

        /**
         * Wires up all the components in the assigned view
         *
         * @param itemView assigned view
         */
        private void wireComponents(final View itemView) {
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_note_title);
        }

        /**
         * Assign the UI components their data
         *
         * @param noteRef
         */
        public void bindNoteRef(final NoteRef noteRef) {
            mNoteRef = noteRef;
            mTitleTextView.setText(noteRef.getTitle());
        }

        @Override
        public void onClick(final View v) {
            Log.i(this.getClass().getName(), "Display note requested: " + mNoteRef.getGuid());
            new GetNoteHtmlTask(mNoteRef).start(NoteListFragment.this, "html");
        }

        @Override
        public boolean onLongClick(final View v) {
            Log.i(this.getClass().getName(), "Delete note requested: " + mNoteRef.getGuid());
            new DeleteNoteTask(mNoteRef.getGuid()).start(NoteListFragment.this, "delete");
            return true;
        }
    }//END_NoteHolder

    /**
     * Result of the task in charge of getting the html code for a note.
     * Launches the detail view of a note
     *
     * @param html string with html content of the note
     * @param task task which did the job
     */
    @TaskResult(id = "html")
    public void onGetNoteContentHtml(final String html, final GetNoteHtmlTask task) {
        startNoteActivity(html, task);
    }

    @TaskResult(id = "delete")
    public void onDeletedNote(final Boolean result) {
        if (result) {
            Log.i(this.getClass().getName(), "Note deleted");
            Snackbar.make(mCoordinatorLayout, R.string.deleted_note_ok, Snackbar.LENGTH_SHORT).show();
            loadDataFromEvernote();
        } else {
            Log.e(this.getClass().getName(), "Error on note deleted");
            Snackbar.make(mCoordinatorLayout, R.string.deleted_note_error, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Launches the detail activity (or fragment) for showing a note
     *
     * @param html html string that stroes the note content
     * @param task task in charge of loading the html
     */
    private void startNoteActivity(final String html, final GetNoteHtmlTask task) {
        updateUI();
        Log.i(this.getClass().getName(), "Launch note view");
        mCallbacks.onNoteSelected(html, task);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            Log.d(this.getClass().getName(), "Activity result not OK");
            return;
        }

        if (requestCode == REQUEST_NEW_NOTE) {
            Log.d(this.getClass().getName(), "Request code: new note");
            final boolean requestHandwriting = NewNoteDialogFragment.getCreateHandwritingFromIntentExtra(data);
            if (requestHandwriting) {
                Log.i(this.getClass().getName(), "Launching handwriting");
                startHandwritingNoteActivity();
                return;
            }
            Log.i(this.getClass().getName(), "Request for saving regular note");
            sendRegularNoteToEvernote(data);

        } else if (requestCode == REQUEST_HANDWRITING_NOTE) {
            Log.d(this.getClass().getName(), "Request code: handwriting note");
            Log.i(this.getClass().getName(), "Request for saving handwriting note");
            sendHandwritingNoteToEvernote(data);
        }
    }

    /**
     * Launches the activity for creating a handwritten note
     */
    private void startHandwritingNoteActivity() {
        Log.i(this.getClass().getName(), "Launching HandwritingNote Activity");
        updateUI();
        final Intent i = HandwritingNoteActivity.newIntent(getActivity());
        startActivityForResult(i, REQUEST_HANDWRITING_NOTE);
    }

    /**
     * Sends the received data from regular note to Evernote
     *
     * @param data intent containing data to send
     */
    private void sendRegularNoteToEvernote(final Intent data) {
        final String title = NewNoteDialogFragment.getNoteTitleFromIntentExtra(data);
        final String content = NewNoteDialogFragment.getNoteContentFromIntentExtra(data);
        if (title == null || content == null) {
            Log.d(this.getClass().getName(), "Empty fields on new note");
            Snackbar.make(mCoordinatorLayout, R.string.create_note_empty_fields, Snackbar.LENGTH_SHORT).show();
            return;
        }

        createNote(title, content, null);
    }

    /**
     * Starts a new task for sending the bitmap to the OCR server
     *
     * @param data intent where the data come from
     */
    private void sendHandwritingNoteToEvernote(final Intent data) {
        final String title = HandwritingNoteFragment.getNoteTitleFromIntentExtra(data);
        final Bitmap bitmap = HandwritingNoteFragment.getBitmapFromIntentExtra(data);
        if (title == null || bitmap == null || title.isEmpty()) {
            Log.d(this.getClass().getName(), "Empty fields on handwritten note");
            Snackbar.make(mCoordinatorLayout, R.string.create_note_empty_fields, Snackbar.LENGTH_SHORT).show();
            return;
        }
        createNote(title, null, bitmap);
    }

    /**
     * Creates a new note with data from the dialog fragment
     *
     * @param title   title of the note
     * @param content content of the note
     * @param bitmap  bitmap from handwriting note
     */
    public void createNote(final String title, final String content, final Bitmap bitmap) {
        Log.i(this.getClass().getName(), "Sending note: " + title + ", " + content + ", " + bitmap.toString());
        new CreateNewNoteTask(title, content, bitmap, getActivity()).start(this, "create");
    }

    @TaskResult(id = "create")
    public void onCreateNewNote(final Note note) {
        if (note != null) {
            Log.i(this.getClass().getName(), "New note created");
            Snackbar.make(mCoordinatorLayout, R.string.create_note_ok, Snackbar.LENGTH_SHORT).show();
            loadDataFromEvernote();
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.create_note_error, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Adapter of a note for the RecyclerView
     */
    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {

        private List<NoteRef> mNoteRefList;

        public NoteAdapter(final List<NoteRef> noteRefs) {
            mNoteRefList = noteRefs;
        }

        @Override
        public NoteHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View view = inflater.inflate(R.layout.list_item_note, parent, false);

            return new NoteHolder(view);
        }

        @Override
        public void onBindViewHolder(final NoteHolder holder, final int position) {
            holder.bindNoteRef(mNoteRefList.get(position));
        }


        @Override
        public int getItemCount() {
            return mNoteRefList.size();
        }

        public void setNotes(final List<NoteRef> noteRefs) {
            mNoteRefList = noteRefs;
        }
    }//END_NoteAdapter
}
