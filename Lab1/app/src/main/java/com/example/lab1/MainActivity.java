package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintsChangedListener;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button change = findViewById(R.id.button_change);
        final TextView txt = findViewById(R.id.textView2);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt.setText("我装的");
            }
        });

        Switch sw = findViewById(R.id.switch1);
        final ImageView image = findViewById(R.id.imageView);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) image.setImageResource(R.mipmap.r2);
                else image.setImageResource(R.mipmap.r1);
            }
        });
    }
}