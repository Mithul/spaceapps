package com.example.billy.rocketbeach;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;


public class LocationNormal extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private TextView output;
    private LocationManager locationManager;
    private boolean mGPSEnabled = false;
    private GoogleApiClient googleApiClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_demo);


        // Get the location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

//        Criteria criteria = new Criteria();
//        String bestProvider = locationManager.getBestProvider(criteria, false);
//        output.append("\n\nBEST Provider:\n");
//        printProvider(bestProvider);
//
//        output.append("\n\nLocations (starting with last known):");
//
//        Location location = locationManager.getLastKnownLocation(bestProvider);
//        printLocation(location);

        if (!mGPSEnabled)
            switchOnGPS();

        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();

    }

    public void switchOnGPS() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 123);
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder( getParent().getApplicationContext())
//                .setTitle("GPS Not Enabled")
//                .setMessage("Do you wants to turn On GPS")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivity(intent);
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//
//        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            // Make sure the request was successful
            Log.d("RocketBeach", "Return from settings");
            mGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (mGPSEnabled)
                googleApiClient.connect();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGPSEnabled) {
            if (googleApiClient != null) {
                googleApiClient.connect();
            }
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("RocketBeach", "Connected to Google Play Services!");

        Intent gotoBeaches = new Intent(getApplicationContext(), BeachActivity.class);

        double lat, lon;
        Location lastLocation = getLoc();
        if (lastLocation == null) {
            lat = 13.45;
            lon = 2.3;
        } else {
            lat = lastLocation.getLatitude();
            lon = lastLocation.getLongitude();
        }
        Log.d("RocketBeach", "LocationNormal " + lat + " " + lon);

        gotoBeaches.putExtra("GPS", new double[]{lat, lon});

        startActivity(gotoBeaches);
        finish();
    }

    @TargetApi(value = Build.VERSION_CODES.LOLLIPOP)
    public Location getLoc() throws SecurityException {
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("RocketBeach", "Can't connect to Google Play Services!");
    }


}