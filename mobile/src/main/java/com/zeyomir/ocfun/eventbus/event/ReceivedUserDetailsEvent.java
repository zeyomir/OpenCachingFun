package com.zeyomir.ocfun.eventbus.event;

import com.zeyomir.ocfun.network.response.UserDetailsResponse;

public class ReceivedUserDetailsEvent extends Event {
    public final UserDetailsResponse userDetailsResponse;

    public ReceivedUserDetailsEvent(UserDetailsResponse userDetailsResponse) {
        this.userDetailsResponse = userDetailsResponse;
    }
}
