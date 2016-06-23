package com.zeyomir.ocfun.eventbus.command.api;

public class SubmitPersonalNoteCommand extends ApiCommand {
    public final String cacheCode;
    public final String newValue;
    public final String oldValue;

    public SubmitPersonalNoteCommand(String cacheCode, String newValue, String oldValue) {
        this.cacheCode = cacheCode;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }
}
