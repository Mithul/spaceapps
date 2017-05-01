package com.example.billy.rocketbeach;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowBeachActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private RocketBeach rocket;
    public Beach intentBeach;
    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;
    private double latitute, longitude;
    boolean enable_attack=false;
    String token;
    Timer poll_health_timer;

    String beach_id;
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
        final TextView beach_team = (TextView) findViewById(R.id.beach_team);
        if(team_name.toLowerCase().trim().equals("neutral")) {
            beach_team.setText("Owner: Unconquered");
            team_image.setImageResource(R.drawable.neutral);
            frame_layout.setBackgroundColor(getResources().getColor(R.color.neutral_theme));
        }else if(team_name.toLowerCase().trim().equals("zeus")) {
            beach_team.setText("Team: "+team_name);
            team_image.setImageResource(R.drawable.zeus_white);
            frame_layout.setBackgroundColor(getResources().getColor(R.color.zeus_theme));
        }else if(team_name.toLowerCase().trim().equals("poseidon")) {
            beach_team.setText("Team: "+team_name);
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
        setTitle("Beach Details");

        rocket = Utils.getService();
        token = getSharedPreferences("RocketBeach", 0).getString("X-Auth-Token", "7e977b10c81d4b7581a2a106b9fb84dc");

        final TextView beach_name = (TextView) findViewById(R.id.beach_name);
        final TextView beach_health = (TextView) findViewById(R.id.beach_health);
        final TextView beach_team = (TextView) findViewById(R.id.beach_team);
        final TextView uv_index = (TextView) findViewById(R.id.uv_index);
        final TextView potential_xp = (TextView) findViewById(R.id.potential_xp);
        final TextView user_health = (TextView) findViewById(R.id.player_health);
        final ImageView team_image = (ImageView) findViewById(R.id.team_image);
        final FrameLayout frame_layout = (FrameLayout) findViewById(R.id.show_beach_frame);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationAndContactsTask();

        final Beach[] beach = {null};

        String[] flatBeach = getIntent().getStringArrayExtra("Beach");

        if (flatBeach != null){
            intentBeach = Beach.unflatten(flatBeach);
            beach_id = intentBeach.id + "";
            Log.e("Beach id", beach_id);
        }else{
            beach_id = "1";
        }
        final Map<String, String> options = new HashMap<>();
        locationAndContactsTask();
        //TODO : remove hardcoded location
        Log.e("Location", String.valueOf(latitute));
        Log.e("Location", String.valueOf(longitude));
        options.put("lat", String.valueOf(latitute));
        options.put("long", String.valueOf(longitude));
        rocket.getBeachInfo(beach_id, token, options).enqueue(new Callback<Beach>() {
            @Override
            public void onResponse(Call<Beach> call, Response<Beach> response) {
                if (response.code() == 200) {
                    Beach beach = response.body();
                    updateBeachStats(beach);
                    String team_name = beach.team.name;
                    set_team_theme(team_name, team_image, frame_layout);
                }
            }

            @Override
            public void onFailure(Call<Beach> call, Throwable t) {
                Log.e("RocketBeach",t.toString());
            }
        });


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

        poll_health_timer = new Timer();
        poll_health_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUserHealth();
                        attackBeach();
                    }
                });
            }
        },0,10000);

        final Button attack_button = (Button) findViewById(R.id.attack_button);
        findViewById(R.id.attack_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enable_attack = !enable_attack;
                if(enable_attack){
                    attack_button.setText("Stop Attacking");
                }else{
                    attack_button.setText("Attack");
                }
            }
        });
        findViewById(R.id.attack_button).setOnTouchListener(mDelayHideTouchListener);
    }

    private void attackBeach(){
        if(!enable_attack) return;
        final ImageView team_image = (ImageView) findViewById(R.id.team_image);
        final FrameLayout frame_layout = (FrameLayout) findViewById(R.id.show_beach_frame);
        token = getSharedPreferences("RocketBeach", 0).getString("X-Auth-Token", "7e977b10c81d4b7581a2a106b9fb84dc");

        final Beach[] beach={null};
        final Map<String, String> options = new HashMap<>();
        locationAndContactsTask();
        //TODO : remove hardcoded location
        Log.e("Location", String.valueOf(latitute));
        Log.e("Location", String.valueOf(longitude));
        options.put("lat", String.valueOf(latitute));
        options.put("long", String.valueOf(longitude));
        rocket.attack(beach_id, token, options).enqueue(new Callback<AttackResponse>() {
            @Override
            public void onResponse(Call<AttackResponse> call, Response<AttackResponse> response) {
                if (response.code() == 200) {
                    AttackResponse ar = response.body();
                    ar.validate_team();
                    if (ar.status) {
                        updateBeachStats(ar.beach);
                        String team_name = ar.team.name;
                        set_team_theme(team_name, team_image, frame_layout);
                    } else {
                        Log.d("RocketBeach", "Cannot attack");
                    }
//                            uv_index.setText(String.valueOf(beach[0].uv_index.value));
                }
            }

            @Override
            public void onFailure(Call<AttackResponse> call, Throwable t) {
                Log.e("RocketBeach",t.toString());
            }
        });
    }

    private void updateBeachStats(Beach beach){
        final TextView beach_name = (TextView) findViewById(R.id.beach_name);
        final TextView beach_health = (TextView) findViewById(R.id.beach_health);
        final TextView uv_index = (TextView) findViewById(R.id.uv_index);
        final TextView distance = (TextView) findViewById(R.id.distance);
        final TextView potential_xp = (TextView) findViewById(R.id.potential_xp);
        beach_name.setText(beach.name);
        beach_health.setText(String.valueOf(beach.get_health()));
        uv_index.setText(String.valueOf(beach.get_uv_value()));
        potential_xp.setText(String.valueOf(beach.potential_xp));
        distance.setText(String.valueOf(beach.get_distance()));
        beach.validate_team();
//        potential_xp.setText(beach.current_xp);
        updateWarnings(beach.get_uv_value());
    }

    private void updateWarnings(double uv_index){
        final TextView warnings = (TextView)findViewById(R.id.warnings);
        if(uv_index < 4)
            warnings.setText("Beach is safe with low exposure to UV radiation");
        else if(uv_index >= 4 && uv_index < 8.5){
            warnings.setText("Consider applying some sunscreen, UV Radiations are moderate");
        }else{
            warnings.setText("You are killing yourself in this level of UV radiation");
        }
    }
    private void updateWarnings(String healths){
        final TextView warnings2 = (TextView)findViewById(R.id.warnings2);
        double health = Double.parseDouble(healths);
        if(health < 20){
            warnings2.setText("Your health is pretty low by prolonged exposure to UV rays. Consider taking rest");
        }else{
            warnings2.setText("");
        }
    }

    private void updateUserHealth(){
        Map<String, String> options = new HashMap<>();
        locationAndContactsTask();
        final TextView user_health = (TextView)findViewById(R.id.player_health);
        //TODO : remove hardcoded location
        Log.e("Location", String.valueOf(latitute));
        Log.e("Location", String.valueOf(longitude));
//        latitute = 40.7;
//        longitude = -74.2;
        options.put("lat", String.valueOf(latitute));
        options.put("long", String.valueOf(longitude));
        final String token = getSharedPreferences("RocketBeach", 0).getString("X-Auth-Token", "7e977b10c81d4b7581a2a106b9fb84dc");
        rocket.updateHealth(token, options).enqueue(new Callback<UpdateHealthResponse>() {
            @Override
            public void onResponse(Call<UpdateHealthResponse> call, Response<UpdateHealthResponse> response) {
                if (response.code() == 200){
                    UpdateHealthResponse uhr = response.body();
                    user_health.setText(String.valueOf(uhr.get_health()));
                    updateWarnings(uhr.health);
                    boolean alive = uhr.user_alive;
                    if (!alive)
                        Toast.makeText(getApplicationContext(), "You are dead and can no longer conquer beaches", Toast.LENGTH_LONG).show();
                }
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
        poll_health_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUserHealth();
                        attackBeach();
                    }
                });
            }
        },0,10000);
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
        poll_health_timer.cancel();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(ShowBeachActivity.class.getSimpleName(), "Connected to Google Play Services!");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
            Log.d("RocketBeach", lon + " " + lat);
            Toast.makeText(this, lon+" "+lat, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("RocketBeach", "Can't connect to Google Play Services!");
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
