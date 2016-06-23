package com.zeyomir.ocfun.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;

import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.event.AuthenticationStateChangedEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedUserDetailsEvent;
import com.zeyomir.ocfun.eventbus.event.SetUpToolbarEvent;
import com.zeyomir.ocfun.eventbus.event.ShowCacheDetailsEvent;
import com.zeyomir.ocfun.view.component.TransactionAnimation;
import com.zeyomir.ocfun.view.fragment.CacheDetailsFragment;
import com.zeyomir.ocfun.view.fragment.CachesListFragment;


public class CachesActivity extends BaseActivity {

    @Override
    public int getLayoutResourceId() {
        return R.layout.a_caches;
    }

    @Override
    protected byte getMenuPosition() {
        return CACHES_MENU_POSITION;
    }

    @Override
    protected void onPostInjection() {
        setFragment(CachesListFragment.create(), true, TransactionAnimation.FADE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawer.setSelectionAtPosition(getMenuPosition(), false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.caches_list, menu);
        return true;
    }

    @Subscribe
    public void showDetails(ShowCacheDetailsEvent event) {
        setFragment(CacheDetailsFragment.create(event.code, event.name), false, TransactionAnimation.SLIDE);
    }

    @Override
    @Subscribe
    public void setUpToolbar(SetUpToolbarEvent event) {
        super.setUpToolbar(event);
    }

    @Override
    @Subscribe
    public void authenticationStateChanged(AuthenticationStateChangedEvent event) {
        super.authenticationStateChanged(event);
    }

    @Override
    @Subscribe
    public void userDetailsFetched(ReceivedUserDetailsEvent event) {
        super.userDetailsFetched(event);
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CachesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}
