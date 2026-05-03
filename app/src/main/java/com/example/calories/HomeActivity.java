package com.example.calories;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button addMealButton; // Button for adding meals
    private Button calorieSummaryButton; // Button for calorie summary
    private Button uploadPhotoButton; // Button for uploading photos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Ensure this matches your layout file name

        // Initialize buttons
        initializeButtons();
    }

    private void initializeButtons() {
        // Initialize the Add Meal Button
        addMealButton = findViewById(R.id.addMealButton);
        addMealButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddMealActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Initialize the Calorie Summary Button
        calorieSummaryButton = findViewById(R.id.calorieSummaryButton);
        calorieSummaryButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CaloriesSummaryActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Initialize the Upload Photo Button
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        uploadPhotoButton.setOnClickListener(v -> {
            // Start PhotoUploadActivity without launching camera
            Intent intent = new Intent(HomeActivity.this, PhotoUploadActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
