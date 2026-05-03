package com.example.calories;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddMealActivity extends AppCompatActivity {

    private EditText foodNameEditText;
    private EditText portionSizeEditText;
    private Spinner mealTypeSpinner;
    private TextView nutritionInfoTextView;
    private Button searchButton;
    private Button logMealButton;

    private EditText caloriesEditText;
    private EditText proteinEditText;
    private EditText fatEditText;
    private EditText carbohydratesEditText;
    private LinearLayout manualInputLayout;

    // Declare nutritional value variables at the class level
    private int calories;
    private double protein;
    private double fat;
    private double carbohydrates;

    private static final String API_KEY = "xRGaC8OvEOjKj3dzXtGGTw==W84fmRsvL4NUmFtw";
    private static final String BASE_URL = "https://api.calorieninjas.com/v1/nutrition?query=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        initViews();
        setupMealTypeSpinner();
        setButtonListeners();

        // Restore nutrition data if available
        if (savedInstanceState != null) {
            calories = savedInstanceState.getInt("calories");
            protein = savedInstanceState.getDouble("protein");
            fat = savedInstanceState.getDouble("fat");
            carbohydrates = savedInstanceState.getDouble("carbohydrates");
            boolean manualInputVisible = savedInstanceState.getBoolean("manualInputVisible");

            // Restore the displayed nutrition information
            if (manualInputVisible) {
                manualInputLayout.setVisibility(View.VISIBLE);
            } else {
                manualInputLayout.setVisibility(View.GONE);
                String nutritionInfo = String.format("Calories: %d kcal\nProtein: %.2f g\nFat: %.2f g\nCarbs: %.2f g",
                        calories, protein, fat, carbohydrates);
                nutritionInfoTextView.setText(nutritionInfo);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save nutritional values
        outState.putInt("calories", calories);
        outState.putDouble("protein", protein);
        outState.putDouble("fat", fat);
        outState.putDouble("carbohydrates", carbohydrates);

        // Save visibility state of the manual input layout
        outState.putBoolean("manualInputVisible", manualInputLayout.getVisibility() == View.VISIBLE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the saved nutritional values
        calories = savedInstanceState.getInt("calories");
        protein = savedInstanceState.getDouble("protein");
        fat = savedInstanceState.getDouble("fat");
        carbohydrates = savedInstanceState.getDouble("carbohydrates");

        boolean manualInputVisible = savedInstanceState.getBoolean("manualInputVisible");

        if (manualInputVisible) {
            manualInputLayout.setVisibility(View.VISIBLE);
        } else {
            manualInputLayout.setVisibility(View.GONE);
            String nutritionInfo = String.format("Calories: %d kcal\nProtein: %.2f g\nFat: %.2f g\nCarbs: %.2f g",
                    calories, protein, fat, carbohydrates);
            nutritionInfoTextView.setText(nutritionInfo);
        }
    }


    private void initViews() {
        foodNameEditText = findViewById(R.id.foodNameEditText);
        portionSizeEditText = findViewById(R.id.portionSizeEditText);
        mealTypeSpinner = findViewById(R.id.mealTypeSpinner);
        nutritionInfoTextView = findViewById(R.id.nutritionInfoTextView);
        searchButton = findViewById(R.id.searchButton);
        logMealButton = findViewById(R.id.logMealButton);

        // Initialize manual input fields and layout
        caloriesEditText = findViewById(R.id.caloriesEditText);
        proteinEditText = findViewById(R.id.proteinEditText);
        fatEditText = findViewById(R.id.fatEditText);
        carbohydratesEditText = findViewById(R.id.carbohydratesEditText);
        manualInputLayout = findViewById(R.id.manualInputLayout);
    }

    private void setupMealTypeSpinner() {
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mealTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);
    }

    private void setButtonListeners() {
        searchButton.setOnClickListener(v -> searchNutrition());
        logMealButton.setOnClickListener(v -> logMeal());
    }

    private void searchNutrition() {
        String foodName = foodNameEditText.getText().toString().trim();

        // Regular expression to allow only letters and spaces
        if (foodName.isEmpty()) {
            Toast.makeText(this, "Please enter a food name.", Toast.LENGTH_SHORT).show();
        } else if (!foodName.matches("[a-zA-Z ]+")) {
            Toast.makeText(this, "Meal name cannot contain numbers or special characters.", Toast.LENGTH_SHORT).show();
        } else {
            OkHttpClient client = new OkHttpClient();
            String url = BASE_URL + foodName;

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("x-api-key", API_KEY)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> handleNetworkError());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        parseNutritionData(responseData);
                    } else {
                        runOnUiThread(() -> handleNoDataFound());
                    }
                }
            });
        }
    }


    private void handleNetworkError() {
        nutritionInfoTextView.setText("Error fetching data.");
        Toast.makeText(AddMealActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
    }

    private void handleNoDataFound() {
        nutritionInfoTextView.setText("No data found.");
        Toast.makeText(AddMealActivity.this, "No nutrition data found for this food.", Toast.LENGTH_SHORT).show();
    }

    private void parseNutritionData(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray items = jsonObject.getJSONArray("items");

            if (items.length() > 0) {
                JSONObject nutritionData = items.getJSONObject(0);
                displayNutritionData(nutritionData);
            } else {
                showManualInputFields();
            }
        } catch (JSONException e) {
            Log.e("JSON_ERROR", e.getMessage(), e);
            showManualInputFields();
        }
    }


    private void displayNutritionData(JSONObject nutritionData) throws JSONException {
        // Fetch nutritional values for 100g portion
        calories = (int) nutritionData.getDouble("calories");
        protein = nutritionData.getDouble("protein_g");
        fat = nutritionData.getDouble("fat_total_g");
        carbohydrates = nutritionData.getDouble("carbohydrates_total_g");

        // Get the portion size from the EditText
        String portionSizeStr = portionSizeEditText.getText().toString().trim();
        int portionSize = 100; // Default to 100 if no input

        if (!portionSizeStr.isEmpty()) {
            try {
                portionSize = Integer.parseInt(portionSizeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid portion size.", Toast.LENGTH_SHORT).show();
                portionSize = 100; // Fallback to 100 if invalid input
            }
        }

        // Adjust nutritional values based on portion size
        adjustNutritionalValuesForPortionSize(portionSize);

        // Display adjusted nutritional information
        String nutritionInfo = String.format("Calories: %d kcal\nProtein: %.2f g\nFat: %.2f g\nCarbs: %.2f g",
                calories, protein, fat, carbohydrates);

        runOnUiThread(() -> {
            nutritionInfoTextView.setText(nutritionInfo);
            manualInputLayout.setVisibility(View.GONE);
        });
    }

    private void adjustNutritionalValuesForPortionSize(int portionSize) {
        // Assuming the nutritional values obtained from the API are for 100g
        double portionMultiplier = portionSize / 100.0;

        calories = (int) (calories * portionMultiplier);
        protein = protein * portionMultiplier;
        fat = fat * portionMultiplier;
        carbohydrates = carbohydrates * portionMultiplier;
    }

    private void logMeal() {
        String foodName = foodNameEditText.getText().toString().trim();
        String portionSizeStr = portionSizeEditText.getText().toString().trim();

        if (foodName.isEmpty() || portionSizeStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int portionSize;
        try {
            portionSize = Integer.parseInt(portionSizeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid portion size.", Toast.LENGTH_SHORT).show();
            return;
        }

        String mealType = mealTypeSpinner.getSelectedItem().toString();
        // Check if manual input is visible to decide where to get nutritional values
        boolean isManualInputVisible = manualInputLayout.getVisibility() == View.VISIBLE;

        if (isManualInputVisible) {
            // Get nutritional values from manual input
            if (!getNutritionalValuesFromManualInput()) {
                return; // Exit if the manual input is invalid
            }
        } else {
            // Adjust the nutritional values based on the portion size
            adjustNutritionalValuesForPortionSize(portionSize);
        }

        // Create meal object with the collected data
        Meal meal = createMealObject(foodName, portionSize, mealType);

        if (meal != null) {
            insertMealIntoDatabase(meal);
        }
    }

    private void showManualInputFields() {
        runOnUiThread(() -> {
            nutritionInfoTextView.setText("No nutrition data found. Please enter manually.");
            manualInputLayout.setVisibility(View.VISIBLE);
        });
    }



    private boolean getNutritionalValuesFromManualInput() {
        try {
            // Get values from EditTexts and assign them to the class variables
            calories = Integer.parseInt(caloriesEditText.getText().toString().trim());
            protein = Double.parseDouble(proteinEditText.getText().toString().trim());
            fat = Double.parseDouble(fatEditText.getText().toString().trim());
            carbohydrates = Double.parseDouble(carbohydratesEditText.getText().toString().trim());

            // Optionally, you can set the values to your fields if needed here
            return true; // Successful parsing
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid nutritional values.", Toast.LENGTH_SHORT).show();
            return false; // Invalid input
        }
    }

    private Meal createMealObject(String foodName, int portionSize, String mealType) {
        String photoUri = ""; // Default value for photoUri
        // Now include the values collected, whether from API or manual input
        return new Meal(foodName, portionSize, mealType, calories, protein, fat, carbohydrates, photoUri);
    }


    private boolean getNutritionalValuesFromNutritionInfo(String[] nutritionInfo) {
        for (String info : nutritionInfo) {
            if (info.startsWith("Calories:")) {
                String calorieString = info.replace("Calories: ", "").replace(" kcal", "").trim();
                calories = Integer.parseInt(calorieString);
            } else if (info.startsWith("Protein:")) {
                String proteinString = info.replace("Protein: ", "").replace(" g", "").trim();
                protein = Double.parseDouble(proteinString);
            } else if (info.startsWith("Fat:")) {
                String fatString = info.replace("Fat: ", "").replace(" g", "").trim();
                fat = Double.parseDouble(fatString);
            } else if (info.startsWith("Carbs:")) {
                String carbsString = info.replace("Carbs: ", "").replace(" g", "").trim();
                carbohydrates = Double.parseDouble(carbsString);
            }
        }
        return true; // Successful parsing
    }

    private void insertMealIntoDatabase(Meal meal) {
        // Get the database instance
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        // Use a background thread to insert the meal into the database
        new Thread(() -> {
            // Insert meal into the database
            db.mealDao().insert(meal);

            // Notify the user that the meal was successfully logged (run on UI thread)
            runOnUiThread(() -> {
                Toast.makeText(AddMealActivity.this, "Meal logged successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to previous activity or clear fields
            });
        }).start();
    }


}
