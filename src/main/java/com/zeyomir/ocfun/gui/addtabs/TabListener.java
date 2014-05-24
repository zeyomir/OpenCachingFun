package com.zeyomir.ocfun.gui.addtabs;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.actionbarsherlock.app.ActionBar;
import org.holoeverywhere.app.Fragment;

public class TabListener<T extends Fragment> implements ActionBar.TabListener {
    private final FragmentActivity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    private Fragment mFragment;

    /**
     * Constructor used each time a new tab is created.
     *
     * @param activity The host Activity, used to instantiate the fragment
     * @param tag      The identifier tag for the fragment
     * @param clz      The fragment's Class, used to instantiate the fragment
     */
    public TabListener(FragmentActivity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;

        mFragment = (Fragment) mActivity.getSupportFragmentManager().findFragmentByTag(tag);
        if (mFragment != null && !mFragment.isDetached()) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            ft.detach(mFragment);
            ft.commit();
        }
    }

    /* The following are each of the ActionBar.TabListener callbacks */

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Check if the fragment is already initialized
        Log.i("parent", "on tab selected");
        FragmentTransaction fft = mActivity.getSupportFragmentManager().beginTransaction();
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = (Fragment) Fragment.instantiate(mActivity, mClass.getName());
            fft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            fft.attach(mFragment);
        }
        fft.commit();
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.i("parent", "on tab UNselected");
        FragmentTransaction fft = mActivity.getSupportFragmentManager().beginTransaction();
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            fft.detach(mFragment);
        }
        fft.commit();
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }
}