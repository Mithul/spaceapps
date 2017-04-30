package com.example.billy.rocketbeach;

import android.hardware.display.DisplayManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    RocketBeach rocket;
    AboutView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName("Your Full Name")
                .setSubTitle("Mobile Developer")
                .setBrief("I'm warmed of mobile technologies. Ideas maker, curious and nature lover.")
                .setWrapScrollView(true)
                .build();


        rocket = Utils.getService();

        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void mapToView(Beachgoer person) {
        profile = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName(person.email)
                .setSubTitle(person.team_id)
//                .setBrief("I'm warmed of mobile technologies. Ideas maker, curious and nature lover.")
                .setWrapScrollView(true)
                .setShowAsCard(true)
                .build();

        addContentView(profile, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void loadUserInfo() {
        String token = getPreferences(0).getString("X-Auth-Token", "");
        rocket.getMe(token).enqueue(new Callback<Beachgoer>() {
            @Override
            public void onResponse(Call<Beachgoer> call, Response<Beachgoer> response) {
                mapToView(response.body());
            }

            @Override
            public void onFailure(Call<Beachgoer> call, Throwable t) {

            }
        });
    }
}
