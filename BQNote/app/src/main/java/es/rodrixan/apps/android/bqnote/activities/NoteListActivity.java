package es.rodrixan.apps.android.bqnote.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.fragments.NoteListFragment;
import es.rodrixan.apps.android.bqnote.utilities.Utils;

/**
 * Shows a list of the notes in the logged Evernote account
 */
public class NoteListActivity extends SingleFragmentActivity implements NoteListFragment.Callbacks {


    @Override
    protected Fragment createFragment() {
        Log.d(Utils.LOG_TAG, "NoteListFragment called");
        return NoteListFragment.newInstance();
    }

    @Override
    protected void init() {

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

    @Override
    public void onNoteSelected(final String noteGUID) {


    }

    @Override
    public void setToolBar(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }
}
