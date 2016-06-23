package com.zeyomir.ocfun.eventbus.command.db;

import java.util.List;

public class ScheduleCachesToDownloadCommand extends DbCommand {
    public final List<String> results;

    public ScheduleCachesToDownloadCommand(List<String> results) {
        this.results = results;
    }
}
