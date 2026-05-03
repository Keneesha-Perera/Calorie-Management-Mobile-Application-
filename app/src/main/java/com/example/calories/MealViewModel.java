package com.example.calories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

/**
 * ViewModel for managing meal data in the UI.
 */
public class MealViewModel extends ViewModel {
    private final MealRepository mealRepository;
    private final LiveData<List<Meal>> allMeals;

    // Constructor that accepts MealRepository
    public MealViewModel(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
        this.allMeals = mealRepository.getAllMeals(); // Get meals from the repository
    }

    /**
     * Returns the LiveData list of all meals.
     */
    public LiveData<List<Meal>> getAllMeals() {
        return allMeals; // Expose the LiveData to the UI
    }

    /**
     * Inserts a new meal into the repository.
     *
     * @param meal The meal to insert.
     */
    public void insert(Meal meal) {
        mealRepository.insert(meal);
    }

    /**
     * Updates the photo URI for a meal.
     *
     * @param mealId The ID of the meal to update.
     * @param photoUri The new photo URI.
     */
    public void updateMealPhoto(int mealId, String photoUri) {
        mealRepository.updateMealPhoto(mealId, photoUri);
    }
}
