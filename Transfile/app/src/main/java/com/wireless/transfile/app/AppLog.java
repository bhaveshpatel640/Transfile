package com.wireless.transfile.app;

import android.util.Log;

import com.wireless.transfile.constants.Constants;

public class AppLog {
    public static final String LOG = "transfile";
    public static final String WARNING = "Warnning";

    public static void logString(String message) {
        if (Constants.LOG_DEBUG)
            Log.i(LOG, message);
    }

    public static void logString(String message, String tag) {
        if (Constants.LOG_DEBUG)
            Log.i(tag, message);
    }

    public static void logWarningString(String message) {
        if (Constants.LOG_DEBUG)
            Log.i(WARNING, message);
    }
}
