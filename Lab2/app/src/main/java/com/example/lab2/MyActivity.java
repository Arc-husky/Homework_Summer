package com.example.lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Log.i("MyActivity","onCreate");

        Button btn = findViewById(R.id.button);
        Intent back = new Intent(MyActivity.this,MainActivity.class);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(back);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MyActivity","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MyActivity","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MyActivity","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MyActivity","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MyActivity","onDestroy");
    }
}