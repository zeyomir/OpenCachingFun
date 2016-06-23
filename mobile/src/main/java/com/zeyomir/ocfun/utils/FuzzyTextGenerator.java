package com.zeyomir.ocfun.utils;

import android.content.Context;

import com.zeyomir.ocfun.R;

public class FuzzyTextGenerator {
    private final Context context;

    public FuzzyTextGenerator(Context context) {
        this.context = context;
    }

    public String forDistance(long distanceInMeters) {
        if (distanceInMeters < 100)
            return String.format("{gmd-directions-walk}%dm", distanceInMeters);
        else if (distanceInMeters < 1000) {
            float l = distanceInMeters / 100f;
            int rounded = Math.round(l);
            return String.format("{gmd-directions-walk}%dm", rounded * 100);
        } else if (distanceInMeters < 5000) {
            float l = distanceInMeters / 100f;
            int rounded = Math.round(l);
            return String.format("{gmd-directions-walk}%.1fkm", rounded / 10f);
        } else {
            float l = distanceInMeters / 1000f;
            int rounded = Math.round(l);
            return String.format("{gmd-directions-walk}%dkm", rounded);
        }
    }

    public String forElapsedTime(long timeDelta) {
        if (timeDelta < 2* 60_000)
            return context.getString(R.string.time_elapsed_one_minute);
        else if (timeDelta < 6 * 60_000)
            return context.getString(R.string.time_elapsed_minutes, timeDelta / 60_000);
        else if (timeDelta < 50 * 60_000)
            return context.getString(R.string.time_elapsed_minutes, timeDelta / 5 / 60_000 * 5);
        else if (timeDelta < 75 * 60_000)
            return context.getString(R.string.time_elapsed_hour);
        else if (timeDelta < 105 * 60_000)
            return context.getString(R.string.time_elapsed_hour_and_half);
        else if (timeDelta < 150 * 60_000)
            return context.getString(R.string.time_elapsed_2_hours);
        else if (timeDelta < 310 * 60_000)
            return context.getString(R.string.time_elapsed_3_hours);
        else if (timeDelta < 11 * 60 * 60_000)
            return context.getString(R.string.time_elapsed_few_hours);
        else if (timeDelta < 21 * 60 * 60_000)
            return context.getString(R.string.time_elapsed_dozen_hours);
        else if (timeDelta < 36 * 60 * 60_000)
            return context.getString(R.string.time_elapsed_24_hours);
        else if (timeDelta < 60 * 60 * 60_000)
            return context.getString(R.string.time_elapsed_2_days);
        else
            return context.getString(R.string.time_elapsed_few_days);
    }
}
