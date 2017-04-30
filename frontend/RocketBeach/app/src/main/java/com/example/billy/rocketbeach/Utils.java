package com.example.billy.rocketbeach;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class Utils {

    private static RocketBeach rocket = null;

    static void addToken(@NonNull SharedPreferences preferences, String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    static void removeToken(@NonNull SharedPreferences preferences, String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    static RocketBeach getService() {
        if (rocket == null) {
            rocket = new Retrofit.Builder()
//                    .baseUrl("http://192.168.44.43/")
                    .baseUrl("http://rocketbeach.ml/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RocketBeach.class);
        }
        return rocket;
    }
}
