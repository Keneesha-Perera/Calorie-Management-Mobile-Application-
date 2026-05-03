// MealViewModelFactory.java
package com.example.calories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MealViewModelFactory implements ViewModelProvider.Factory {
    private final MealRepository mealRepository;

    // Constructor to accept the repository
    public MealViewModelFactory(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MealViewModel.class)) {
            return (T) new MealViewModel(mealRepository); // Pass the repository to the ViewModel
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
