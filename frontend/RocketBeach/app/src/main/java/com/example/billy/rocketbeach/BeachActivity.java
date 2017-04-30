package com.example.billy.rocketbeach;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.dift.ui.SwipeToAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeachActivity extends AppCompatActivity {

    RecyclerView beaches;
    BeachAdapter adapter;
    SwipeToAction swipeToAction;

    List<Beach> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);

        beaches = (RecyclerView) findViewById(R.id.beaches);
        adapter = new BeachAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        beaches.setLayoutManager(layoutManager);
        beaches.setHasFixedSize(true);
        beaches.setAdapter(adapter);

        RocketBeach service = Utils.getService();

        Map<String, String> options = new HashMap<>();
        options.put("page", "1");

        double[] gps = getIntent().getDoubleArrayExtra("GPS");
        options.put("lat", gps[0] + "");
        options.put("lon", gps[1] + "");

        String token = getSharedPreferences("RocketBeach", 0).getString("X-Auth-Token", "");

        service.listBeaches(options, token).enqueue(new Callback<List<Beach>>() {
            @Override
            public void onResponse(Call<List<Beach>> call, Response<List<Beach>> response) {
                Log.d("RocketBeach", response.code() + "");
                list.addAll(response.body());
                Log.d("RocketBeach", response.body().size() + " ");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Beach>> call, Throwable t) {
                Log.d("RocketBeach", t.getMessage());
            }
        });

        swipeToAction = new SwipeToAction(beaches, new SwipeToAction.SwipeListener<Beach>() {
            @Override
            public boolean swipeLeft(final Beach itemData) {
                return true;
            }

            @Override
            public boolean swipeRight(Beach itemData) {
                return true;
            }

            @Override
            public void onClick(Beach itemData) {
            }

            @Override
            public void onLongClick(Beach itemData) {

            }

        });

    }
}
