package es.rodrixan.apps.android.bqnote.activities;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;

import es.rodrixan.apps.android.bqnote.fragments.EntryPointFragment;
import es.rodrixan.apps.android.bqnote.services.EvernoteService;
import es.rodrixan.apps.android.bqnote.services.EvernoteServiceImpl;
import es.rodrixan.apps.android.bqnote.utilities.Utils;

/**
 * Entry point of the App. Checks the user login
 */
public class EntryPointActivity extends SingleFragmentActivity implements EntryPointFragment.Callbacks, EvernoteLoginFragment.ResultCallback {


    private EvernoteSession mEvernoteSession;
    private final EvernoteService mEvernoteService = new EvernoteServiceImpl();

    @Override
    protected Fragment createFragment() {
        return EntryPointFragment.newInstance();
    }

    /**
     * Creates the Evernote session when activity is created
     */
    @Override
    protected void init() {
        Log.i(Utils.LOG_TAG, "Creating Evernote Session");
        mEvernoteSession = mEvernoteService.createSession(this);
    }

    @Override
    public void setToolBar(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    @Override
    public EvernoteService getEvernoteService() {
        return mEvernoteService;
    }

    @Override
    public void onLoginFinished(final boolean successful) {
        if (successful) {
            Toast.makeText(this, "SUCCESS!!!", Toast.LENGTH_SHORT).show();
            Log.i(Utils.LOG_TAG, "Login Successful");
        } else {
            Log.i(Utils.LOG_TAG, "Login canceled. Closing app...");
            finish();
        }
    }

}
