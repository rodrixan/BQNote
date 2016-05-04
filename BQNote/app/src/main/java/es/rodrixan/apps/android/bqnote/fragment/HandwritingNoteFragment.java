package es.rodrixan.apps.android.bqnote.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.util.Utils;
import es.rodrixan.apps.android.bqnote.view.HandwritingView;

/**
 * Handwriting note: allow to create a new note by hand
 */
public class HandwritingNoteFragment extends Fragment {

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_BITMAP = "bitmap";


    private HandwritingView mHandwritingView;
    private Button mCancelButton;
    private Button mSaveButton;
    private AutoCompleteTextView mTitleEditText;

    /**
     * Callbacks for the activity to implement
     */
    public interface Callbacks {

        /**
         * @param toolbar toolbar to set in the parent activity
         */
        void setToolBar(Toolbar toolbar);
    }

    private Callbacks mCallbacks;

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
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_handwriting_note, container, false);

        Log.d(Utils.LOG_TAG, "HandwritingNoteFragment view created");
        wireComponents(view);
        setListeners();

        return view;
    }

    /**
     * Assigns the layout components to the local fields
     *
     * @param v root view
     */
    private void wireComponents(final View v) {

        wireToolbar(v);

        mHandwritingView = (HandwritingView) v.findViewById(R.id.handwriting_note_view);
        mCancelButton = (Button) v.findViewById(R.id.handwriting_note_button_cancel);
        mSaveButton = (Button) v.findViewById(R.id.handwriting_note_button_save);
        mTitleEditText = (AutoCompleteTextView) v.findViewById(R.id.handwriting_note_title);
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

    /**
     * Creates and set the appropriated listeners for each component
     */
    private void setListeners() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.i(Utils.LOG_TAG, "Saving image text");
                Toast.makeText(getActivity(), "Saving...", Toast.LENGTH_SHORT).show();
                sendDataToParentActivity();
            }
        });
    }

    /**
     * Sends the title and the bitmap with the text image to NoteListActivity
     */
    private void sendDataToParentActivity() {
        final String noteTitle = mTitleEditText.getText().toString();
        final Bitmap imageText = mHandwritingView.getBitmap();
        Log.d(Utils.LOG_TAG, "Data from Handwriting: [" + noteTitle + "], " + imageText.toString());
        final Intent i = new Intent();
        i.putExtra(EXTRA_TITLE, noteTitle);

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageText.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] bytes = stream.toByteArray();
        i.putExtra(EXTRA_BITMAP, bytes);

        getActivity().setResult(Activity.RESULT_OK, i);
        getActivity().finish();
    }

    /**
     * Given an intent, retrieves the note title
     *
     * @param i intent
     * @return note title
     */
    public static String getNoteTitleFromIntentExtra(final Intent i) {
        return i.getStringExtra(EXTRA_TITLE);
    }

    /**
     * Given an intent, retrieves the bitmap with the handwritten text
     *
     * @param i intent
     * @return bitmap with handwritten text
     */
    public static Bitmap getBitmapFromIntentExtra(final Intent i) {
        final byte[] bytes = i.getByteArrayExtra(EXTRA_BITMAP);
        final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bmp;
    }
}
