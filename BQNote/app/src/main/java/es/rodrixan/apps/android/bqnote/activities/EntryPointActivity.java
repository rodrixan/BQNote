package es.rodrixan.apps.android.bqnote.activities;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;

import es.rodrixan.apps.android.bqnote.fragments.EntryPointFragment;
import es.rodrixan.apps.android.bqnote.utilities.Utils;

/**
 * Entry Point of the App. Checks the user login
 */
public class EntryPointActivity extends SingleFragmentActivity implements EntryPointFragment.Callbacks, EvernoteLoginFragment.ResultCallback {

    private static final String CONSUMER_KEY = "rodrixan-5042";
    private static final String CONSUMER_SECRET = "3caeb6da5a2e5ce5";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = false;

    private EvernoteSession mEvernoteSession;

    @Override
    protected Fragment createFragment() {
        return EntryPointFragment.newInstance();
    }

    @Override
    protected void init() {
        Log.d(Utils.LOG_TAG, "Creating Evernote Session");
        mEvernoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
    }

    @Override
    public void setToolBar(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    @Override
    public void onLoginFinished(final boolean successful) {
        if (successful) {
            Toast.makeText(this, "SUCCESS!!!", Toast.LENGTH_SHORT).show();
            Log.d(Utils.LOG_TAG, "Login Successful");
        } else {
            Log.d(Utils.LOG_TAG, "Login Error. Closing app...");
            finish();
        }
    }

}
