package com.example.calories;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;

import java.util.List;
import android.provider.MediaStore;

public class MealAdapter extends ArrayAdapter<Meal> {
    private final Context context;
    private List<Meal> mealList;

    public MealAdapter(Context context, List<Meal> meals) {
        super(context, 0, meals);
        this.context = context;
        this.mealList = meals;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.meal_item, parent, false);
        }

        // Get the Meal object for this position
        Meal meal = mealList.get(position);

        // Lookup view for data population
        TextView mealIdTextView = convertView.findViewById(R.id.mealIdTextView);
        TextView mealNameTextView = convertView.findViewById(R.id.mealNameTextView);
        TextView mealDetailsTextView = convertView.findViewById(R.id.mealDetailsTextView);
        ImageView mealImageView = convertView.findViewById(R.id.mealImageView);

        // Populate the data into the template view using the Meal object
        mealIdTextView.setText("Meal ID: " + meal.getId());
        mealNameTextView.setText(meal.getFoodName());

        // Display additional meal details
        String details = String.format("%d g - %s\nCalories: %d, Protein: %.2f g, Fat: %.2f g, Carbs: %.2f g",
                meal.getPortionSize(), meal.getMealType(), meal.getCalories(),
                meal.getProtein(), meal.getFat(), meal.getCarbohydrates());
        mealDetailsTextView.setText(details);

        // Load the meal photo if available
        if (meal.getPhotoUri() != null && !meal.getPhotoUri().isEmpty()) {
            try {
                Uri photoUri = Uri.parse(meal.getPhotoUri());
                Glide.with(context)
                        .load(photoUri)
                        .placeholder(R.drawable.screen) // Placeholder image while loading
                        .error(R.drawable.meal1) // Error image if loading fails
                        .into(mealImageView); // Load into ImageView
                mealImageView.setVisibility(View.VISIBLE); // Show the ImageView if photo is available
            } catch (Exception e) {
                e.printStackTrace();
                mealImageView.setVisibility(View.GONE); // Hide ImageView on error
            }
        } else {
            mealImageView.setVisibility(View.GONE); // Hide the ImageView if no photo exists
        }


        // Return the completed view to render on screen
        return convertView;
    }

    public void updateMeals(List<Meal> newMeals) {
        mealList.clear();
        mealList.addAll(newMeals);
        notifyDataSetChanged();
    }
}
