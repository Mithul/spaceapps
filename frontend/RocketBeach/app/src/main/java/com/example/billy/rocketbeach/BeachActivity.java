package com.example.billy.rocketbeach;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.dift.ui.SwipeToAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://81.4.105.95:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RocketBeach service = retrofit.create(RocketBeach.class);

        Map<String, String> options = new HashMap<>();
        options.put("page", "1");

        service.addUser("teq@ag.com", "123456", "123456").enqueue(new Callback<Beachgoer>() {
            @Override
            public void onResponse(Call<Beachgoer> call, Response<Beachgoer> response) {
                try {
                    Log.d("TEST", response.code() + "");
                    Beachgoer token = response.body();
                    Log.d("TEST", token.auth_token);
                } catch (Exception e) {
                    Log.d("TEST", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Beachgoer> call, Throwable t) {
                Log.d("TEST", t.getMessage() + " ");
            }
        });


//        service.listBeaches(options, AccessToken.makeToken("fbea86a136e44d649970fe2effc645b7")).enqueue(new Callback<List<Beach>>() {
//            @Override
//            public void onResponse(Call<List<Beach>> call, Response<List<Beach>> response) {
//                Log.d("TEST", response.code() + "");
//                list.addAll(response.body());
//                Log.d("TEST", response.body().size() + " ");
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(Call<List<Beach>> call, Throwable t) {
//                Log.d("TEST", t.getMessage());
//            }
//        });

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
