package com.example.calories;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a meal with its nutritional information.
 */
@Entity(tableName = "meal_table")
public class Meal {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String foodName;
    private int portionSize;
    private String mealType;
    private int calories;
    private double protein;
    private double fat;
    private double carbohydrates;
    private String photoUri; // Field for photo URI

    // Constructor
    public Meal(String foodName, int portionSize, String mealType, int calories,
                double protein, double fat, double carbohydrates, String photoUri) {
        this.foodName = foodName;
        this.portionSize = portionSize;
        this.mealType = mealType;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.photoUri = photoUri; // Initialize field
    }

    // Default constructor (optional)
    public Meal() {}

    // Getters
    public int getId() { return id; }
    public String getFoodName() { return foodName; }
    public int getPortionSize() { return portionSize; }
    public String getMealType() { return mealType; }
    public int getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
    public double getCarbohydrates() { return carbohydrates; }
    public String getPhotoUri() { return photoUri; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public void setPortionSize(int portionSize) { this.portionSize = portionSize; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setProtein(double protein) { this.protein = protein; }
    public void setFat(double fat) { this.fat = fat; }
    public void setCarbohydrates(double carbohydrates) { this.carbohydrates = carbohydrates; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }

    /**
     * Returns a string representation of the Meal object.
     */
    @Override
    public String toString() {
        return "Meal ID: " + id + "\n" +
                foodName + " (" + portionSize + "g) - " + mealType + "\n" +
                "Calories: " + calories + " kcal, Protein: " + protein + "g, " +
                "Fat: " + fat + "g, Carbs: " + carbohydrates + "g\n" +
                (photoUri != null && !photoUri.isEmpty() ? "Photo: " + photoUri : "No photo available");
    }
}
