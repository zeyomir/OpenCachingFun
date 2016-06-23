package com.zeyomir.ocfun.storage;

import android.location.Location;
import android.support.annotation.NonNull;
import android.text.Html;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Subscribe;
import com.zeyomir.ocfun.eventbus.EventBus;
import com.zeyomir.ocfun.eventbus.command.db.ClearAuthorizationCredentialsCommand;
import com.zeyomir.ocfun.eventbus.command.db.ComputeDistanceToCachesCommand;
import com.zeyomir.ocfun.eventbus.command.db.RemoveAllCachesCommand;
import com.zeyomir.ocfun.eventbus.command.db.SaveAuthorizationCredentialsCommand;
import com.zeyomir.ocfun.eventbus.command.db.SaveCacheCommand;
import com.zeyomir.ocfun.eventbus.command.db.SaveUserDetailsCommand;
import com.zeyomir.ocfun.eventbus.command.db.ScheduleCachesToDownloadCommand;
import com.zeyomir.ocfun.eventbus.event.AuthenticationStateChangedEvent;
import com.zeyomir.ocfun.eventbus.event.CacheSavedEvent;
import com.zeyomir.ocfun.eventbus.event.CachesListChangedEvent;
import com.zeyomir.ocfun.eventbus.event.DistanceComputedEvent;
import com.zeyomir.ocfun.eventbus.event.ReceivedUserDetailsEvent;
import com.zeyomir.ocfun.eventbus.event.ScheduldedToDownloadEvent;
import com.zeyomir.ocfun.network.response.AlternateWaypointData;
import com.zeyomir.ocfun.network.response.CacheData;
import com.zeyomir.ocfun.network.response.ImageData;
import com.zeyomir.ocfun.network.response.LogData;
import com.zeyomir.ocfun.storage.model.CacheModel;
import com.zeyomir.ocfun.storage.model.CacheToDownloadModel;
import com.zeyomir.ocfun.storage.model.CacheType;
import com.zeyomir.ocfun.storage.model.ImageModel;
import com.zeyomir.ocfun.storage.model.LogModel;
import com.zeyomir.ocfun.storage.model.LogType;
import com.zeyomir.ocfun.storage.model.WaypointModel;
import com.zeyomir.ocfun.utils.AzimuthSanitizer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.turri.jiso8601.Iso8601Deserializer;

public class DataBaseManager {
    private final EventBus bus;
    private final FileManager fileManager;
    private final PreferencesManager preferencesManager;
    private static final SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
    private Location tempLocation = new Location("?");
    private Location lastLocation;

    public DataBaseManager(EventBus bus, FileManager fileManager, PreferencesManager preferencesManager) {
        this.bus = bus;
        this.fileManager = fileManager;
        this.preferencesManager = preferencesManager;
    }

    @Subscribe
    public void removeAllCaches(RemoveAllCachesCommand command) {
        ActiveAndroid.beginTransaction();

        List<CacheModel> cacheModels = CacheModel.getAllCodes();
        for (CacheModel cacheModel : cacheModels)
            fileManager.removeImages(cacheModel.code);
        CacheModel.truncate();
        ImageModel.truncate();
        LogModel.truncate();
        WaypointModel.truncate();

        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();

        bus.post(new CachesListChangedEvent());
    }

    @Subscribe
    public void saveCache(SaveCacheCommand command) {
        CacheData cacheData = command.cacheData;
        CacheModel cacheFromApi = createCacheModel(cacheData);
        List<ImageModel> cacheImagesFromApi = createImagesList(cacheData.code, cacheData.images);
        Map<String, List<ImageModel>> logsImagesFromApi = createLogToImagesMap(cacheData);
        List<LogModel> logsFromApi = createLogsList(cacheData);
        List<WaypointModel> waypointsFromApi = createWaypointsList(cacheData);
        computeDistance(cacheFromApi);

        ActiveAndroid.beginTransaction();
        cacheFromApi.save();
        saveImages(cacheFromApi.code, cacheImagesFromApi);
        saveLogs(cacheFromApi.code, logsFromApi, logsImagesFromApi);
        saveWaypoints(cacheFromApi.code, waypointsFromApi);
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();

        bus.post(new CacheSavedEvent(cacheFromApi.code));
    }

    private void computeDistance(CacheModel cacheFromApi) {
        if (lastLocation != null) {
            tempLocation.setLatitude(cacheFromApi.latitude);
            tempLocation.setLongitude(cacheFromApi.longitude);
            cacheFromApi.distance = (int) lastLocation.distanceTo(tempLocation);
            cacheFromApi.azimuth = AzimuthSanitizer.sanitize((int) lastLocation.bearingTo(tempLocation));
        }
    }

    private void saveImages(String code, Collection<ImageModel> imagesFromApi) {
        List<ImageModel> imagesFromDB = new Select().from(ImageModel.class).where("code LIKE ?", code).execute();
        imagesFromDB.removeAll(imagesFromApi);
        for (ImageModel image : imagesFromDB) {
            image.delete();
            fileManager.removeImage(code, image.name);
        }
        for (ImageModel image : imagesFromApi)
            image.save();
    }

    private void saveLogs(String code, Collection<LogModel> logsFromApi, Map<String, List<ImageModel>> logsImagesFromApi) {
        List<LogModel> logsFromDB = new Select().from(LogModel.class).where("code LIKE ?", code).execute();
        logsFromDB.removeAll(logsFromApi);
        for (LogModel log : logsFromDB) {
            log.delete();
            new Delete().from(ImageModel.class).where("code LIKE ?", log.uuid).execute();
            fileManager.removeImages(log.uuid);
        }
        for (LogModel log : logsFromApi) {
            saveImages(log.uuid, logsImagesFromApi.get(log.uuid));
            log.save();
        }
    }

    private void saveWaypoints(String code, Collection<WaypointModel> waypointsFromApi) {
        new Delete().from(WaypointModel.class).where("code LIKE ?", code).execute();
        for (WaypointModel waypoint : waypointsFromApi)
            waypoint.save();
    }

    private Map<String, List<ImageModel>> createLogToImagesMap(CacheData cacheData) {
        Map<String, List<ImageModel>> map = new HashMap<>(cacheData.latestLogs.size());
        for (LogData logData : cacheData.latestLogs) {
            map.put(logData.uuid, createImagesList(logData.uuid, logData.images));
        }
        return map;
    }

    @NonNull
    private List<ImageModel> createImagesList(String code, List<ImageData> imagesFromApi) {
        List<ImageModel> images = new ArrayList<>(imagesFromApi.size());
        for (ImageData imageData : imagesFromApi) {
            ImageModel image = new ImageModel();
            image.code = code;
            image.caption = imageData.caption;
            String[] split = imageData.url.split("/");
            image.name = split[split.length - 1];
            image.isSpoiler = imageData.is_spoiler;
            images.add(image);
        }
        return images;
    }

    @NonNull
    private List<WaypointModel> createWaypointsList(CacheData cacheData) {
        List<WaypointModel> waypoints = new ArrayList<>(cacheData.alternateWaypoints.size());
        for (AlternateWaypointData waypointData : cacheData.alternateWaypoints) {
            WaypointModel waypoint = new WaypointModel();
            waypoint.code = cacheData.code;
            waypoint.description = waypointData.description;
            waypoint.typeName = waypointData.type_name;
            String[] location = waypointData.location.split("\\|");
            waypoint.latitude = Float.parseFloat(location[0]);
            waypoint.longitude = Float.parseFloat(location[1]);
            waypoints.add(waypoint);
        }
        return waypoints;
    }

    @NonNull
    private List<LogModel> createLogsList(CacheData cacheData) {
        List<LogModel> logs = new ArrayList<>(cacheData.latestLogs.size());
        for (LogData logData : cacheData.latestLogs) {
            LogModel log = new LogModel();
            log.uuid = logData.uuid;
            log.code = cacheData.code;
            Date date = Iso8601Deserializer.toDate(logData.date);
            log.date = outFormat.format(date);
            log.type = LogType.fromText(logData.type);
            log.username = logData.username;
            log.comment = stripHtml(logData.comment);
            logs.add(log);
        }
        return logs;
    }

    private String stripHtml(String s) {
        return Html.fromHtml(s).toString()
                .replace('\n', (char) 32)
                .replace((char) 160, (char) 32)
                .replace((char) 65532, (char) 32)
                .trim();
    }

    @NonNull
    private CacheModel createCacheModel(CacheData cacheData) {
        CacheModel cache = new CacheModel();
        cache.code = cacheData.code;
        cache.name = cacheData.name;
        cache.difficulty = cacheData.difficulty;
        cache.terrain = cacheData.terrain;
        cache.founds = cacheData.founds;
        cache.notFounds = cacheData.notFounds;
        cache.description = stripHtml(cacheData.description);
        cache.shortDescription = cacheData.shortDescription;
        cache.hint = cacheData.hint;
        cache.type = CacheType.fromText(cacheData.type);
        String[] location = cacheData.location.split("\\|");
        cache.latitude = Float.parseFloat(location[0]);
        cache.longitude = Float.parseFloat(location[1]);
        cache.lastFound = cacheData.lastFound;
        cache.owner = cacheData.owner;
        cache.attributes = cacheData.attributes;
        cache.notes = cacheData.notes;
        cache.requiresPassword = cacheData.requiresPassword;
        cache.isFound = cacheData.isFound;
        cache.size = cacheData.size;
        cache.trackables = cacheData.trackables;
        return cache;
    }

    @Subscribe
    public void scheduleToDownload(ScheduleCachesToDownloadCommand command) {
        ActiveAndroid.beginTransaction();
        for (String code : command.results) {
            CacheToDownloadModel cacheToDownloadModel = new CacheToDownloadModel();
            cacheToDownloadModel.code = code;
            cacheToDownloadModel.save();
        }
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();

        bus.post(new ScheduldedToDownloadEvent());
    }

    @Subscribe
    public void clearAuthorization(ClearAuthorizationCredentialsCommand command) {
        preferencesManager.userAuthentication.authenticated(null, null);
        bus.post(new AuthenticationStateChangedEvent(false));
    }

    @Subscribe
    public void saveAuthorizationCredentials(SaveAuthorizationCredentialsCommand command) {
        preferencesManager.userAuthentication.authenticated(command.token, command.tokenSecret);
        boolean authenticated = preferencesManager.userAuthentication.isAuthenticated();
        bus.post(new AuthenticationStateChangedEvent(authenticated));
    }

    @Subscribe
    public void saveUserDetails(SaveUserDetailsCommand command) {
        Crashlytics.getInstance().core.setUserName(command.userDetails.username);
        preferencesManager.user.saveDetails(command.userDetails);
        bus.post(new ReceivedUserDetailsEvent(command.userDetails));
    }

    @Subscribe
    public void computeDistancesToCaches(ComputeDistanceToCachesCommand command) {
        Location userLocation = command.location;
        lastLocation = userLocation;
        if (lastLocation == null) {
            bus.post(new DistanceComputedEvent());
            return;
        }

        List<CacheModel> cacheModels = CacheModel.getForDistanceComputing();
        for (CacheModel cacheModel : cacheModels) {
            tempLocation.setLatitude(cacheModel.latitude);
            tempLocation.setLongitude(cacheModel.longitude);
            float distanceTo = userLocation.distanceTo(tempLocation);
            cacheModel.distance = (int) distanceTo;
            cacheModel.azimuth = AzimuthSanitizer.sanitize((int) userLocation.bearingTo(tempLocation));
        }

        ActiveAndroid.beginTransaction();
        for (CacheModel cacheModel : cacheModels) {
            CacheModel.setDistance(cacheModel.code, cacheModel.distance, cacheModel.azimuth);
        }
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
        bus.post(new DistanceComputedEvent());
    }
}
