package com.zeyomir.ocfun.network.response;

import android.support.annotation.StringRes;

import com.zeyomir.ocfun.R;

public enum ContainerSize {
    none(R.string.cache_size_none),
    nano(R.string.cache_size_nano),
    micro(R.string.cache_size_micro),
    small(R.string.cache_size_small),
    regular(R.string.cache_size_regular),
    large(R.string.cache_size_large),
    xlarge(R.string.cache_size_xlarge),
    other(R.string.cache_size_other);

    @StringRes
    private final int name;

    ContainerSize(int name) {
        this.name = name;
    }

    @StringRes
    public int getName() {
        return name;
    }
}
