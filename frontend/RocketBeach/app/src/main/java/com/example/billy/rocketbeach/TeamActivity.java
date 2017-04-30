package com.example.billy.rocketbeach;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidessence.lib.RichTextView;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_affliation);
        final SwipeSelector sizeSelector = (SwipeSelector) findViewById(R.id.toppingSelector);

        SwipeItem zeus = new SwipeItem(
                "1",
                "Team Zeus",
                "Join the team of the great god of lightning and may he keep his lightning clouds guarding you from the wrath of Helios sun"
        );

        SwipeItem poseidon = new SwipeItem(
                "2",
                "Team Poseidon",
                "Arch nemesis of Helios, Poseidon shall keep you safe in his warm waters"
        );

        sizeSelector.setItems(zeus, poseidon);

        sizeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {
                int badge_id = item.value == "1"
                        ? R.drawable.zeus
                        : R.drawable.poseidon;
                sizeSelector.setBackgroundResource(badge_id);
            }
        });

//        setContentView(R.layout.activity_team);
//
//        LinearLayout layout = (LinearLayout) findViewById(R.id.teamlinear);
//        RelativeLayout frame = (RelativeLayout) findViewById(R.id.teamframe);
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//
//        WebView zeus_text = (WebView)findViewById(R.id.zeus_text);
//        zeus_text.loadUrl("file:///android_res/raw/zeus.html");
//
//        WebView poseidon_text = (WebView)findViewById(R.id.poseidon_text);
//        poseidon_text.loadUrl("file:///android_res/raw/poseidon.html");
//
//        layout.getLayoutParams().height = width/2;
    }
}
