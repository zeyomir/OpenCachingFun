package com.zeyomir.ocfun.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.configuration.OCFunApplication;
import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.eventbus.command.api.GetUserDetailsCommand;
import com.zeyomir.ocfun.eventbus.command.api.StartOAuthCommand;
import com.zeyomir.ocfun.eventbus.command.db.ClearAuthorizationCredentialsCommand;
import com.zeyomir.ocfun.eventbus.command.db.RemoveAllCachesCommand;
import com.zeyomir.ocfun.eventbus.event.AuthenticationStateChangedEvent;
import com.zeyomir.ocfun.eventbus.event.ChangeListSortEvent;
import com.zeyomir.ocfun.eventbus.event.FilterCachesEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedUserDetailsEvent;
import com.zeyomir.ocfun.eventbus.event.SetUpToolbarEvent;
import com.zeyomir.ocfun.eventbus.event.ShowCacheCompassEvent;
import com.zeyomir.ocfun.eventbus.event.ShowCacheOnMapEvent;
import com.zeyomir.ocfun.storage.PreferencesManager;
import com.zeyomir.ocfun.storage.model.CacheModel;
import com.zeyomir.ocfun.view.component.TransactionAnimation;
import com.zeyomir.ocfun.view.fragment.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;
import permissions.dispatcher.PermissionRequest;

public abstract class BaseActivity extends AppCompatActivity {
    protected static final byte NOT_IN_MENU = -1;
    protected static final byte CACHES_MENU_POSITION = 1;
    protected static final byte LOG_BOOK_MENU_POSITION = 2;
    protected static final byte MAP_MENU_POSITION = 3;
    protected static final byte SETTINGS_MENU_POSITION = 4;
    protected static final byte HELP_MENU_POSITION = 5;
    protected static final byte CONTACT_MENU_POSITION = 6;

    @Inject
    protected EventBus bus;
    @Inject
    protected Lazy<PreferencesManager> preferencesManager;

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.content)
    protected FrameLayout content;

    protected FragmentManager fragmentManager;
    protected Drawer drawer;

    protected abstract byte getMenuPosition();

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        ((OCFunApplication) getApplication()).getInjector().inject(this);
        int layoutResourceId = getLayoutResourceId();
        if (layoutResourceId != -1) {
            setContentView(layoutResourceId);
            ButterKnife.bind(this);
        }
        if (toolbar != null)
            setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();
        setUpDrawer();

        onPostInjection();
    }

    protected void onPostInjection() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @LayoutRes
    public int getLayoutResourceId() {
        return -1;
    }


    public void setFragment(@NonNull final BaseFragment fragment, final boolean ignoreBackstack, final TransactionAnimation animation) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(animation.getEnter(), animation.getExit(), animation.getPopEnter(), animation.getPopExit());
        if (!ignoreBackstack) {
            transaction.addToBackStack(fragment.getFragmentTag());
        }
        transaction.replace(R.id.content, fragment, fragment.getFragmentTag());
        transaction.commit();
    }

    private void setUpDrawer() {
        boolean authenticated = preferencesManager.get().userAuthentication.isAuthenticated();
        AccountHeader headerResult = getAccountHeader(authenticated);

        PrimaryDrawerItem caches_list = new PrimaryDrawerItem().withName(R.string.drawer_menu_item_caches).withIcon(GoogleMaterial.Icon.gmd_list).withTag(CachesActivity.getIntent(this));
        PrimaryDrawerItem logbook = new PrimaryDrawerItem().withName(R.string.drawer_menu_item_logbook).withIcon(GoogleMaterial.Icon.gmd_archive);
        PrimaryDrawerItem caches_map = new PrimaryDrawerItem().withName(R.string.drawer_menu_item_map).withIcon(GoogleMaterial.Icon.gmd_map).withTag(MapActivity.getIntent(this, null));
        PrimaryDrawerItem settings = new PrimaryDrawerItem().withName(R.string.drawer_menu_item_settings).withIcon(GoogleMaterial.Icon.gmd_settings);
        PrimaryDrawerItem help = new PrimaryDrawerItem().withName(R.string.drawer_menu_item_help).withIcon(GoogleMaterial.Icon.gmd_help);
        PrimaryDrawerItem contact = new PrimaryDrawerItem().withName(R.string.drawer_menu_item_contact).withIcon(GoogleMaterial.Icon.gmd_chat);
        DrawerBuilder builder = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withTranslucentNavigationBar(false)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        caches_list,
                        logbook,
                        caches_map,
                        new DividerDrawerItem(),
                        settings,
                        help,
                        contact
                )
                .withSelectedItemByPosition(1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (fragmentManager.getBackStackEntryCount() > 0) {
                            int id = fragmentManager.getBackStackEntryAt(0).getId();
                            fragmentManager.popBackStackImmediate(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        drawer.closeDrawer();
                        if (position == getMenuPosition())
                            return true;
                        Intent tag = (Intent) drawerItem.getTag();
                        if (tag != null)
                            startActivity(tag);
                        else {
                            Toast.makeText(BaseActivity.this, R.string.snack_coming_soon, Toast.LENGTH_SHORT).show();
                            drawer.setSelectionAtPosition(getMenuPosition(), false);
                        }
                        return true;
                    }
                });
        if (toolbar != null)
            builder.withToolbar(toolbar);
        drawer = builder.build();
        drawer.setSelectionAtPosition(getMenuPosition(), false);
    }

    private AccountHeader getAccountHeader(boolean authenticated) {
        return new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.account_background)
                    .addProfiles(
                            new ProfileDrawerItem().withName(authenticated ? preferencesManager.get().user.getUsername() : "").withEmail(authenticated ? getString(R.string.log_out) : getString(R.string.log_in))
                    )
                    .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                        @Override
                        public boolean onClick(View view, IProfile profile) {
                            boolean authenticated1 = preferencesManager.get().userAuthentication.isAuthenticated();
                            if (authenticated1) {
                                new AlertDialog.Builder(BaseActivity.this)
                                        .setTitle(getString(R.string.logout_dialog_title))
                                        .setMessage(getString(R.string.logout_dialog_message))
                                        .setPositiveButton(getString(R.string.logout_dialog_yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                bus.post(new ClearAuthorizationCredentialsCommand());
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.logout_dialog_no), null)
                                        .show();
                            } else {
                                new AlertDialog.Builder(BaseActivity.this)
                                        .setTitle(getString(R.string.login_dialog_title))
                                        .setMessage(getString(R.string.login_dialog_message))
                                        .setPositiveButton(getString(R.string.login_dialog_yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                bus.post(new StartOAuthCommand());
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.login_dialog_no), null)
                                        .show();
                            }
                            return true;
                        }
                    })
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                            if (currentProfile)
                                return true;
                            return false;
                        }
                    })
                    .build();
    }

    protected void showRationaleForGps(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_gps_rationale)
                .setPositiveButton(R.string.permission_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.permission_dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    protected void showDeniedForGps() {
        Toast.makeText(this, R.string.permission_gps_denied, Toast.LENGTH_LONG).show();
    }

    protected void showNeverAskForGps() {
        Toast.makeText(this, R.string.permission_gps_neverask, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(TransactionAnimation.FADE.getEnter(), TransactionAnimation.FADE.getExit());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(TransactionAnimation.FADE.getPopEnter(), TransactionAnimation.FADE.getPopExit());
    }

    protected void authenticationStateChanged(AuthenticationStateChangedEvent event) {
        if (drawer != null)
            drawer.closeDrawer();
        if (event.authenticated) {
            bus.post(new GetUserDetailsCommand());
        } else {
            setUpDrawer();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_name:
                bus.post(new ChangeListSortEvent(CacheModel.ORDER_BY_NAME));
                return true;
            case R.id.action_sort_distance:
                bus.post(new ChangeListSortEvent(CacheModel.ORDER_BY_DISTANCE));
                return true;
            case R.id.action_search:
                final SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        bus.post(new FilterCachesEvent(query));
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty())
                            bus.post(new FilterCachesEvent(newText));
                        return false;
                    }
                });
                searchView.setQueryHint(getString(R.string.search_hint));
                return true;
            case R.id.action_remove_all:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_caches_dialog_title)
                        .setMessage(R.string.delete_caches_dialog_message)
                        .setPositiveButton(R.string.delete_caches_dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bus.post(new RemoveAllCachesCommand());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.delete_caches_dialog_no, null)
                        .show();
                return true;
            case R.id.action_map:
                bus.post(new ShowCacheOnMapEvent());
                return true;
            case R.id.action_compass:
                bus.post(new ShowCacheCompassEvent());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void userDetailsFetched(ReceivedUserDetailsEvent event) {
        setUpDrawer();
    }

    public void setUpToolbar(SetUpToolbarEvent event) {
        toolbar.setTitle(event.title);
        toolbar.setSubtitle(event.subtitle);
        Menu menu = toolbar.getMenu();
        menu.clear();
        if (event.hasMenu)
            toolbar.inflateMenu(event.menu);
    }
}
