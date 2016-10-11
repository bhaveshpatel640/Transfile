package com.wireless.transfile.ui;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wireless.transfile.R;
import com.wireless.transfile.app.AppSettings;
import com.wireless.transfile.service.HTTPService;
import com.wireless.transfile.utility.Utility;

import static com.wireless.transfile.app.AppSettings.getPortNumber;
import static com.wireless.transfile.app.AppSettings.setClientIp;
import static com.wireless.transfile.app.AppSettings.setPortNumber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int NOTIFICATION_STARTED_ID = 5;
    Button startStopButton;
    TextView ipAddress;
    ImageView imageView;
    Dialog changePortDialog;
    Dialog howToDialog;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        dialog = new Dialog(this);
        dialog.setTitle("Select connectivity");
        dialog.setContentView(R.layout.dialog_wifi_hotspot);
        dialog.setCancelable(false);

        imageView = (ImageView) findViewById(R.id.active_inactive_image);
        imageView.setImageResource(R.drawable.transfile_inactive);
        AppSettings.setServiceStarted(MainActivity.this, false);
        setClientIp(MainActivity.this, false);

        //Check whether service is started or not.
        boolean isRunning = AppSettings.isServiceStarted(this);
        //Getting references of views....
        startStopButton = (Button) findViewById(R.id.startStopButton);
        ipAddress = (TextView) findViewById(R.id.ipAddressText);
        startStopButton.setOnClickListener(btnClick);
        setButtonText(isRunning);
        setInfoText(isRunning);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.startStopButton: {
                    if (Utility.getLocalIpAddress(1).equals("noIP")) {
                        showNetworkDialog();
                    } else {
                        cancelNetworkDialog();
                        Intent intent = new Intent(MainActivity.this, HTTPService.class);
                        if (AppSettings.isServiceStarted(MainActivity.this)) {
                            stopService(intent);
                            AppSettings.setServiceStarted(MainActivity.this, false);
                            setClientIp(MainActivity.this, false);
                            setButtonText(false);
                            setInfoText(false);
                            imageView.setImageResource(R.drawable.transfile_inactive);
                        } else {
                            int serverPort = getPortNumber(getApplicationContext());
                            if (Utility.available(serverPort)) {
                                startService(intent);
                                AppSettings.setServiceStarted(MainActivity.this, true);
                                setButtonText(true);
                                setInfoText(true);
                                imageView.setImageResource(R.drawable.transfile_active);
                            } else {
                                Toast.makeText(getApplicationContext(), "Port " + serverPort + " already in use!", Toast.LENGTH_LONG).show();
                            }

                        }
                        break;
                    }
                }
            }
        }
    };

    private void setButtonText(boolean isServiceRunning) {
        ((Button) findViewById(R.id.startStopButton)).setText(getString(isServiceRunning ? R.string.stop_caption : R.string.start_caption));
    }

    private void setInfoText(boolean isServiceRunning) {
        TextView textViewLog = (TextView) findViewById(R.id.ipAddressText);
        String text = getString(R.string.log_notrunning);

        if (isServiceRunning) {
            text = getString(R.string.log_running) + "\n" + getString(R.string.log_msg1) + "\n'http://" + Utility.getLocalIpAddress(1) + ":" + getPortNumber(this) + "' " + getString(R.string.log_msg2);
        }
        textViewLog.setText(text);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.change_port) {
            changePortDialog = new Dialog(this);
            changePortDialog.setTitle("Change Port Number");
            changePortDialog.setContentView(R.layout.dialog_change_port);
            changePortDialog.setCancelable(false);
            changePortDialog.show();
        } else if (id == R.id.how_to) {
            howToDialog = new Dialog(this);
            howToDialog.setContentView(R.layout.dialog_how_to_use);
            howToDialog.setTitle("How to use");
            howToDialog.setCancelable(false);
            howToDialog.show();
        } else if (id == R.id.contact_us) {
            Dialog contactUsDialog = new Dialog(this);
            contactUsDialog.setContentView(R.layout.dialog_contact_us);
            contactUsDialog.setTitle("Find us at ");
            contactUsDialog.show();
        } else if (id == R.id.share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TransFile");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey!, checkout TransFile: " +
                    "It's a" +
                    " great app to manage files, stream music and videos wirelessly between " +
                    "android and PC");
            startActivity(Intent.createChooser(sharingIntent, "Share using"));

        } else if (id == R.id.exit) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_STARTED_ID);
            stopService(new Intent(MainActivity.this, HTTPService.class));
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changePortSetTextClicked(View view) {
        TextView view1 = (TextView) changePortDialog.findViewById(R.id.change_port_edit_text);
        int serverPort = Integer.parseInt(view1.getText().toString());

        if (serverPort >= 1000 && serverPort <= 9999) {
            if (Utility.available(serverPort)) {
                setPortNumber(getApplicationContext(), serverPort);
                Toast.makeText(getApplicationContext(), "Port number changed successfully to " + serverPort, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Port " + serverPort + " already in use!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), serverPort + " is not a valid port number;", Toast.LENGTH_LONG).show();
        }
        changePortDialog.dismiss();
    }

    public void changePortCancelTextClicked(View view) {
        changePortDialog.dismiss();
    }

    public void howToGotItClicked(View view) {
        howToDialog.dismiss();
    }

    public void photoClicked(View view) {
        String url;
        if (view.getId() == R.id.bhavesh)
            url = "https://www.facebook.com/bhaveshpatel640";
        else if (view.getId() == R.id.piyush)
            url = "https://www.facebook.com/piyush.saravagi";
        else
            url = "https://www.facebook.com/suyash.thakre.4";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void showNetworkDialog() {
        dialog.show();
    }

    public void cancelNetworkDialog() {
        dialog.cancel();
    }

    public void enableWifiClicked(View view) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        dialog.dismiss();
    }

    public void enableHotspotClicked(View view) {
        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        dialog.dismiss();
    }
}
