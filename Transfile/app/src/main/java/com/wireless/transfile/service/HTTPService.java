package com.wireless.transfile.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;

import com.wireless.transfile.R;
import com.wireless.transfile.constants.Constants;
import com.wireless.transfile.ui.MainActivity;
import com.wireless.transfile.utility.Utility;
import com.wireless.transfile.webserver.WebServer;

import static com.wireless.transfile.app.AppSettings.getPortNumber;

public class HTTPService extends Service {
    private static final int NOTIFICATION_STARTED_ID = 5;
    private NotificationManager mNotifyMgr = null;
    private WebServer server = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        server = new WebServer(this, mNotifyMgr);
    }

    @Override
    public void onDestroy() {
        if (server != null) {
            server.stopThread();
            server = null;
        }
        mNotifyMgr.cancel(NOTIFICATION_STARTED_ID);
        mNotifyMgr = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        server.startThread();
        showNotification();
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {
        String text = "http://" + Utility.getLocalIpAddress(1) + ":" + getPortNumber(getApplicationContext());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.transfile_launcher);
        mBuilder.setContentTitle(getString(R.string.log_running));
        mBuilder.setContentText(text);
        mBuilder.setOngoing(true);
        //  Intent resultIntent = new Intent(this, MainActivity.class);
        //PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // mBuilder.setContentIntent(resultPendingIntent);
        mNotifyMgr.notify(NOTIFICATION_STARTED_ID, mBuilder.build());
    }
}
