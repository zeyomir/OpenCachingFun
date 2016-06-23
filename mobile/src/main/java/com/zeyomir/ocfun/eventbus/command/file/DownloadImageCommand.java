package com.zeyomir.ocfun.eventbus.command.file;

import java.io.File;

public class DownloadImageCommand extends FileTransferCommand {
    public final String name;
    public final String cacheCode;
    public final File cacheFile;

    public DownloadImageCommand(String name, String cacheCode, File cacheFile) {
        this.name = name;
        this.cacheCode = cacheCode;
        this.cacheFile = cacheFile;
    }
}
