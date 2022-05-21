package com.aliyahatzoff.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.aliyahatzoff.R;

public class Congratulation extends AppCompatActivity {
     Button welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation);
        welcome=findViewById(R.id.welcome);
        welcome.setOnClickListener(v ->{
            Intent intent = new Intent(this, LandingActivity.class);
            startActivity(intent);
        });
    }
}