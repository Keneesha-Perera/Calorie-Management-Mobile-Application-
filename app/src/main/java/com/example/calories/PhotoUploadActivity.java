package com.example.calories;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class PhotoUploadActivity extends AppCompatActivity {

    private static final String TAG = "PhotoUploadActivity"; // For logging
    private static final int CAMERA_PERMISSION_CODE = 100;
    private List<Meal> meals;
    private EditText mealIdEditText;
    private ImageView photoImageView;
    private Button uploadButton;
    private Button selectPhotoButton;
    private ListView mealsListView;
    private Uri photoUri;
    private MealDao mealDao;
    private MealAdapter mealAdapter;
    private Button takePhotoButton;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ProgressBar progressBar;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the photoUri if it exists
        if (photoUri != null) {
            outState.putString("photoUri", photoUri.toString());
        }

        // Save the mealId entered in the EditText
        outState.putString("mealId", mealIdEditText.getText().toString());

        // Save the progress of the ProgressBar if it's visible
        if (progressBar.getVisibility() == View.VISIBLE) {
            outState.putInt("progressBarValue", progressBar.getProgress());
        }

        // Save the visibility state of the ImageView
        outState.putInt("photoImageViewVisibility", photoImageView.getVisibility());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the photoUri
        if (savedInstanceState.containsKey("photoUri")) {
            photoUri = Uri.parse(savedInstanceState.getString("photoUri"));
            Glide.with(this)
                    .load(photoUri)
                    .into(photoImageView);
            photoImageView.setVisibility(View.VISIBLE);
        }

        // Restore the mealId entered in the EditText
        if (savedInstanceState.containsKey("mealId")) {
            mealIdEditText.setText(savedInstanceState.getString("mealId"));
        }

        // Restore the progress of the ProgressBar
        if (savedInstanceState.containsKey("progressBarValue")) {
            int progressValue = savedInstanceState.getInt("progressBarValue");
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progressValue);
        }

        // Restore the visibility state of the ImageView
        if (savedInstanceState.containsKey("photoImageViewVisibility")) {
            photoImageView.setVisibility(savedInstanceState.getInt("photoImageViewVisibility"));
        }
    }



    private final ActivityResultLauncher<Intent> photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        photoUri = result.getData().getData();
                        if (photoUri != null) {
                            // Use Glide to load the image into the ImageView
                            Glide.with(this)
                                    .load(photoUri)
                                    .into(photoImageView);
                            photoImageView.setVisibility(View.VISIBLE);
                        } else {
                            showError("Unable to retrieve photo URI.");
                        }
                    } catch (Exception e) {
                        showError("Error retrieving photo: " + e.getMessage());
                        Log.e(TAG, "Error retrieving photo: ", e);
                    }
                } else {
                    showError("Photo selection failed.");
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Photo taken successfully, load the image into the ImageView
                    if (photoUri != null) {
                        Glide.with(this)
                                .load(photoUri)
                                .into(photoImageView);
                        photoImageView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e(TAG, "Camera action failed with result code: " + result.getResultCode());
                    showError("Camera action failed.");
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        // Initialize UI components
        mealIdEditText = findViewById(R.id.mealIdEditText);
        photoImageView = findViewById(R.id.photoImageView);
        uploadButton = findViewById(R.id.uploadButton);
        selectPhotoButton = findViewById(R.id.selectPhotoButton);
        mealsListView = findViewById(R.id.mealsListView);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        progressBar = findViewById(R.id.progressBar);

        // Initialize DAO and adapter
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        mealDao = db.mealDao();

        meals = new ArrayList<>();
        mealAdapter = new MealAdapter(this, meals);
        mealsListView.setAdapter(mealAdapter);

        // Initialize Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("meal_photos");

        // Set up listeners
        initializeButtons();
        loadMeals();
    }

    private void initializeButtons() {
        // Select photo from gallery
        selectPhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            photoPickerLauncher.launch(intent);
        });

        // Take a photo using the camera
        takePhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera(); // Call method to open the camera
            }
        });

        uploadButton.setOnClickListener(v -> {
            String mealIdStr = mealIdEditText.getText().toString();
            if (photoUri == null) {
                showError("Please select a photo first.");
                return;
            }
            if (mealIdStr.isEmpty()) {
                showError("Please enter a Meal ID.");
                return;
            }

            int mealId;
            try {
                mealId = Integer.parseInt(mealIdStr);
            } catch (NumberFormatException e) {
                showError("Invalid Meal ID. Please enter a number.");
                Log.e(TAG, "Invalid Meal ID: ", e);
                return;
            }

            boolean mealExists = false;
            for (Meal meal : meals) {
                if (meal.getId() == mealId) {
                    mealExists = true;
                    break;
                }
            }

            if (!mealExists) {
                showError("Meal ID " + mealId + " does not exist. Please enter a valid Meal ID.");
                return;
            }
            // Start the upload process to Firebase Storage
            uploadPhotoToFirebase(mealId, photoUri);

            String photoUriString = photoUri.toString();

            // Update the photo URI in the database
            new Thread(() -> {
                try {
                    mealDao.updateMealPhoto(mealId, photoUriString);
                    runOnUiThread(() -> {
                        loadMeals();
                        photoImageView.setImageURI(photoUri);
                        photoImageView.setVisibility(View.VISIBLE);
                        showSuccess("Photo uploaded for Meal ID: " + mealId);
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> showError("Error uploading photo: " + e.getMessage()));
                    Log.e(TAG, "Error uploading photo: ", e);
                }
            }).start();
        });


    }


    private void openCamera() {
        // Create an intent to open the camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraLauncher.launch(intent);
        } else {
            showError("Error creating image file.");
        }
    }



    private void loadMeals() {
        mealDao.getAllMeals().observe(this, meals -> {
            if (meals != null) {
                updateMeals(meals);
            } else {
                showError("No meals found.");
            }
        });
    }

    private void handlePhotoResult(Uri uri) {
        // Handle the selected or captured photo and decode it in the background
        new Thread(() -> {
            Bitmap bitmap = decodeUri(uri); // Decode the image off the main thread
            if (bitmap != null) {
                runOnUiThread(() -> {
                    photoImageView.setImageBitmap(bitmap);
                    photoImageView.setVisibility(View.VISIBLE);
                });
            } else {
                runOnUiThread(() -> showError("Error decoding photo."));
            }
        }).start();
    }


    public void updateMeals(List<Meal> newMeals) {
        mealAdapter.updateMeals(newMeals);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Bitmap decodeUri(Uri uri) {
        try {
            InputStream input = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Get image dimensions without allocating memory
            BitmapFactory.decodeStream(input, null, options);
            input.close();

            // Calculate sample size to reduce memory usage
            options.inSampleSize = calculateInSampleSize(options, 800, 800);
            options.inJustDecodeBounds = false;

            input = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            input.close();
            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "Error decoding image: ", e);
            return null;
        }
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera(); // Permission granted, open the camera
            } else {
                showError("Camera permission denied.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void uploadPhotoToFirebase(int mealId, Uri photoUri) {
        // Create a reference to the photo in Firebase Storage
        StorageReference photoRef = storageReference.child(mealId + "_" + System.currentTimeMillis() + ".jpg");

        // Show ProgressBar before starting upload
        progressBar.setVisibility(View.VISIBLE);

        // Upload the photo
        UploadTask uploadTask = photoRef.putFile(photoUri);

        // Monitor the progress of the upload
        uploadTask.addOnProgressListener(taskSnapshot -> {
            // Calculate and update the progress
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            //progressBar.setProgress((int) progress);
            animateProgressBar((int) progress);
        }).addOnSuccessListener(taskSnapshot -> {
            // Get the download URL and update the database on success
            photoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                String photoUriString = downloadUri.toString();

                // Log success and photo URL (you can log or use this URL to verify)
                Log.d(TAG, "Photo uploaded successfully to the Firebase cloud storage. Download URL: " + photoUriString);
                // Update the photo URI in the database in a background thread
                new Thread(() -> {
                    try {
                        mealDao.updateMealPhoto(mealId, photoUriString);

                        // Run UI updates on the main thread
                        runOnUiThread(() -> {
                            loadMeals(); // Reload the meals list to reflect changes
                            photoImageView.setImageURI(photoUri); // Show the uploaded image
                            photoImageView.setVisibility(View.VISIBLE);
                            showSuccess("Photo uploaded to the cloud storage successfully for Meal ID: " + mealId);
                            //progressBar.setVisibility(View.GONE); // Hide the ProgressBar
                            hideProgressBar();
                        });

                    } catch (Exception e) {
                        // Handle any exceptions during the database update
                        runOnUiThread(() -> {
                            showError("Error updating photo in the database: " + e.getMessage());
                            hideProgressBar();
                            //progressBar.setVisibility(View.GONE); // Hide ProgressBar on failure
                        });
                        Log.e(TAG, "Error updating photo in the database: ", e);
                    }
                }).start();
            }).addOnFailureListener(e -> {
                // Handle any failures when getting the download URL
                showError("Error getting download URL: " + e.getMessage());
                Log.e(TAG, "Error getting download URL: ", e);
                hideProgressBar();
                //progressBar.setVisibility(View.GONE); // Hide ProgressBar on failure
            });

        }).addOnFailureListener(e -> {
            // Handle any failures during the upload
            showError("Error uploading photo: " + e.getMessage());
            Log.e(TAG, "Error uploading photo: ", e);
            hideProgressBar();
            //progressBar.setVisibility(View.GONE); // Hide ProgressBar on failure
        });
    }
    private void animateProgressBar(int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progress);
        animation.setDuration(2000); // Duration in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }
    private void hideProgressBar() {
        progressBar.animate().alpha(0f).setDuration(500).withEndAction(() -> {
            progressBar.setVisibility(View.GONE);
            progressBar.setAlpha(1f); // Reset alpha
        });
    }



}
