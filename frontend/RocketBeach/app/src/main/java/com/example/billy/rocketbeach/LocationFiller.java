package com.example.billy.rocketbeach;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class LocationFiller extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent gotoBeaches = new Intent(getApplicationContext(), BeachActivity.class);
        Log.d("RocketBeach", "Filler");
        gotoBeaches.putExtra("GPS", new double[]{13.45, 2.3});
        startActivity(gotoBeaches);
    }
}
