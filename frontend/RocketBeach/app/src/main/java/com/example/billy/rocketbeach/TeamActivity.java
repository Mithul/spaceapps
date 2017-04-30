package com.example.billy.rocketbeach;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidessence.lib.RichTextView;

public class TeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        LinearLayout layout = (LinearLayout) findViewById(R.id.teamlinear);
        RelativeLayout frame = (RelativeLayout) findViewById(R.id.teamframe);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        WebView zeus_text = (WebView)findViewById(R.id.zeus_text);
        zeus_text.loadUrl("file:///android_res/raw/zeus.html");

        WebView poseidon_text = (WebView)findViewById(R.id.poseidon_text);
        poseidon_text.loadUrl("file:///android_res/raw/poseidon.html");

        layout.getLayoutParams().height = width/2;
    }
}
