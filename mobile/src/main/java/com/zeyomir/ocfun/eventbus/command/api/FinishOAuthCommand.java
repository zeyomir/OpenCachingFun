package com.zeyomir.ocfun.eventbus.command.api;

public class FinishOAuthCommand extends ApiCommand {
    public final String verifier;

    public FinishOAuthCommand(String verifier) {
        this.verifier = verifier;
    }
}
