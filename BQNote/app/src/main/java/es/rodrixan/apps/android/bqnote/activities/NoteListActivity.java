package es.rodrixan.apps.android.bqnote.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.services.EvernoteService;
import es.rodrixan.apps.android.bqnote.services.EvernoteServiceImpl;
import es.rodrixan.apps.android.bqnote.utilities.Utils;

/**
 * Shows a list of the notes in the logged Evernote account
 */
public class NoteListActivity extends SingleFragmentActivity {
    private EvernoteService mEvernoteService;

    @Override
    protected Fragment createFragment() {
        return null;
    }

    @Override
    protected void init() {
        mEvernoteService = new EvernoteServiceImpl();
    }

    @Override
    protected int getLayoutResId() {
        //this id choses between single layout when phone or two-pane when tablet
        return R.layout.activity_masterdetail;
    }

    public static Intent newIntent(final Context packageContext) {
        Log.d(Utils.LOG_TAG, "NoteListActivity new Intent");
        final Intent i = new Intent(packageContext, NoteListActivity.class);
        return i;
    }
}
