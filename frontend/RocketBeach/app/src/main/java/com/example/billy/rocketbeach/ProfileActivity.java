package com.example.billy.rocketbeach;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    SnakeView snakeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences preferences = getSharedPreferences("RocketBeach", 0);
        builder = AboutBuilder.with(this);

        String team = preferences.getString("Team", "Team Unknown");

        profile = builder.setPhoto(R.mipmap.profile_picture)
                .setCover(team.contains("Poseidon") ? R.drawable.trident_horizontal : R.drawable.lightning_horizontal)
                .setName("Loading...")
                .setSubTitle(team)
                .setBrief("20HP")
                .build();

        addContentView(profile, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        snakeView = (SnakeView)findViewById(R.id.snake);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fight, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goto_beach:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startActivity(new Intent(getApplicationContext(), LocationAPI23.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), LocationNormal.class));
                }
                break;
        }
        return true;
    }


    @Override
    protected void onStart() {
        loadUserInfo();
        super.onStart();
    }

    public void mapToView(Beachgoer person) {
        snakeView.addValue((float) person.health);
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
