package com.example.billy.rocketbeach;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeachActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "RocketBeach";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 160000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    protected GoogleApiClient mGoogleApiClient;

    protected LocationRequest mLocationRequest;

    protected LocationSettingsRequest mLocationSettingsRequest;

    protected Location mCurrentLocation;

    protected Boolean mRequestingLocationUpdates;

    protected String mLastUpdateTime;

    ListView beaches;
    BeachAdapter adapter;
    TextView empty;

    RocketBeach service;

    List<Beach> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.beach_conquest);
        }
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        updateValuesFromBundle(savedInstanceState);

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

        beaches = (ListView) findViewById(R.id.beaches);
        adapter = new BeachAdapter(list);
        beaches.setAdapter(adapter);

        beaches.setEmptyView(empty);

        final Intent showBeach = new Intent(getApplicationContext(), ShowBeachActivity.class);

        service = Utils.getService();

        Map<String, String> options = new HashMap<>();

        double[] gps = getIntent().getDoubleArrayExtra("GPS");
        options.put("lat", gps[0] + "");
        options.put("long", gps[1] + "");
        Log.d("RocketBeach", "Beach " + gps[0] + " " + gps[1]);
        getBeachesList(options);

        beaches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Beach b = list.get(position);
                Toast.makeText(
                        getApplicationContext(),
                        b.name,
                        Toast.LENGTH_SHORT
                ).show();
//                Log.e("BeachAct",b.health);
                showBeach.putExtra("Beach", b.flatten());
                startActivity(showBeach);
            }
        });

        empty = (TextView) findViewById(R.id.empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goto_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
        }
        return true;
    }


    public void getBeachesList(Map<String, String> options) {
        String token = getSharedPreferences("RocketBeach", 0).getString("X-Auth-Token", "");
        service.listBeaches(options, token).enqueue(new Callback<List<Beach>>() {
            @Override
            public void onResponse(Call<List<Beach>> call, Response<List<Beach>> response) {
                Log.d("RocketBeach", response.code() + "");
                list.clear();
                list.addAll(response.body());
                for(Beach b: response.body()) {
//                    Log.d("RocketBeach", b.name);
                }
                Log.d("RocketBeach", response.body().size() + " ");
                adapter.setList(list);
                beaches.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Beach>> call, Throwable t) {
                Log.d("RocketBeach", t.getMessage());
            }
        });

    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateLocationUI();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateLocationUI();
                        break;
                }
                break;
        }
    }

    protected void startLocationUpdates() {
        LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "All location settings are satisfied.");
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, BeachActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings ");
                        try {
                            status.startResolutionForResult(BeachActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Log.e(TAG, errorMessage);
                        Toast.makeText(BeachActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = false;
                }
                updateLocationUI();
            }
        });

    }

    private void updateLocationUI() {
        Log.d("RocketBeach", "Updating UI according to Location");
//        if (mCurrentLocation != null) {
//            Map<String, String> options = new HashMap<>();
//            options.put("lat", mCurrentLocation.getLatitude() + "");
//            options.put("long", mCurrentLocation.getLongitude() + "");
//            getBeachesList(options);
//        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        updateLocationUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateLocationUI();
        }
        if (mRequestingLocationUpdates) {
            Log.i(TAG, "in onConnected(), starting location updates");
            startLocationUpdates();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateLocationUI();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
}
