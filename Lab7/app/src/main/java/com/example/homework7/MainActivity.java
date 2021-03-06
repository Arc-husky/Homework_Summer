package com.example.homework7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPic = findViewById(R.id.btn_pic);
        Button btnVideo = findViewById(R.id.btn_video);

        btnPic.setOnClickListener(v -> {
            Intent intent = new Intent(this, pictrueActivity.class);
            startActivity(intent);
        });
        btnVideo.setOnClickListener(v -> {
            Intent intent = new Intent(this, VideoActivity.class);
            startActivity(intent);
        });
    }
}