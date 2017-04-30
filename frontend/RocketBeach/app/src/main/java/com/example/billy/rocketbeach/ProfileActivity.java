package com.example.billy.rocketbeach;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.txusballesteros.SnakeView;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    AboutView profile;
    AboutBuilder builder;
    View name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences preferences = getSharedPreferences("RocketBeach", 0);
        builder = AboutBuilder.with(this);

        profile = builder.setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName("Loading...")
                .setSubTitle(preferences.getString("BannerTeam", "BannerTeam Unknown"))
                .setBrief("20HP")
                .build();

        addContentView(profile, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        final SnakeView snakeView = (SnakeView)findViewById(R.id.snake);

        snakeView.addValue(10);
        snakeView.addValue(40);
        snakeView.addValue(60);
        snakeView.addValue(40);
        snakeView.addValue(70);
        snakeView.addValue(80);
        snakeView.addValue(20);


    }

    @Override
    protected void onStart() {
        loadUserInfo();
        super.onStart();
    }

    public void mapToView(Beachgoer person) {
        profile = builder
                .setName(person.email)
                .setBrief(person.health + "HP")
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
