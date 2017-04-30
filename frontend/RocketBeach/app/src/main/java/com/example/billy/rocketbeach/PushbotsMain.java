package com.example.billy.rocketbeach;

import android.app.Application;

import com.pushbots.push.Pushbots;

public class PushbotsMain extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Pushbots Library
        Pushbots.sharedInstance().init(this);
    }
}