package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton make_alarm_btn = findViewById(R.id.make_alarm_btn);
        make_alarm_btn.setImageResource(R.drawable.make_alarm_btn);
        make_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageButton main_btn = findViewById(R.id.main_btn);
        main_btn.setImageResource(R.drawable.main_btn);
        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        ImageButton setting_btn = findViewById(R.id.configuration_btn);
        setting_btn.setImageResource(R.drawable.configuration_btn);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}