package com.zeyomir.ocfun.eventbus.command.db;

import com.zeyomir.ocfun.network.response.UserDetailsResponse;

public class SaveUserDetailsCommand extends DbCommand{
    public final UserDetailsResponse userDetails;

    public SaveUserDetailsCommand(UserDetailsResponse userDetails) {
        this.userDetails = userDetails;
    }
}
