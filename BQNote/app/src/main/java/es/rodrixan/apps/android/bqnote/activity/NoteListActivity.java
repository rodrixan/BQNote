package es.rodrixan.apps.android.bqnote.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.fragment.NoteListFragment;
import es.rodrixan.apps.android.bqnote.fragment.NoteViewFragment;
import es.rodrixan.apps.android.bqnote.task.GetNoteHtmlTask;
import es.rodrixan.apps.android.bqnote.util.Utils;

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

    /**
     * New intent for the activity
     *
     * @param packageContext context for the activity
     * @return intent of the activity
     */
    public static Intent newIntent(final Context packageContext) {
        Log.d(Utils.LOG_TAG, "NoteListActivity new Intent");
        final Intent i = new Intent(packageContext, NoteListActivity.class);
        return i;
    }

    @Override
    public void onNoteSelected(final String html, final GetNoteHtmlTask task) {

        if (findViewById(R.id.detail_fragment_container) == null) {
            Log.i(Utils.LOG_TAG, "Inflating for a phone");
            final Intent i = NoteViewActivity.newIntent(this, task.getNoteRef(), html);
            startActivity(i);
        } else {
            Log.i(Utils.LOG_TAG, "Inflating for a tablet");
            final Fragment detail = NoteViewFragment.newInstance(task.getNoteRef(), html);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, detail)
                    .commit();
        }
    }

    @Override
    public void setToolBar(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }


}
