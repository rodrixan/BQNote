package es.rodrixan.apps.android.bqnote.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.util.EvernoteUtils;
import es.rodrixan.apps.android.bqnote.util.Utils;

/**
 * Entry Point of the App. It decides whether the user has logged in or not, and redirect him to the list of notes
 */
public class EntryPointFragment extends Fragment {

    /**
     * Callbacks for the activity to implement
     */
    public interface Callbacks {
        /**
         * @param toolbar toolbar to set in the parent activity
         */
        void setToolBar(Toolbar toolbar);

        /**
         * Gives the login process to parent activity
         */
        void authenticateToEvernote();

        /**
         * Starts the activity for listing notes
         */
        void launchNoteListActivity();
    }

    private Callbacks mCallbacks;


    /**
     * @return new instance of  this fragment
     */
    public static Fragment newInstance() {
        return new EntryPointFragment();
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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_entry_point, container, false);


        wireToolbar(v);
        if (!EvernoteUtils.isLoggedIn()) {
            Log.i(this.getClass().getName(), "Logging in...");
            mCallbacks.authenticateToEvernote();
        } else {
            Log.i(this.getClass().getName(), "Already Logged!");
            mCallbacks.launchNoteListActivity();
        }
        return v;
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


}