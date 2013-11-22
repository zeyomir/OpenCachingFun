package com.zeyomir.ocfun.controller.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static PropertiesLoader instance;

    public final String applicationVersionName;
    public final String okapiKey;
    public final String okapiSecret;


    private PropertiesLoader(){
        Properties properties = loadProperties();
        if (properties == null) {
            this.applicationVersionName = "";
            this.okapiKey = "";
            this.okapiSecret = "";
        } else {
            this.applicationVersionName = properties.getProperty("app.versionName");
            this.okapiKey = properties.getProperty("okapi.oauthKey");
            this.okapiSecret = properties.getProperty("okapi.oauthSecret");
        }
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("application.properties");
        try {
            properties.load(stream);
        } catch (IOException e) {
            properties = null;
            e.printStackTrace();
        }
        return properties;
    }

    public static PropertiesLoader get() {
        if (instance == null)
            instance = new PropertiesLoader();
        return instance;
    }
}
