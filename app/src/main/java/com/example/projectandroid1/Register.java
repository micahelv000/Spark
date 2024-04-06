package com.example.projectandroid1;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Register extends AppCompatActivity {

    private EditText editTextFullName, editTextPass, editTextInstagramHandle, editTextEmail, editTextCity, editTextCountry;
    private ImageView ProfilePIC;
    private LocationHelper locationHelper;
    private Uri selectedImageUri;
    private Location user_location;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPass = findViewById(R.id.editTextPassword);
        editTextInstagramHandle = findViewById(R.id.editTextInstagramHandle);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCity = findViewById(R.id.editTextCity);
        editTextCountry = findViewById(R.id.editTextCountry);
        ProfilePIC = findViewById(R.id.profileIMG);

        locationHelper = new LocationHelper(this);
        if (locationHelper.isLocationPermissionMissing()) {
            locationHelper.checkLocationPermission();
        } else {
            locationHelper.LocationUpdater(editTextCity, editTextCountry);
        }
    }

    public void updateLocation(){
        user_location = locationHelper.getLocation();
        if (user_location != null) {
            String[] cityCountry = locationHelper.getCityCountryFromLocation(user_location);
            if (cityCountry != null) {
                editTextCity.setText(cityCountry[0]);
                editTextCountry.setText(cityCountry[1]);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationHelper.getRequestLocationPermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            }

        }
    }

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageUri = data.getData();
                            ProfilePIC.setImageURI(selectedImageUri);
                        }
                    }
                }
            });

    public void UploaderPhoto(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mGetContent.launch(intent);
    }
    public void Login(View view) {
        Intent intent = new Intent(this, com.example.projectandroid1.Login.class);
        startActivity(intent);
    }

    private boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) && username.length() >= 3;
    }

    private boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6 && password.length() <= 14;
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidIG(String instagramHandle) {
        return(instagramHandle.length() > 2);
    }
    private boolean isValidCountry(String country) {
        return(country.length() > 2);
    }
    private boolean isValidCity(String city) {
        return(city.length() > 2);
    }
    public void B_Register(View view) {
        final String full_name = editTextFullName.getText().toString();
        final String password = editTextPass.getText().toString();
        final String instagram_handle = editTextInstagramHandle.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String city = editTextCity.getText().toString();
        final String country = editTextCountry.getText().toString();

        // Reset flag
        flag = false;

        // Validate inputs
        if (!isValidUsername(full_name)) {
            editTextFullName.setError("Invalid Full name");
            flag = true;
        }

        if (!isValidPassword(password)) {
            editTextPass.setError("Password must be between 6 and 14 characters long");
            flag = true;
        }

        if (!isValidEmail(email)) {
            editTextEmail.setError("Invalid email format");
            flag = true;
        }

        if (!isValidIG(instagram_handle)) {
            editTextInstagramHandle.setError("Invalid instagram handle");
            flag = true;
        }
        if (!isValidCity(city)) {
            editTextCity.setError("Invalid instagram handle");
            flag = true;
        }
        if (!isValidCountry(country)) {
            editTextCountry.setError("Invalid instagram handle");
            flag = true;
        }


        // If any validation failed, return
        if (flag) {
            return;
        }

        // Location handling...
        if (user_location == null) {
            user_location = locationHelper.getLocationFromCityCountry(city, country);
        }

        FireBaseHandler fb = new FireBaseHandler();

        Uri selectedImageUri = this.selectedImageUri;

        fb.registerAndSaveUser(email, password, full_name, instagram_handle, user_location, city, country)
                .addOnCompleteListener(registerTask -> {
                    if (registerTask.isSuccessful()) {
                        FirebaseUser user = registerTask.getResult();
                        if (user != null) {
                            fb.updateUserImage(selectedImageUri, user)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(Register.this, com.example.projectandroid1.LayoutFragments.class);
                                            fb.getUserData(user).addOnCompleteListener(userDataTask -> {
                                                if (userDataTask.isSuccessful()) {
                                                    intent.putExtra("user", userDataTask.getResult().toString());
                                                    startActivity(intent);
                                                }
                                            });
                                        } else {
                                            Toast.makeText(Register.this, "Image upload failed", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Register.this, "Registration failed: " + Objects.requireNonNull(registerTask.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }



}
