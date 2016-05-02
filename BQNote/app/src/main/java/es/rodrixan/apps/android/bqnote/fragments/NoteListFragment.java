package es.rodrixan.apps.android.bqnote.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Notebook;

import net.vrallev.android.task.TaskResult;

import java.util.ArrayList;
import java.util.List;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.services.EvernoteService;
import es.rodrixan.apps.android.bqnote.task.FindNotesTask;
import es.rodrixan.apps.android.bqnote.utilities.Utils;


public class NoteListFragment extends Fragment {
    private static final int REQUEST_NOTE = 1;
    private static final String SAVED_SUBTITLE_SHOWN = "subtitle";

    private static final int MAX_NOTES = 2;


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyView;

    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private List<NoteRef> mNoteRefList;
    private int mLastOffset = MAX_NOTES;

    private boolean mShowSubtitle = true;
    private Callbacks mCallbacks;
    private final Notebook mNotebook = null;
    private final LinkedNotebook mLinkedNotebook = null;
    private final String mQuery = null;

    public interface Callbacks {
        /**
         * Actions to do  when a note is clicked
         *
         * @param noteGUID id of the note
         */
        void onNoteSelected(String noteGUID);

        /**
         * @param toolbar toolbar to set in the parent activity
         */
        void setToolBar(Toolbar toolbar);
    }

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
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_note_list, container, false);

        wireToolbar(v);

        wireSwipeLayout(v);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mEmptyView = (TextView) v.findViewById(R.id.empty_list_text_view);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        restoreData(savedInstanceState);

        loadData();
        updateUI();
        mRecyclerView.setAdapter(mAdapter);
        return v;
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
     * Attach a toolbar to the activity
     *
     * @param v view where the toolbar is from
     * @return the toolbar attached
     */
    private Toolbar wireToolbar(final View v) {
        final Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mCallbacks.setToolBar(toolbar);
        return toolbar;
    }

    private void refreshItems() {
        loadData();
    }

    void onItemsLoadComplete() {
        updateUI();
        mSwipeRefreshLayout.setRefreshing(false);
    }


    private void restoreData(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mShowSubtitle = savedInstanceState.getBoolean(SAVED_SUBTITLE_SHOWN);
        }
        mNoteRefList = new ArrayList<>();
    }

    public void updateUI() {
        updateAdapter();
        updateSubtitle();
        updateEmptyView();
    }

    private void updateAdapter() {

        if (isAdded()) {
            if (mAdapter == null) {
                Log.d(Utils.LOG_TAG, "Updating adapter from zero");
                mAdapter = new NoteAdapter(mNoteRefList);
            } else {
                Log.d(Utils.LOG_TAG, "Updating adapter from setNotes method");
                mAdapter.setNotes(mNoteRefList);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateSubtitle() {

        final String subtitle = createSubtitle();
        setSubtitleText(subtitle);
    }

    private String createSubtitle() {
        final int nNotes = mNoteRefList.size();

        return (!mShowSubtitle) ? null : getResources().getQuantityString(R.plurals.subtitle_plurals, nNotes, nNotes);
    }

    private void setSubtitleText(final String subtitle) {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateEmptyView() {
        if (mNoteRefList.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadData() {
        Log.i(Utils.LOG_TAG, "Loading notes");
        new FindNotesTask(0, mLastOffset, mNotebook, mLinkedNotebook, mQuery).start(this);

        mLastOffset += MAX_NOTES;
    }

    @TaskResult
    public void onFindNotes(final List<NoteRef> noteRefList) {
        Log.i(Utils.LOG_TAG, "Loaded notes");
        if (noteRefList == null || noteRefList.isEmpty()) {
            Toast.makeText(getActivity(), R.string.no_notes, Toast.LENGTH_SHORT).show();
        } else {

            mNoteRefList = noteRefList;

        }
        onItemsLoadComplete();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_SHOWN, mShowSubtitle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    private void startNoteActivity(final String noteGUID) {
        Log.d(Utils.LOG_TAG, "launching Note Activity");
        updateUI();
        mCallbacks.onNoteSelected(noteGUID);
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_NOTE) {
            //HANDLE
        }
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
                Log.i(Utils.LOG_TAG, "Logout from evernote");
                EvernoteService.logoutEvernote(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //ViewHolder for notes
    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;

        private NoteRef mNoteRef;

        public NoteHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            wireComponents(itemView);
        }

        private void wireComponents(final View itemView) {
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_note_title);

        }


        public void bindNoteRef(final NoteRef noteRef) {
            mNoteRef = noteRef;
            Log.d(Utils.LOG_TAG, "Setting textview " + noteRef.getTitle());
            mTitleTextView.setText(noteRef.getTitle());
        }

        @Override
        public void onClick(final View v) {
            //mCallbacks.onNoteSelected(mNoteRef.getGuid());
        }

    }//END_NoteHolder


    //Adapter for a note list
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
            Log.d(Utils.LOG_TAG, "Position " + position);
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
