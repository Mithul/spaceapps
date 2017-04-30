package com.example.billy.rocketbeach;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    AboutView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUserInfo();
    }

    public void mapToView(Beachgoer person) {
        SharedPreferences preferences = getSharedPreferences("RocketBeach", 0);
        profile = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName(person.email)
                .setSubTitle(preferences.getString("Team", "Team Unknown"))
                .setWrapScrollView(true)
                .build();

        addContentView(profile, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void loadUserInfo() {
        String token = getSharedPreferences("RocketBeach", 0).getString("X-Auth-Token", "");
        Log.d("RocketBeach", token);
        Utils.getService().getMe(token).enqueue(new Callback<MeDTO>() {
            @Override
            public void onResponse(Call<MeDTO> call, Response<MeDTO> response) {
                Log.d("RocketBeach", response.body().user.email);
                mapToView(response.body().user);
            }

            @Override
            public void onFailure(Call<MeDTO> call, Throwable t) {

            }
        });
    }
}
