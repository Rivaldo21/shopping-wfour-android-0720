package com.wfour.onlinestoreapp.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;


import androidx.appcompat.app.AppCompatActivity;

import com.wfour.onlinestoreapp.R;

public class TestingActivity extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String url="https://www.youtube.com/watch?v=TQTlCHxyuu8";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + "TQTlCHxyuu8"));
                startActivity(intent);
//                startActivity(new Intent(TestingActivity.this, MainActivity.class));
            }
        }, 5000);

    }
}
