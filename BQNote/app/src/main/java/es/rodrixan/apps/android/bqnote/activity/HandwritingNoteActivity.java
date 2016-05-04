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
 * Created by jutna on 04/05/2016.
 */
public class HandwritingNoteActivity extends SingleFragmentActivity implements HandwritingNoteFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        Log.d(Utils.LOG_TAG, "CreateFragment: Handwriting");
        return new HandwritingNoteFragment();
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
        Log.d(Utils.LOG_TAG, "HandwritingNoteActivity new Intent");
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
                Log.d(Utils.LOG_TAG, "Going back from Handwriting nav icon");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
