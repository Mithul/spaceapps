package com.example.billy.rocketbeach;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.pushbots.push.Pushbots;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ShowBeachActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private RocketBeach rocket;
    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;
    private double latitute, longitude;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void set_team_theme(String team_name, ImageView team_image, FrameLayout frame_layout){
        if(team_name.toLowerCase().trim().equals("neutral")) {
            team_image.setImageResource(R.drawable.neutral);
            frame_layout.setBackgroundColor(getResources().getColor(R.color.neutral_theme));
        }else if(team_name.toLowerCase().trim().equals("zeus")) {
            team_image.setImageResource(R.drawable.zeus_white);
            frame_layout.setBackgroundColor(getResources().getColor(R.color.zeus_theme));
        }else if(team_name.toLowerCase().trim().equals("poseidon")) {
            team_image.setImageResource(R.drawable.poseidon_white);
            frame_layout.setBackgroundColor(getResources().getColor(R.color.poseidon_theme));
        }
    }

    private void show_warning(Float uv_index){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_beach);

        mVisible = true;
//        latitute = Float.parseFloat(null);
//        longitude = Float.parseFloat(null);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        rocket = Utils.getService();
        final String token = getSharedPreferences("RocketBeach", 0).getString("X-Auth-Token", "7e977b10c81d4b7581a2a106b9fb84dc");

        final TextView beach_name = (TextView)findViewById(R.id.beach_name);
        final TextView beach_health = (TextView)findViewById(R.id.beach_health);
        final TextView beach_team = (TextView)findViewById(R.id.beach_team);
        final TextView uv_index = (TextView)findViewById(R.id.uv_index);
        final TextView potential_xp = (TextView)findViewById(R.id.potential_xp);
        final TextView user_health = (TextView)findViewById(R.id.player_health);
        final ImageView team_image = (ImageView) findViewById(R.id.team_image);
        final FrameLayout frame_layout = (FrameLayout) findViewById(R.id.show_beach_frame);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationAndContactsTask();

        final Beach[] beach = {null};
        //TODO : Dynamic Beach id
        String beach_id = "1";
        rocket.getBeachInfo(beach_id, token).enqueue(new Callback<Beach>() {
            @Override
            public void onResponse(Call<Beach> call, Response<Beach> response) {
                if (response.code() == 200) {
                    beach[0] = response.body();
                    beach[0].validate_team();
                    beach_name.setText(beach[0].name);
                    beach_health.setText(beach[0].health);
                    String team_name = beach[0].team.name;
                    beach_team.setText(beach[0].team.name);
                    set_team_theme(team_name, team_image, frame_layout);
                    show_warning((float) beach[0].uv_index.value);
                    uv_index.setText(String.valueOf(beach[0].uv_index.value));
                    potential_xp.setText(String.valueOf(beach[0].potential_xp));
                }
            }

            @Override
            public void onFailure(Call<Beach> call, Throwable t) {
                Log.e("ShowBeach",t.toString());
            }
        });



        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUserHealth();
                    }
                });
            }
        },0,10000);

        findViewById(R.id.attack_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> options = new HashMap<>();
                locationAndContactsTask();
                //TODO : remove hardcoded location
                Log.e("Location", String.valueOf(latitute));
                Log.e("Location", String.valueOf(longitude));
                latitute = 40.7;
                longitude = -74.2;
                options.put("lat", String.valueOf(latitute));
                options.put("long", String.valueOf(longitude));
                rocket.attack("1", token, options).enqueue(new Callback<AttackResponse>() {
                    @Override
                    public void onResponse(Call<AttackResponse> call, Response<AttackResponse> response) {
                        if (response.code() == 200) {
                            AttackResponse ar =response.body();
                            ar.validate_team();
                            Log.e("Attack", String.valueOf(ar.status));
                            beach[0] = ar.beach;
                            beach_name.setText(beach[0].name);
                            beach_health.setText(beach[0].health);
                            String team_name = ar.team.name;
                            beach_team.setText(ar.team.name);
                            set_team_theme(team_name, team_image, frame_layout);
//                            uv_index.setText(String.valueOf(beach[0].uv_index.value));
                        }
                    }

                    @Override
                    public void onFailure(Call<AttackResponse> call, Throwable t) {
                        Log.e("ShowBeach",t.toString());
                    }
                });
            }
        });
        findViewById(R.id.attack_button).setOnTouchListener(mDelayHideTouchListener);
    }

    private void updateUserHealth(){
        Map<String, String> options = new HashMap<>();
        locationAndContactsTask();
        final TextView user_health = (TextView)findViewById(R.id.player_health);
        //TODO : remove hardcoded location
        Log.e("Location", String.valueOf(latitute));
        Log.e("Location", String.valueOf(longitude));
        latitute = 40.7;
        longitude = -74.2;
        options.put("lat", String.valueOf(latitute));
        options.put("long", String.valueOf(longitude));
        final String token = getSharedPreferences("RocketBeach", 0).getString("X-Auth-Token", "7e977b10c81d4b7581a2a106b9fb84dc");
        rocket.updateHealth(token, options).enqueue(new Callback<UpdateHealthResponse>() {
            @Override
            public void onResponse(Call<UpdateHealthResponse> call, Response<UpdateHealthResponse> response) {
                UpdateHealthResponse uhr = response.body();
                user_health.setText(uhr.health);
                boolean alive = uhr.user_alive;
                if (!alive)
                    Toast.makeText(getApplicationContext(), "You are dead and can no longer conquer beaches", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<UpdateHealthResponse> call, Throwable t) {
                Log.e("ShowBeach", t.toString());
            }
        });
    }

    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    public void locationAndContactsTask() throws SecurityException {
        String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION };
        latitute = 0;
        longitude = 0;
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Have permissions, do the thing!
            Location t = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (t == null) return;
            latitute = t.getLatitude();
            longitude = t.getLongitude();
//            Toast.makeText(this, "TODO: Location things" + t.getLatitude(), Toast.LENGTH_LONG).show();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, "GIVE PERM",
                    RC_LOCATION_CONTACTS_PERM, perms);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(ShowBeachActivity.class.getSimpleName(), "Connected to Google Play Services!");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
            Log.v("Biljith", lon + " " + lat);
            Toast.makeText(this, lon+" "+lat, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(MainActivity.class.getSimpleName(), "Can't connect to Google Play Services!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
