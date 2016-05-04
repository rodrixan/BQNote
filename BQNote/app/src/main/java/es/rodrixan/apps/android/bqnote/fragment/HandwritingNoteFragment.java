package es.rodrixan.apps.android.bqnote.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.util.Utils;
import es.rodrixan.apps.android.bqnote.view.HandwritingView;

/**
 * Handwriting note: allow to create a new note by hand
 */
public class HandwritingNoteFragment extends Fragment {

    private HandwritingView mHandwritingView;
    private Button mCancelButton;
    private Button mSaveButton;

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

        //setData();

        return view;
    }

    /**
     * Assigns the layout components to the local fields
     *
     * @param v root view
     */
    private void wireComponents(final View v) {
        Log.d("HOLA", "SGKJNGN");
        wireToolbar(v);

        mHandwritingView = (HandwritingView) v.findViewById(R.id.handwriting_note_view);
        mCancelButton = (Button) v.findViewById(R.id.handwriting_note_button_cancel);
        mSaveButton = (Button) v.findViewById(R.id.handwriting_note_button_save);
    }

    /**
     * Creates and set the appropiated listeners for each component
     */
    private void setListeners() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d("HOLA", "PROBANDp");

            }
        });

        mSaveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                Toast.makeText(getActivity(), "Saving...", Toast.LENGTH_SHORT);
                return true;
            }
        });
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
     * Notifies the parent fragment to create a new note with given title and content
     *
     * @param resultCode result code of the action
     */
    private void sendResult(final int resultCode) {

        if (getTargetFragment() == null) {
            Log.d("HOLA", "PSKFHS");
            return;
        }
        final Intent i = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

}
