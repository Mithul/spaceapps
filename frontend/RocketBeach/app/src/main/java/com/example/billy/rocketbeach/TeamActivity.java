package com.example.billy.rocketbeach;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamActivity extends AppCompatActivity {

    CarouselView customCarouselView;
    View mProgressView;
    Button joinTeamBtn;

    private class Team {
        int image;
        int id;
        String name;
        String info;

        Team(int id, int image, String name, String info) {
            this.id = id;
            this.image = image;
            this.name = name;
            this.info = info;
        }
    }

    Team[] teams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_affliation);

        teams = new Team[]{
                new Team(2, R.drawable.zeus, "Team Zeus", "Join the team of the great god of lightning and may he keep his lightning clouds guarding you from the wrath of Helios sun"),
                new Team(1, R.drawable.poseidon, "Team Poseidon", "Arch nemesis of Helios, Poseidon shall keep you safe in his warm waters")
        };

        mProgressView = findViewById(R.id.team_progress);

        customCarouselView = (CarouselView) findViewById(R.id.teams);
        customCarouselView.setPageCount(teams.length);
        // set ViewListener for custom view
        customCarouselView.setViewListener(viewListener);

        joinTeamBtn = (Button) findViewById(R.id.join_team);
    }

    @Override
    protected void onStart() {
        checkLogin();
        super.onStart();
    }

    public void associateToTeam(View v) {
        checkLogin();
        final int item = customCarouselView.getCurrentItem();
        showProgress(true);
        Utils.getService()
                .associateWithTeam(
                        teams[item].id,
                        getSharedPreferences("RocketBeach", 0).getString("X-Auth-Token", "")
                )
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        showProgress(false);
                        Utils.addToken(getSharedPreferences("RocketBeach", 0), "Team", teams[item].name);
                        startActivity(new Intent(getApplicationContext(), LocationFiller.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        showProgress(false);
                        Toast.makeText(getApplicationContext(), "Some problem occured. Try again!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    ViewListener viewListener = new ViewListener() {

        @Override
        public View setViewForPosition(final int position) {
            View customView = getLayoutInflater().inflate(R.layout.team, null);

            customView.findViewById(R.id.badge).setBackgroundResource(teams[position].image) ;

            //set view attributes here
            ((TextView) customView.findViewById(R.id.team_name)).setText(teams[position].name);
            ((TextView) customView.findViewById(R.id.team_info)).setText(teams[position].info);

            return customView;
        }
    };

    private void checkLogin() {
        if (getSharedPreferences("RocketBeach", 0).contains("Team")) {
            startActivity(new Intent(getApplicationContext(), LocationFiller.class));
            finish();
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        final int view = show ? View.GONE : View.VISIBLE;
        joinTeamBtn.setVisibility(view);
        joinTeamBtn.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        joinTeamBtn.setVisibility(view);
                    }
                });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }
}

