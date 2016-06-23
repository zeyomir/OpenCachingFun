package com.zeyomir.ocfun.eventbus.command.sensors;

public class SetLocationUpdatesCommand extends SensorsCommand {
    public final RequestType requestType;

    public SetLocationUpdatesCommand(RequestType requestType) {
        this.requestType = requestType;
    }

    public enum RequestType{
        SINGLE,
        CONTINUOUS,
        STOP
    }
}
