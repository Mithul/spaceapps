package com.example.billy.rocketbeach;

import android.app.Application;

import com.pushbots.push.Pushbots;

/**
 * Created by mithul on 30/4/17.
 */

public class PushbotsMain extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Pushbots Library
        Pushbots.sharedInstance().init(this);
    }
}