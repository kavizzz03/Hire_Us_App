package com.example.hire_me_test.view.actvities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.hire_me_test.R;

public class MainActivity extends AppCompatActivity {

    CardView findJobLayout, giveJobLayout, chatLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findJobLayout = findViewById(R.id.findJobLayout);
        giveJobLayout = findViewById(R.id.giveJobLayout);
        chatLayout = findViewById(R.id.chatLayout); // new chat CardView

        findJobLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FindJobActivity.class));
        });

        giveJobLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GiveJobActivity.class));
        });

        chatLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ChatActivity.class)); // your chat activity
        });
    }
}
