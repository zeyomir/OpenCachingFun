package com.zeyomir.ocfun.gui.addtabs;

import android.app.ActionBar;

import android.app.FragmentTransaction;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class TabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment mFragment;
    private final FragmentActivity mActivity;
    private final String mTag;
    private final Class<T> mClass;

    /** Constructor used each time a new tab is created.
      * @param activity  The host Activity, used to instantiate the fragment
      * @param tag  The identifier tag for the fragment
      * @param clz  The fragment's Class, used to instantiate the fragment
      */
    public TabListener(FragmentActivity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        
        mFragment = (Fragment) mActivity.getSupportFragmentManager().findFragmentByTag(tag);
        if (mFragment != null && !mFragment.isDetached()){
        	android.support.v4.app.FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        	ft.detach(mFragment);
        	ft.commit();
        }
    }

    /* The following are each of the ActionBar.TabListener callbacks */
    
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Check if the fragment is already initialized
    	Log.i("parent", "on tab selected");
    	android.support.v4.app.FragmentTransaction fft = mActivity.getSupportFragmentManager().beginTransaction();
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = (Fragment) Fragment.instantiate(mActivity, mClass.getName());
            fft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            fft.attach(mFragment);
        }
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    	Log.i("parent", "on tab UNselected");
	    android.support.v4.app.FragmentTransaction fft = mActivity.getSupportFragmentManager().beginTransaction();
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            fft.detach(mFragment);
        }
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }
}