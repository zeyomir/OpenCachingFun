package com.zeyomir.ocfun.eventbus.event;

import com.zeyomir.ocfun.eventbus.command.api.SubmitLogCommand;
import com.zeyomir.ocfun.network.response.LogSubmissionResponse;

public class LogSubmittedEvent extends Event {
    public final SubmitLogCommand command;
    public final LogSubmissionResponse logSubmissionResponse;

    public LogSubmittedEvent(SubmitLogCommand command, LogSubmissionResponse logSubmissionResponse) {
        this.command = command;
        this.logSubmissionResponse = logSubmissionResponse;
    }
}
