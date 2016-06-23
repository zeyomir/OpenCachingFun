package com.zeyomir.ocfun.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.command.sensors.GetLastKnownLocationCommand;
import com.zeyomir.ocfun.eventbus.event.AuthenticationStateChangedEvent;
import com.zeyomir.ocfun.eventbus.event.LastLocationReceivedEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedUserDetailsEvent;
import com.zeyomir.ocfun.storage.model.CacheModel;
import com.zeyomir.ocfun.view.component.TransactionAnimation;
import com.zeyomir.ocfun.view.fragment.CacheDetailsFragment;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

@RuntimePermissions
public class MapActivity extends BaseActivity implements OnMapReadyCallback, ClusterManager.OnClusterItemInfoWindowClickListener<MapActivity.CacheMapItem>, ClusterManager.OnClusterInfoWindowClickListener<MapActivity.CacheMapItem>, ClusterManager.OnClusterItemClickListener<MapActivity.CacheMapItem>, ClusterManager.OnClusterClickListener<MapActivity.CacheMapItem> {
    private static final String ZOOM_TARGET = "target";
    public static final String USER_ZOOM_TARGET = "user";
    private GoogleMap map;
    private ClusterManager<CacheMapItem> clusterManager;
    private String zoomTarget;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void getLocation() {
        map.setMyLocationEnabled(true);
        if (USER_ZOOM_TARGET.equals(zoomTarget))
            bus.post(new GetLastKnownLocationCommand());
    }

    @Subscribe
    public void lastLocationFound(LastLocationReceivedEvent event) {
        if (event.lastLocation == null)
            return;
        LatLng latLng = new LatLng(event.lastLocation.getLatitude(), event.lastLocation.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void showRationaleForGps(final PermissionRequest request) {
        super.showRationaleForGps(request);
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void showDeniedForGps() {
        super.showDeniedForGps();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void showNeverAskForGps() {
        super.showNeverAskForGps();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.a_map;
    }

    @Override
    protected byte getMenuPosition() {
        return MAP_MENU_POSITION;
    }

    @Override
    protected void onPostInjection() {
        super.onPostInjection();
        zoomTarget = getIntent().getExtras().getString(ZOOM_TARGET);
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.replace(R.id.content, supportMapFragment, "Map");
        transaction.commit();

        supportMapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawer.setSelectionAtPosition(getMenuPosition(), false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (map != null) {
            return;
        }
        map = googleMap;
        MapActivityPermissionsDispatcher.getLocationWithCheck(this);
        setUpMap();
        loadCaches();
    }

    private void setUpMap() {
        clusterManager = new ClusterManager<>(this, map);
        clusterManager.setRenderer(new CacheMapItemRenderer());
        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);

        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterInfoWindowClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
    }

    private void loadCaches() {
        List<CacheModel> cacheModels = CacheModel.getForList(CacheModel.ORDER_BY_DISTANCE, null);
        for (CacheModel model : cacheModels) {
            if (zoomTarget.equals(model.code))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(model.latitude, model.longitude), 15));
            clusterManager.addItem(new CacheMapItem(model.name, model.code, model.type.getDrawableId(), new LatLng(model.latitude, model.longitude)));
        }
        clusterManager.cluster();
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

    @Override
    public void onClusterItemInfoWindowClick(CacheMapItem cacheMapItem) {

    }

    @Override
    public void onClusterInfoWindowClick(Cluster<CacheMapItem> cluster) {

    }

    @Override
    public boolean onClusterItemClick(CacheMapItem cacheMapItem) {
        setFragment(CacheDetailsFragment.create(cacheMapItem.code, cacheMapItem.name), false, TransactionAnimation.SLIDE);
        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<CacheMapItem> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }

        final LatLngBounds bounds = builder.build();

        try {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            Timber.w(e, "Couldn't animate to bounds %s", bounds.toString());
        }
        return true;
    }

    class CacheMapItem implements ClusterItem {
        public final String name;
        public final String code;
        @DrawableRes
        public final int type;
        private final LatLng position;

        CacheMapItem(String name, String code, @DrawableRes int type, LatLng position) {
            this.name = name;
            this.code = code;
            this.type = type;
            this.position = position;
        }


        @Override
        public LatLng getPosition() {
            return position;
        }
    }

    private class CacheMapItemRenderer extends DefaultClusterRenderer<CacheMapItem> {
        private final IconGenerator iconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView markerImageView;
        private final TextView markerTextView;

        public CacheMapItemRenderer() {
            super(getApplicationContext(), map, clusterManager);

            View myMarker = getLayoutInflater().inflate(R.layout.i_cache_on_map, null);
            iconGenerator.setContentView(myMarker);
            markerImageView = (ImageView) myMarker.findViewById(R.id.image);
            markerTextView = (TextView) myMarker.findViewById(R.id.text);
        }

        @Override
        protected void onBeforeClusterItemRendered(CacheMapItem cacheMapItem, MarkerOptions markerOptions) {
            markerImageView.setImageResource(cacheMapItem.type);
            markerTextView.setText(cacheMapItem.name);
            Bitmap icon = iconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<CacheMapItem> cluster) {
            return cluster.getSize() > 2;
        }
    }

    public static Intent getIntent(Context context, String zoomTarget) {
        Intent intent = new Intent(context, MapActivity.class);
        if (zoomTarget == null)
            zoomTarget = USER_ZOOM_TARGET;
        intent.putExtra(ZOOM_TARGET, zoomTarget);
        return intent;
    }
}
