package es.rodrixan.apps.android.bqnote.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import es.rodrixan.apps.android.bqnote.R;

/**
 * Abstract class for single fragment management
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {
    /**
     * @return the fragment created by the son classes
     */
    protected abstract Fragment createFragment();

    /**
     * Initizalization of the son classes
     */
    protected abstract void init();

    private FragmentManager mFragmentManager;

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        mFragmentManager = getSupportFragmentManager();

        addFragmentToManager(R.id.fragment_container);

        init();
    }

    /**
     * @param fragmentId id of the fragment to add
     */
    private void addFragmentToManager(final int fragmentId) {
        //Fragment transitions are used to add, remove, (de)attach, or replace fragments in the frag. list
        Fragment fragment = mFragmentManager.findFragmentById(fragmentId);

        if (fragment == null) {
            fragment = createFragment();
            mFragmentManager.beginTransaction().add(fragmentId, fragment).commit();
        }
    }
}
