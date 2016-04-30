package es.rodrixan.apps.android.bqnote.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.services.EvernoteService;
import es.rodrixan.apps.android.bqnote.utilities.Utils;

/**
 * Entry Point of the App. It decides whether or not the user has logged in, and redirect him/her to the next activity
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
         * @return evernote service in order to call the methods needed
         */
        EvernoteService getEvernoteService();
    }

    private Callbacks mCallbacks;
    private EvernoteService mEvernoteService;

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
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_entry_point, container, false);

        mEvernoteService = mCallbacks.getEvernoteService();

        wireToolbar(v);
        if (!mEvernoteService.isLoggedIn()) {
            Log.i(Utils.LOG_TAG, "Logging in...");
            mEvernoteService.loginToEvernote(getActivity());
        } else {
            Log.i(Utils.LOG_TAG, "Already Logged!");
            Toast.makeText(getActivity(), "LOGIN COMPLETED", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                Toast.makeText(getActivity(), "Logout not available yet!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}