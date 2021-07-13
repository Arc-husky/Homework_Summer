package com.example.lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Quiz extends AppCompatActivity {

    private static String Tag = "Quiz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Tag,"onCreate");
        setContentView(R.layout.activity_quiz);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(Tag,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Tag,"onResume");
        Button btn = findViewById(R.id.answer1);
        Intent reward = new Intent(Quiz.this,MyActivity.class);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(reward);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(Tag,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(Tag,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(Tag,"onDestroy");
    }
}