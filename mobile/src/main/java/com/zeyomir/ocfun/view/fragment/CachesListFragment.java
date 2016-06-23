package com.zeyomir.ocfun.view.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.command.api.GetCachesByLocationCommand;
import com.zeyomir.ocfun.eventbus.command.api.GetCachesByNameCommand;
import com.zeyomir.ocfun.eventbus.command.api.GetWatchedCachesCommand;
import com.zeyomir.ocfun.eventbus.command.db.ComputeDistanceToCachesCommand;
import com.zeyomir.ocfun.eventbus.command.db.ScheduleCachesToDownloadCommand;
import com.zeyomir.ocfun.eventbus.command.sensors.GetLastKnownLocationCommand;
import com.zeyomir.ocfun.eventbus.command.sensors.SetLocationUpdatesCommand;
import com.zeyomir.ocfun.eventbus.event.AuthenticationStateChangedEvent;
import com.zeyomir.ocfun.eventbus.event.CachesListChangedEvent;
import com.zeyomir.ocfun.eventbus.event.ChangeListSortEvent;
import com.zeyomir.ocfun.eventbus.event.DistanceComputedEvent;
import com.zeyomir.ocfun.eventbus.event.FilterCachesEvent;
import com.zeyomir.ocfun.eventbus.event.LastLocationReceivedEvent;
import com.zeyomir.ocfun.eventbus.event.LocationUpdateEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedCachesListEvent;
import com.zeyomir.ocfun.eventbus.event.SetUpToolbarEvent;
import com.zeyomir.ocfun.eventbus.event.ShowCacheDetailsEvent;
import com.zeyomir.ocfun.storage.model.CacheModel;
import com.zeyomir.ocfun.utils.FuzzyTextGenerator;
import com.zeyomir.ocfun.view.component.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.codetail.animation.ViewAnimationUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import snow.skittles.BaseSkittle;
import snow.skittles.SkittleBuilder;
import snow.skittles.SkittleLayout;
import snow.skittles.TextSkittle;

@RuntimePermissions
public class CachesListFragment extends BaseFragment {
    public static final int GPS_SKITTLE_ID = 0;
    public static final int NAME_SKITTLE_ID = 1;
    public static final int WATCHED_SKITTLE_ID = 2;
    @BindView(R.id.empty)
    View empty;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.skittle)
    SkittleLayout skittleLayout;
    @BindView(R.id.over)
    View over;

    private List<CacheModel> caches = new ArrayList<>();
    private CachesAdapter adapter;
    private String orderBy = CacheModel.ORDER_BY_NAME;
    private String filter = "";
    private boolean fabExpanded = false;
    private SkittleBuilder skittleBuilder;
    private Location lastLocation = null;
    private FuzzyTextGenerator fuzzyTextGenerator;

    @Override
    public int getLayoutResourceId() {
        return R.layout.f_caches_list;
    }

    @Override
    protected void onPostInjection() {
        super.onPostInjection();
        setUpSkittles();

        Context context = getContext();
        fuzzyTextGenerator = new FuzzyTextGenerator(context);

        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setItemAnimator(new DefaultItemAnimator());
        list.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        adapter = new CachesAdapter();
        list.setAdapter(adapter);
        orderBy = preferencesManager.get().settings.getCachedOrderMethod();
        refreshList();
    }

    private void setUpSkittles() {
        skittleBuilder = SkittleBuilder.newInstance(skittleLayout, true);

        final Context context = getContext();
        skittleBuilder.changeMainSkittleColor(ContextCompat.getColor(context, R.color.fab_background));
        skittleBuilder.changeMainSkittleIcon(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_add).color(ContextCompat.getColor(context, R.color.fab_icon)).sizeDp(24));

        IconicsDrawable gpsIcon = new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_my_location).color(ContextCompat.getColor(context, R.color.fab_icon)).sizeDp(24);
        IconicsDrawable pencilIcon = new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_edit).color(ContextCompat.getColor(context, R.color.fab_icon)).sizeDp(24);
        IconicsDrawable eyeIcon = new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_visibility).color(ContextCompat.getColor(context, R.color.fab_icon)).sizeDp(24);
        skittleBuilder.addSkittle(
                new TextSkittle.Builder(getString(R.string.skittle_gps),
                        ContextCompat.getColor(context, R.color.fab_background),
                        gpsIcon)
                        .setTextBackground(ContextCompat.getColor(context, R.color.window))
                        .build());
        skittleBuilder.addSkittle(
                new TextSkittle.Builder(getString(R.string.skittle_name),
                        ContextCompat.getColor(context, R.color.fab_background),
                        pencilIcon)
                        .setTextBackground(ContextCompat.getColor(context, R.color.window))
                        .build());
        if (preferencesManager.get().userAuthentication.isAuthenticated()) {
            skittleBuilder.addSkittle(
                    new TextSkittle.Builder(getString(R.string.skittle_watched),
                            ContextCompat.getColor(context, R.color.fab_background),
                            eyeIcon)
                            .setTextBackground(ContextCompat.getColor(context, R.color.window))
                            .build());
        }
        skittleBuilder.setSkittleClickListener(new SkittleBuilder.OnSkittleClickListener() {
            @Override
            public void onSkittleClick(BaseSkittle skittle, int position) {
                skittleBuilder.onSkittleContainerClick();

                final boolean skipFound = preferencesManager.get().settings.getSkipFound();
                final boolean skipOwn = preferencesManager.get().settings.getSkipOwn();
                final boolean skipIgnored = preferencesManager.get().userAuthentication.isAuthenticated();

                switch (position) {
                    case GPS_SKITTLE_ID:
                        if (lastLocation == null) {
                            showSnack(context, R.string.snack_no_last_location);
                        } else
                            bus.post(new GetCachesByLocationCommand((float) lastLocation.getLatitude(), (float) lastLocation.getLongitude(), 2, skipFound, skipOwn, skipIgnored));
                        break;
                    case NAME_SKITTLE_ID:
                        final EditText editText = new EditText(context);
                        editText.setSingleLine(true);
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.dialog_add_caches_by_name_title))
                                .setMessage(getString(R.string.dialog_add_caches_by_name_message))
                                .setView(editText)
                                .setPositiveButton(getString(R.string.dialog_add_caches_by_name_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        bus.post(new GetCachesByNameCommand(String.format("*%s*", editText.getText().toString()), skipFound, skipOwn, skipIgnored));
                                    }
                                })
                                .setNeutralButton(getString(R.string.dialog_add_caches_by_name_cancel), null)
                                .create();
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        dialog.show();
                        break;
                    case WATCHED_SKITTLE_ID:
                        bus.post(new GetWatchedCachesCommand());
                        break;
                }
            }

            @Override
            public void onMainSkittleClick() {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int startRadius = 0;
                int finalRadius = Math.max(size.x, size.y);
                Animator animator;
                if (fabExpanded) {
                    skittleBuilder.changeMainSkittleIcon(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_add).color(ContextCompat.getColor(context, R.color.fab_icon)).sizeDp(24));
                    animator = ViewAnimationUtils.createCircularReveal(over, (int) (size.x * 0.9f), (int) (size.y * 0.9f), finalRadius, startRadius);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            over.setVisibility(View.GONE);
                        }
                    });
                } else {
                    skittleBuilder.changeMainSkittleIcon(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_clear).color(ContextCompat.getColor(context, R.color.fab_icon)).sizeDp(24));
                    over.setVisibility(View.VISIBLE);
                    animator = ViewAnimationUtils.createCircularReveal(over, (int) (size.x * 0.9f), (int) (size.y * 0.9f), startRadius, finalRadius);
                }

                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(300);
                animator.start();

                fabExpanded = !fabExpanded;
            }
        });
    }

    private void showSnack(Context context, @StringRes int stringId) {
        Snackbar snackbar = Snackbar.make(skittleLayout.getSkittleContainer(), stringId, Snackbar.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(context, R.color.accent));
        snackbar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new SetUpToolbarEvent(getString(R.string.toolbar_title_caches), "", R.menu.caches_list, true));
        CachesListFragmentPermissionsDispatcher.getLocationWithCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CachesListFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnClick(R.id.over)
    public void overlayClicked() {
        skittleBuilder.onSkittleContainerClick();
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void getLocation() {
        bus.post(new GetLastKnownLocationCommand());
    }

    @Subscribe
    public void lastLocationFound(final LastLocationReceivedEvent event) {
        if (event.lastLocation == null)
            return;
        long timeDelta = System.currentTimeMillis() - event.lastLocation.getTime();
        if (timeDelta > 60_000) {
            Context context = getContext();
            String text = fuzzyTextGenerator.forElapsedTime(timeDelta);
            new AlertDialog.Builder(context)
                    .setTitle(getString(R.string.dialog_old_gps_title))
                    .setMessage(getString(R.string.dialog_old_gps_message, text))
                    .setPositiveButton(getString(R.string.dialog_old_gps_use_old), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lastLocation = event.lastLocation;
                            bus.post(new ComputeDistanceToCachesCommand(event.lastLocation));
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_old_gps_wait), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bus.post(new SetLocationUpdatesCommand(SetLocationUpdatesCommand.RequestType.SINGLE));
                        }
                    })
                    .show();
        } else {
            lastLocation = event.lastLocation;
            bus.post(new ComputeDistanceToCachesCommand(event.lastLocation));
        }
    }

    @Subscribe
    public void locationFound(LocationUpdateEvent event) {
        if (event.location == null)
            return;
        lastLocation = event.location;
        bus.post(new ComputeDistanceToCachesCommand(event.location));
    }

    @Subscribe
    public void computed(DistanceComputedEvent event) {
        refreshList();
    }

    @Subscribe
    public void authenticationStateChanged(AuthenticationStateChangedEvent event) {
        setUpSkittles();
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

    private void refreshList() {
        if (CacheModel.count() > 0) {
            empty.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            caches.clear();
            caches.addAll(CacheModel.getForList(orderBy, filter));
            adapter.notifyDataSetChanged();
        } else {
            empty.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void changeSortOrder(ChangeListSortEvent event) {
        orderBy = event.sortBy;
        preferencesManager.get().settings.setCachesOrderMethod(orderBy);
        refreshList();
    }

    @Subscribe
    public void filter(FilterCachesEvent event) {
        filter = event.filter;
        refreshList();
    }

    @Subscribe
    public void cacheSearchResult(ReceivedCachesListEvent event) {
        if (event.searchResult.results.isEmpty()) {
            showSnack(getContext(), R.string.no_matching_caches);
            return;
        }
        bus.post(new ScheduleCachesToDownloadCommand(event.searchResult.results));
    }

    @Subscribe
    public void cacheListChanged(CachesListChangedEvent event) {
        refreshList();
    }

    public static CachesListFragment create() {
        return new CachesListFragment();
    }

    public class CachesAdapter extends RecyclerView.Adapter<CachesViewHolder> {
        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CachesViewHolder child = (CachesViewHolder) list.getChildViewHolder(v);
                skittleBuilder.onSkittleContainerClick();
                bus.post(new ShowCacheDetailsEvent(child.code, child.name.getText().toString()));
            }
        };

        @Override
        public CachesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflated = LayoutInflater.from(getContext()).inflate(R.layout.i_cache, parent, false);
            CachesViewHolder viewHolder = new CachesViewHolder(inflated);
            inflated.setOnClickListener(clickListener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CachesViewHolder holder, int position) {
            CacheModel cacheModel = caches.get(position);
            holder.code = cacheModel.code;
            holder.name.setText(cacheModel.name);
            holder.tagline.setText(cacheModel.shortDescription);
            holder.icon.setImageResource(cacheModel.type.getDrawableId());
            if (cacheModel.distance >= 0) {
                holder.distance.setVisibility(View.VISIBLE);
                holder.distance.setText(fuzzyTextGenerator.forDistance(cacheModel.distance));
                holder.bearing.setVisibility(View.VISIBLE);
                holder.bearing.setRotation(cacheModel.azimuth);
            } else {
                holder.bearing.setVisibility(View.INVISIBLE);
                holder.distance.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return caches.size();
        }
    }

    public class CachesViewHolder extends RecyclerView.ViewHolder {
        String code;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.tagline)
        TextView tagline;
        @BindView(R.id.distance)
        TextView distance;
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.bearing)
        ImageView bearing;

        public CachesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
