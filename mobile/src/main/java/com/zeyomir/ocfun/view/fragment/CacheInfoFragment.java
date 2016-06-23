package com.zeyomir.ocfun.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.event.ShowCacheCompassEvent;
import com.zeyomir.ocfun.eventbus.event.ShowCacheOnMapEvent;
import com.zeyomir.ocfun.storage.model.CacheModel;
import com.zeyomir.ocfun.utils.FuzzyTextGenerator;
import com.zeyomir.ocfun.view.activity.BaseActivity;
import com.zeyomir.ocfun.view.activity.MapActivity;
import com.zeyomir.ocfun.view.component.TransactionAnimation;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class CacheInfoFragment extends BaseFragment {
    private static final String CODE = "code";

    @BindView(R.id.lock)
    View lock;

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.owner)
    TextView owner;
    @BindView(R.id.code)
    TextView code;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.tagline)
    TextView shortDescription;
    @BindView(R.id.custom_note)
    TextView customNote;
    @BindView(R.id.custom_note_wrapper)
    View customNoteWrapper;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.bearing_ico)
    ImageView bearingIco;
    @BindView(R.id.bearing)
    TextView bearing;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.last_found)
    TextView lastFound;
    @BindView(R.id.founds)
    TextView founds;
    @BindView(R.id.not_founds)
    TextView notFounds;
    @BindView(R.id.size)
    TextView size;
    @BindView(R.id.difficulty)
    TextView difficulty;
    @BindView(R.id.terrain)
    TextView terrainDifficulty;
    @BindView(R.id.attributes)
    TextView attributes;
    @BindView(R.id.attributes_wrapper)
    View attributesWrapper;
    @BindView(R.id.trackables)
    TextView trackables;
    @BindView(R.id.trackables_wrapper)
    View trackablesWrapper;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.hint)
    TextView hint;
    @BindView(R.id.hint_wrapper)
    View hintWrapper;

    private String cacheCode;
    private CacheModel cacheModel;

    @Override
    protected void onPostInjection() {
        cacheCode = getArguments().getString(CODE);
        cacheModel = CacheModel.getByCacheCode(cacheCode);

        name.setText(cacheModel.name);
        lock.setVisibility(cacheModel.requiresPassword ? View.VISIBLE : View.GONE);
        icon.setImageResource(cacheModel.type.getDrawableId());
        code.setText(cacheModel.code);
        owner.setText(cacheModel.owner);
        shortDescription.setText(cacheModel.shortDescription);

        if (cacheModel.notes != null && !cacheModel.notes.trim().isEmpty()) {
            customNote.setText(cacheModel.notes);
        } else {
            customNoteWrapper.setVisibility(View.GONE);
        }

        location.setText(String.format(Locale.US, "%.6f, %.6f", cacheModel.latitude, cacheModel.longitude));
        FuzzyTextGenerator fuzzyTextGenerator = new FuzzyTextGenerator(getContext());
        distance.setText(fuzzyTextGenerator.forDistance(cacheModel.distance));
        bearing.setText(String.format("%d°", cacheModel.azimuth));
        bearingIco.setRotation(cacheModel.azimuth);

        lastFound.setText(cacheModel.lastFound);
        founds.setText(String.valueOf(cacheModel.founds));
        notFounds.setText(String.valueOf(cacheModel.notFounds));
        difficulty.setText(String.valueOf(cacheModel.difficulty));
        terrainDifficulty.setText(String.valueOf(cacheModel.terrain));
        size.setText(cacheModel.size.getName());

        if (cacheModel.trackables != null && !cacheModel.trackables.trim().isEmpty()) {
            trackables.setText(cacheModel.trackables.replaceAll(CacheModel.TOKENIZER, ", "));
        } else {
            trackablesWrapper.setVisibility(View.GONE);
        }

        if (cacheModel.attributes != null && !cacheModel.attributes.trim().isEmpty()) {
            attributes.setText(cacheModel.attributes.replaceAll(CacheModel.TOKENIZER, ", "));
        } else {
            attributesWrapper.setVisibility(View.GONE);
        }

        description.setText(cacheModel.description);

        if (cacheModel.hint != null && !cacheModel.hint.trim().isEmpty()) {
            hint.setText(rot13(cacheModel.hint));
        } else {
            hintWrapper.setVisibility(View.GONE);
        }
    }

    private String rot13(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'm')
                c += 13;
            else if (c >= 'n' && c <= 'z')
                c -= 13;
            else if (c >= 'A' && c <= 'M')
                c += 13;
            else if (c >= 'A' && c <= 'Z')
                c -= 13;
            sb.append(c);
        }
        return sb.toString();
    }

    @OnClick(R.id.hint_wrapper)
    public void hintClicked() {
        Toast.makeText(getContext(), cacheModel.hint, Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.lock)
    public void lockClicked() {
        Toast.makeText(getContext(), R.string.cache_info_password_needed, Toast.LENGTH_LONG).show();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.f_cache_info;
    }

    @Subscribe
    public void showOnMap(ShowCacheOnMapEvent event) {
        Intent intent = MapActivity.getIntent(getContext(), cacheCode);
        startActivity(intent);
    }

    @Subscribe
    public void showCompass(ShowCacheCompassEvent event) {
        ((BaseActivity) getActivity()).setFragment(CompassFragment.create(cacheModel.latitude, cacheModel.longitude, name.getText().toString()), false, TransactionAnimation.FADE);
    }

    public static CacheInfoFragment create(String code) {
        CacheInfoFragment fragment = new CacheInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CODE, code);
        fragment.setArguments(bundle);
        return fragment;
    }
}
