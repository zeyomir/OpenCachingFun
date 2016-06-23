package com.zeyomir.ocfun.storage;

import android.support.annotation.NonNull;

import java.io.File;

public class FileManager {
    private final File directory;

    public FileManager(File directory) {
        this.directory = directory;
    }

    @NonNull
    public File getImageFile(String code, String fileName) {
        File dirForCode = new File(directory, code);
        dirForCode.mkdir();

        return new File(dirForCode, fileName);
    }

    public void removeImages(String code) {
        File dirForCode = new File(directory, code);
        if (!dirForCode.exists())
            return;
        for (File file : dirForCode.listFiles())
            file.delete();
        dirForCode.delete();
    }

    public void removeImage(String code, String name) {
        File file = new File(directory, code + File.pathSeparator + name);
        file.delete();
    }
}
