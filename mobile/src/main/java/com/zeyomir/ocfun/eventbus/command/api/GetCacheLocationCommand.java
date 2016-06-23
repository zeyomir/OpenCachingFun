package com.zeyomir.ocfun.eventbus.command.api;

public class GetCacheLocationCommand extends ApiCommand {
    public final String cacheCode;

    public GetCacheLocationCommand(String cacheCode) {
        this.cacheCode = cacheCode;
    }
}
