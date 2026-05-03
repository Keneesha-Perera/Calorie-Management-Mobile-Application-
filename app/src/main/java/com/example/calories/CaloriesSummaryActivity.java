// CaloriesSummaryActivity.java
package com.example.calories;

import android.os.Bundle;
import android.view.View; // Import View to use visibility constants
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class CaloriesSummaryActivity extends AppCompatActivity {

    private ListView mealsListView;
    private MealViewModel mealViewModel; // ViewModel
    private TextView dailyGoalTextView;
    private TextView currentCaloriesTextView;
    private TextView goalReachedTextView; // Declare the goal reached TextView
    private ProgressBar calorieProgressBar;

    private int dailyCalorieGoal = 2000; // Set your daily calorie goal here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories_summary);

        mealsListView = findViewById(R.id.mealsListView);
        dailyGoalTextView = findViewById(R.id.dailyGoalTextView);
        currentCaloriesTextView = findViewById(R.id.currentCaloriesTextView);
        calorieProgressBar = findViewById(R.id.calorieProgressBar);
        goalReachedTextView = findViewById(R.id.goalReachedTextView); // Initialize the goal reached TextView

        // Initialize the MealRepository with the MealDao obtained from AppDatabase
        MealDao mealDao = AppDatabase.getDatabase(this).mealDao(); // Get MealDao from AppDatabase
        MealRepository mealRepository = new MealRepository(mealDao);

        // Use MealViewModelFactory to create the ViewModel
        MealViewModelFactory factory = new MealViewModelFactory(mealRepository);
        mealViewModel = new ViewModelProvider(this, factory).get(MealViewModel.class);

        // Observe the LiveData from the ViewModel
        mealViewModel.getAllMeals().observe(this, new Observer<List<Meal>>() {
            @Override
            public void onChanged(List<Meal> meals) {
                // Update the UI when the data changes
                MealAdapter adapter = new MealAdapter(CaloriesSummaryActivity.this, meals);
                mealsListView.setAdapter(adapter);

                // Calculate total calorie intake
                int totalCalories = calculateTotalCalories(meals);
                updateCalorieSummary(totalCalories);
            }
        });
    }

    // Method to calculate total calories consumed from meals
    private int calculateTotalCalories(List<Meal> meals) {
        int total = 0;
        for (Meal meal : meals) {
            total += meal.getCalories(); // Assuming you have a getCalories() method in your Meal class
        }
        return total;
    }

    // Method to update the calorie summary UI
    private void updateCalorieSummary(int totalCalories) {
        currentCaloriesTextView.setText("Total Calorie Intake: " + totalCalories + " kcal");
        calorieProgressBar.setMax(dailyCalorieGoal);  // Ensure max is set correctly
        calorieProgressBar.setProgress(totalCalories); // Set the current calories as progress
        dailyGoalTextView.setText("Daily Calorie Goal: " + dailyCalorieGoal + " kcal");

        // Show the goal reached text if the goal is met
        if (totalCalories >= dailyCalorieGoal) {
            goalReachedTextView.setVisibility(View.VISIBLE); // Show the goal reached message
        } else {
            goalReachedTextView.setVisibility(View.GONE); // Hide if not reached
        }
    }
}
