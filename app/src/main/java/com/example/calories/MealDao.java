// MealDao.java
package com.example.calories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface MealDao {

    @Insert
    void insert(Meal meal);

    @Query("SELECT * FROM meal_table")
    LiveData<List<Meal>> getAllMeals();

    @Query("UPDATE meal_table SET photoUri = :photoUri WHERE id = :mealId")
    void updateMealPhoto(int mealId, String photoUri);
}

