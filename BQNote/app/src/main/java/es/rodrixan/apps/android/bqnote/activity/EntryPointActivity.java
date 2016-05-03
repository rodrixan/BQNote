package es.rodrixan.apps.android.bqnote.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.fragment.EntryPointFragment;
import es.rodrixan.apps.android.bqnote.util.EvernoteUtils;
import es.rodrixan.apps.android.bqnote.util.Utils;

/**
 * Entry point of the App. Checks the user login
 */
public class EntryPointActivity extends SingleFragmentActivity implements EntryPointFragment.Callbacks, EvernoteLoginFragment.ResultCallback {

    private EvernoteSession mEvernoteSession;

    public static Intent newIntent(final Context packageContext) {
        Log.d(Utils.LOG_TAG, "EntryPointActivity new Intent");
        final Intent i = new Intent(packageContext, EntryPointActivity.class);
        return i;
    }

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
        mEvernoteSession = EvernoteUtils.createSession(this);
    }

    @Override
    public void setToolBar(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    @Override
    public void authenticateToEvernote() {
        mEvernoteSession.authenticate(this);
    }

    @Override
    public void launchNoteListActivity() {
        Log.i(Utils.LOG_TAG, "Launching NoteList");
        final Intent i = NoteListActivity.newIntent(this);
        startActivity(i);
        finish();
    }


    @Override
    public void onLoginFinished(final boolean successful) {
        if (successful) {
            Toast.makeText(this, R.string.login_ok, Toast.LENGTH_SHORT).show();
            Log.i(Utils.LOG_TAG, "Login Successful");
            launchNoteListActivity();
        } else {
            Log.i(Utils.LOG_TAG, "Login canceled. Closing app...");
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
