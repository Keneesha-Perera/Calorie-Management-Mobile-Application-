// AppDatabase.java
package com.example.calories;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Meal.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MealDao mealDao();

    // Create a singleton instance of the database
    private static volatile AppDatabase INSTANCE;

    // Create an ExecutorService for database operations
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4); // Use a thread pool

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "meal_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
