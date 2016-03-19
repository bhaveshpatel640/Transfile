package com.wireless.transfile.utility;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Utility {
    private static int batteryLevel;
    private static String status;

    public static String getLocalIpAddress(int flag) {
        return getLocalIpAddress(true, flag);
    }

    public static String getLocalIpAddress(boolean useIPv4, int flag) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress inetAddress : inetAddresses) {
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipAddress = inetAddress.getHostAddress().toUpperCase();
                        if (flag == 2) {
                            byte[] mac = networkInterface.getHardwareAddress();
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < mac.length; i++) {
                                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                            }
                            return sb.toString();
                        }
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(ipAddress);
                        if (useIPv4) {
                            if (isIPv4) {
                                return ipAddress;
                            }
                        } else {
                            if (!isIPv4) {
                                int delimiter = ipAddress.indexOf('%'); // drop ip6 port suffix
                                return delimiter < 0 ? ipAddress : ipAddress.substring(0, delimiter);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "noIP";
    }

    public static boolean available(int port) {
        if (port < 1 || port > 65535) {
            return false;
        }
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    /*
    * You may be connected to internet through a Wifi or a cellular network.
    * You can get the IP address of your mobile device programmatically using the following steps:
    * First of all, find and list all the different network interfaces in your mobile
    * Find their IP addresses
    * Then one by one check each if each IP address is not a loopback address.
    * Loopback address is only used for testing purposes and does not has an associated hardware to connect to the internet.
    * If an IP address is not a loopback address, then its an IP address with which you are connected to internet.
    * Don't forgot to add the permission in Android manifest
    * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    * <uses-permission android:name="android.permission.INTERNET" />
    */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    public static int getBatteryPercentage(Context context) {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (currentLevel >= 0 && scale > 0) {
                    level = (currentLevel * 100) / scale;
                }
                batteryLevel = level;
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        return batteryLevel;
    }

    public static String getBatteryStatus(Context context) {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int statusCode = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                status = "Unknown";
                switch (statusCode) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        status = "Charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        status = "Discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        status = "Full";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        status = "Not Charging";
                        break;
                }
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        return status;
    }

    public static String getWifiStatus(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            return "Enabled";
        } else
            return "Disabled";
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getAvailableInternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return Long.toString(availableBlocks * blockSize);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getTotalInternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return Long.toString(totalBlocks * blockSize);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return Long.toString(availableBlocks * blockSize);
        } else {
            return "-";
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return Long.toString(totalBlocks * blockSize);
        } else {
            return "-";
        }
    }


    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int extensionPos = filename.lastIndexOf('.');
        int lastUnixPos = filename.lastIndexOf('/');
        int lastWindowsPos = filename.lastIndexOf('\\');
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);

        int index = lastSeparator > extensionPos ? -1 : extensionPos;
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index);
        }
    }
}
