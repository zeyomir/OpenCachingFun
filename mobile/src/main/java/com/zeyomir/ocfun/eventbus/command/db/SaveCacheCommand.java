package com.zeyomir.ocfun.eventbus.command.db;

import com.zeyomir.ocfun.network.response.CacheData;

public class SaveCacheCommand extends DbCommand {
    public final CacheData cacheData;

    public SaveCacheCommand(CacheData cacheData) {
        this.cacheData = cacheData;
    }
}
