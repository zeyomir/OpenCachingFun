package com.zeyomir.ocfun.eventbus.command.db;

public class SaveAuthorizationCredentialsCommand extends DbCommand {
    public final String token;
    public final String tokenSecret;

    public SaveAuthorizationCredentialsCommand(String token, String tokenSecret) {
        this.token = token;
        this.tokenSecret = tokenSecret;
    }
}
