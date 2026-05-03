// MealRepository.java
package com.example.calories;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MealRepository {
    private final MealDao mealDao;
    private final LiveData<List<Meal>> allMeals;

    // Constructor accepting MealDao
    public MealRepository(MealDao mealDao) {
        this.mealDao = mealDao;
        this.allMeals = mealDao.getAllMeals(); // Get LiveData of all meals
    }

    // Method to get all meals
    public LiveData<List<Meal>> getAllMeals() {
        return allMeals;
    }

    // Method to insert a meal
    public void insert(Meal meal) {
        // Use a background thread to insert the meal
        AppDatabase.databaseWriteExecutor.execute(() -> mealDao.insert(meal));
    }

    // Method to update meal photo
    public void updateMealPhoto(int mealId, String photoUri) {
        new Thread(() -> mealDao.updateMealPhoto(mealId, photoUri)).start();
    }
}
