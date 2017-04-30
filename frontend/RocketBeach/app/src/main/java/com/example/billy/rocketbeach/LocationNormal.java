package com.example.billy.rocketbeach;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;


public class LocationNormal extends Activity implements LocationListener {
    private static final String TAG = "LocationNormal";
    private static final String[] S = {"Out of Service",
            "Temporarily Unavailable", "Available"};

    private TextView output;
    private LocationManager locationManager;
    private String bestProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_demo);

        // Get the output UI
        output = (TextView) findViewById(R.id.output);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // List all providers:
        List<String> providers = locationManager.getAllProviders();
        for (String provider : providers) {
            printProvider(provider);

        }


        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, false);
        output.append("\n\nBEST Provider:\n");
        printProvider(bestProvider);

        output.append("\n\nLocations (starting with last known):");

        Location location = locationManager.getLastKnownLocation(bestProvider);
        printLocation(location);
    }

    /** Register for the updates when Activity is in foreground */
    @Override
    protected void onResume() {
        super.onResume();
        re();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void re() throws SecurityException {
        locationManager.requestLocationUpdates(bestProvider, 20000, 1, this);
    }


    /** Stop the updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        printLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // let okProvider be bestProvider
        // re-register for updates
        output.append("\n\nProvider Disabled: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        // is provider better than bestProvider?
        // is yes, bestProvider = provider
        output.append("\n\nProvider Enabled: " + provider);
        Location location = locationManager.getLastKnownLocation(provider);
        printLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        output.append("\n\nProvider Status Changed: " + provider + ", Status="
                + S[status] + ", Extras=" + extras);
    }

    private void printProvider(String provider) {
        LocationProvider info = locationManager.getProvider(provider);
        output.append(provider + "\n\n");
        output.append(info.toString() + "\n\n");
    }

    private void printLocation(Location location) {
        if (location == null)
            output.append("\nLocation[unknown]\n\n");
        else
            output.append("\n\n" + location.toString());
    }

}