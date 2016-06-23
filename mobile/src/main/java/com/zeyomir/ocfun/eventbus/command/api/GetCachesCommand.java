package com.zeyomir.ocfun.eventbus.command.api;

public abstract class GetCachesCommand extends ApiCommand {
    protected String getFoundStatus(boolean preferencesSkipFound){
        return preferencesSkipFound ? "notfound_only" : null;
    }
    protected Boolean getSkipOwn(boolean preferencesSkipOwn){
        return preferencesSkipOwn ? true : null;
    }
    protected String getIgnoredStatus(boolean preferencesSkipIgnored) {
        return preferencesSkipIgnored ? "notignored_only" : null;
    }
}
