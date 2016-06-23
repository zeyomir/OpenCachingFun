package com.zeyomir.ocfun.eventbus.command.api;

public class GetCacheDetailsCommand extends ApiCommand {
    public final String cacheCode;

    public GetCacheDetailsCommand(String cacheCode) {
        this.cacheCode = cacheCode;
    }
}
