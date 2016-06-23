package com.zeyomir.ocfun.eventbus.command.api;

public class GetCachesByNameCommand extends GetCachesCommand {
    public final String name;
    public final String foundStatus;
    public final Boolean skipOwn;
    public final String ignoredStatus;

    public GetCachesByNameCommand(String name, boolean skipFound, boolean skipOwn, boolean skipIgnored) {
        this.name = name;
        this.foundStatus = getFoundStatus(skipFound);
        this.skipOwn = getSkipOwn(skipOwn);
        this.ignoredStatus = getIgnoredStatus(skipIgnored);
    }
}
