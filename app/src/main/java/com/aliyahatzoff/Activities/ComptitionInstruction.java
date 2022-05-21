package com.aliyahatzoff.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyahatzoff.R;

public class ComptitionInstruction extends AppCompatActivity {
TextView comptition_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comptition_instruction);
        comptition_type=findViewById(R.id.comptition_type);
        comptition_type.setText(getIntent().getStringExtra("intrest"));
        Button button=findViewById(R.id.register_now);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ComptitionInstruction.this,ComptitionForm.class);
                intent.putExtra("intrest",getIntent().getStringExtra("intrest"));
                intent.putExtra("pricing",getIntent().getStringExtra("price"));
                startActivity(intent);
            }
        });
    }
}