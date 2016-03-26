package com.wireless.transfile.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wireless.transfile.constants.Constants;

public class AppSettings {

    public static boolean isServiceStarted(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(Constants.IS_SERVICE_STARTED, false);
    }

    public static void setServiceStarted(Context context, boolean isStarted) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.IS_SERVICE_STARTED, isStarted);
        editor.apply();
    }

    public static int getPortNumber(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(Constants.PREF_SERVER_PORT, Constants.DEFAULT_SERVER_PORT);
    }

    public static void setPortNumber(Context context, int port) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Constants.PREF_SERVER_PORT, port);
        editor.apply();
    }
}
