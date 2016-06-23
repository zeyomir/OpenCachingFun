package com.zeyomir.ocfun.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.command.api.GetCachesByLocationCommand;
import com.zeyomir.ocfun.eventbus.command.api.GetCachesByNameCommand;
import com.zeyomir.ocfun.eventbus.command.db.ScheduleCachesToDownloadCommand;
import com.zeyomir.ocfun.eventbus.command.sensors.GetLastKnownLocationCommand;
import com.zeyomir.ocfun.eventbus.command.sensors.SetLocationUpdatesCommand;
import com.zeyomir.ocfun.eventbus.event.CacheSavedEvent;
import com.zeyomir.ocfun.eventbus.event.LastLocationReceivedEvent;
import com.zeyomir.ocfun.eventbus.event.LocationUpdateEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedCacheLocationEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedCachesListEvent;
import com.zeyomir.ocfun.storage.model.CacheModel;

import java.util.Date;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

@RuntimePermissions
public class TestActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text)
    TextView text;

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void getLocation(){
        bus.post(new GetLastKnownLocationCommand());
//        bus.post(new SetLocationUpdatesCommand(SetLocationUpdatesCommand.RequestType.CONTINUOUS));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TestActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForPhoneCall(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_gps_rationale)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("NOPE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    // Annotate a method which is invoked if the user doesn't grant the permissions
    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForPhoneCall() {
        Toast.makeText(this, R.string.permission_gps_denied, Toast.LENGTH_SHORT).show();
    }

    // Annotates a method which is invoked if the user
    // chose to have the device "never ask again" about a permission
    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForPhoneCall() {
        Toast.makeText(this, R.string.permission_gps_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected byte getMenuPosition() {
        return NOT_IN_MENU;
    }

    @Override
    protected void onPostInjection() {


        setSupportActionBar(toolbar);
//        setUpDrawer();

//        bus.post(new GetCacheLocationCommand("OP8D43"));
/*
        String foundStatus = preferencesManager.get().settings.getSkipFound() ? "notfound_only" : null;
        Boolean skipOwn = preferencesManager.get().settings.getSkipOwn() ? true : null;
        String ignoredStatus = preferencesManager.get().userAuthentication.isAuthenticated() ? "notignored_only" : null;
        bus.post(new GetCachesByNameCommand("LIM*", foundStatus, skipOwn, ignoredStatus));*/
//        TestActivityPermissionsDispatcher.getLocationWithCheck(this);
//        bus.post(new ScheduleCachesToDownloadCommand(Collections.singletonList("OP850H")));
    }

    /*private void setUpDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.ic_launcher)
                .addProfiles(
                        new ProfileDrawerItem().withName("zeyomir").withEmail("opencaching.pl").withIcon(getResources().getDrawable(R.drawable.ic_notification)),
                        new ProfileDrawerItem().withName("hans").withEmail("opencaching.de").withIcon(getResources().getDrawable(R.drawable.ic_notification)),
                        new ProfileDrawerItem().withName("Log In!").withEmail("opencaching.co.uk").withIcon(getResources().getDrawable(R.drawable.ic_notification))
                )
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile profile) {
                        if (profile.getName().toString().equals("Log In!")) {
                            Toast.makeText(TestActivity.this, "Open settings to log in!", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if (currentProfile)
                            return true;

                        Toast.makeText(TestActivity.this, profile.getName() + " | " + profile.getEmail(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem caches_list = new PrimaryDrawerItem().withName("Lista skrzynek").withIcon(GoogleMaterial.Icon.gmd_list);
        PrimaryDrawerItem caches_map = new PrimaryDrawerItem().withName("Mapa").withIcon(GoogleMaterial.Icon.gmd_map);
        PrimaryDrawerItem logbook = new PrimaryDrawerItem().withName("Logbook").withIcon(GoogleMaterial.Icon.gmd_archive);
        PrimaryDrawerItem settings = new PrimaryDrawerItem().withName("Ustawienia").withIcon(GoogleMaterial.Icon.gmd_settings);
        PrimaryDrawerItem help = new PrimaryDrawerItem().withName("Pomoc").withIcon(GoogleMaterial.Icon.gmd_help);
        PrimaryDrawerItem contact = new PrimaryDrawerItem().withName("Kontakt").withIcon(GoogleMaterial.Icon.gmd_speaker);
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withTranslucentNavigationBar(false)
                .withToolbar(toolbar)
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
                        toolbar.setTitle(getTitleForMenuItem(position));
                        drawer.closeDrawer();
//                        Toast.makeText(TestActivity.this, "ping", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .build();



        drawer.openDrawer();
    }*/

    private String getTitleForMenuItem(int position) {
        switch (position){
            default:
            case 1: return "Skrzynki";
            case 2: return "Logbook";
            case 3: return "Mapa";
            case 5: return "Ustawienia";
            case 6: return "Pomoc";
            case 7: return "Kontakt";
        }
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.a_map;
    }

    @Subscribe
    public void lastLocationReceived(LastLocationReceivedEvent event) {
        Location lastLocation = event.lastLocation;
        if (lastLocation == null) {
            new AlertDialog.Builder(this)
                    .setMessage("No last location found... want to wait for a new one?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            bus.post(new SetLocationUpdatesCommand(SetLocationUpdatesCommand.RequestType.SINGLE));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
            Timber.d("No last location found... want to wait for a new one?");
        } else
            updateLocation(lastLocation);
    }

    @Subscribe
    public void locationUpdateReceived(LocationUpdateEvent event) {
        Location location = event.location;
        updateLocation(location);
    }

    private void updateLocation(Location location) {
        String string = String.format("lat %f long %f acc %f date %s", location.getLatitude(), location.getLongitude(), location.getAccuracy(), new Date(location.getTime()).toString());

        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
        Timber.d(string);

    }

    @Subscribe
    public void cacheDetailsReceived(ReceivedCacheLocationEvent event) {
      /*  String foundStatus = preferencesManager.get().settings.getSkipFound() ? "notfound_only" : null;
        Boolean skipOwn = preferencesManager.get().settings.getSkipOwn() ? true : null;
        String ignoredStatus = preferencesManager.get().userAuthentication.isAuthenticated() ? "notignored_only" : null;

        String[] strings = event.location.split("\\|");
        bus.post(new GetCachesByLocationCommand(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]), 20f, foundStatus, skipOwn, ignoredStatus));*/
    }

    @Subscribe
    public void cacheSearchResult(ReceivedCachesListEvent event) {
        bus.post(new ScheduleCachesToDownloadCommand(event.searchResult.results));
    }

    @Override
    public void onBackPressed() {
       /* if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }*/
        super.onBackPressed();
    }


    @Subscribe
    public void cacheSaved(CacheSavedEvent event) {
        CacheModel cache = CacheModel.getByCacheCode(event.code);
        text.setText(cache.description);
    }
}
