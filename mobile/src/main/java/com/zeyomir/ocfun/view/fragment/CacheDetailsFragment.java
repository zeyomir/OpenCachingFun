package com.zeyomir.ocfun.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.event.SetUpToolbarEvent;

import butterknife.BindView;

public class CacheDetailsFragment extends BaseFragment {
    private static final String CODE = "code";
    private static final String NAME = "name";
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private CacheDetailsPagerAdapter pagerAdapter;
    private CachePhotosFragment photosFragment;
    private CacheInfoFragment infoFragment;
    private CacheLogsFragment logsFragment;
    private String name;

    @Override
    public int getLayoutResourceId() {
        return R.layout.f_cache_details;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostInjection() {
        Bundle arguments = getArguments();
        String code = arguments.getString(CODE);

        name = arguments.getString(NAME);
        bus.post(new SetUpToolbarEvent(getString(R.string.toolbar_title_single_cache), name, R.menu.cache_info, true));
        photosFragment = CachePhotosFragment.create(code);
        infoFragment = CacheInfoFragment.create(code);
        logsFragment = CacheLogsFragment.create(code);

        pagerAdapter = new CacheDetailsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bus.post(new SetUpToolbarEvent(getString(R.string.toolbar_title_single_cache), name, -1, false));
                        return;
                    case 1:
                        bus.post(new SetUpToolbarEvent(getString(R.string.toolbar_title_single_cache), name, R.menu.cache_info, true));
                        return;
                    case 2:
                        bus.post(new SetUpToolbarEvent(getString(R.string.toolbar_title_single_cache), name, -1, false));
                        return;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static CacheDetailsFragment create(String code, String name) {
        CacheDetailsFragment fragment = new CacheDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CODE, code);
        bundle.putString(NAME, name);
        fragment.setArguments(bundle);
        return fragment;
    }

    private class CacheDetailsPagerAdapter extends FragmentPagerAdapter {
        public CacheDetailsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.cache_photos_tab_title);
                case 1:
                    return getString(R.string.cache_info_tab_title);
                case 2:
                    return getString(R.string.cache_logs_tab_title);
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return photosFragment;
                case 1:
                    return infoFragment;
                case 2:
                    return logsFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
