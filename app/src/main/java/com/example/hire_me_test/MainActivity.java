package com.example.hire_me_test;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    CardView findJobLayout, giveJobLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findJobLayout = findViewById(R.id.findJobLayout);
        giveJobLayout = findViewById(R.id.giveJobLayout);

        findJobLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FindJobActivity.class));
        });

        giveJobLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GiveJobActivity.class));
        });
    }
}
