package com.zeyomir.ocfun.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.configuration.OCFunApplication;
import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.storage.FileManager;
import com.zeyomir.ocfun.storage.PreferencesManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.Lazy;
import permissions.dispatcher.PermissionRequest;

public abstract class BaseFragment extends Fragment {
    @Inject
    protected EventBus bus;
    @Inject
    protected Lazy<PreferencesManager> preferencesManager;
    @Inject
    protected Lazy<FileManager> fileManager;

    protected FragmentManager fragmentManager;
    protected LayoutInflater layoutInflater;
    protected View mainView;
    private Unbinder unbinder;


    @Override
    final public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((OCFunApplication) getActivity().getApplication()).getInjector().inject(this);
    }


    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;
        fragmentManager = getFragmentManager();

        int layoutResourceId = getLayoutResourceId();
        if (layoutResourceId != -1) {
            mainView = inflater.inflate(layoutResourceId, container, false);
            unbinder = ButterKnife.bind(this, mainView);
        }

        onPostInjection();
        return mainView;
    }

    protected void onPostInjection() {
    }

    @Override
    public final void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    @NonNull
    public String getFragmentTag() {
        return this.getClass().getSimpleName();
    }


    @LayoutRes
    public int getLayoutResourceId() {
        return -1;
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    protected void showRationaleForGps(final PermissionRequest request) {
        new AlertDialog.Builder(getActivity())
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
        Toast.makeText(getActivity(), R.string.permission_gps_denied, Toast.LENGTH_LONG).show();
    }

    protected void showNeverAskForGps() {
        Toast.makeText(getActivity(), R.string.permission_gps_neverask, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startActivity(Intent intent) {
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ((OCFunApplication) getActivity().getApplication()).getRefWatcher();
        if (refWatcher != null)
            refWatcher.watch(this);
    }
}
