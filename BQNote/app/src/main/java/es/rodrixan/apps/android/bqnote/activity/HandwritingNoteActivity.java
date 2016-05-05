package es.rodrixan.apps.android.bqnote.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import es.rodrixan.apps.android.bqnote.R;
import es.rodrixan.apps.android.bqnote.fragment.HandwritingNoteFragment;
import es.rodrixan.apps.android.bqnote.util.Utils;

/**
 * Creates a handwriting note, and saves it as a Bitmap
 */
public class HandwritingNoteActivity extends SingleFragmentActivity implements HandwritingNoteFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return HandwritingNoteFragment.newInstance();
    }

    @Override
    protected void init() {

    }

    /**
     * New intent for the activity
     *
     * @param packageContext context for the activity
     * @return intent of the activity
     */
    public static Intent newIntent(final Context packageContext) {
        final Intent i = new Intent(packageContext, HandwritingNoteActivity.class);
        return i;
    }

    @Override
    public void setToolBar(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.handwriting_activity_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Log.d(this.getClass().getName(), "Going back from Handwriting nav icon");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
