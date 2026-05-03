package com.example.calories;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the button and set an onClickListener using a lambda expression
        Button getStartedButton = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener(v -> {
            // Create an intent to start the Home Activity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            // Optionally add a transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
