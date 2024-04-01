package com.example.projectandroid1;

import static com.example.projectandroid1.AddParking.REQUEST_SELECT_IMAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText editTextFullName, editTextPass, editTextInstagramHandle, editTextEmail, editTextCity, editTextCountry;
    private DatabaseReference mDatabase;
    private ImageView ProfilePIC;
    private LocationHelper locationHelper;
    private Uri selectedImageUri;

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
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        ProfilePIC = findViewById(R.id.profileIMG);

        locationHelper = new LocationHelper(this);
        locationHelper.checkLocationPermission(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationHelper.getRequestLocationPermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Location location = locationHelper.getLocation();
                if (location != null) {
                    String[] cityCountry = locationHelper.getCityCountryFromLocation(location);
                    if (cityCountry != null) {
                        editTextCity.setText(cityCountry[0]);
                        editTextCountry.setText(cityCountry[1]);
                    }
                }
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


    public void B_Register(View view) {
        final String full_name = editTextFullName.getText().toString();
        final String password = editTextPass.getText().toString();
        final String instagram_handle = editTextInstagramHandle.getText().toString();
        final String email = editTextEmail.getText().toString();

        FireBaseHandler fb = new FireBaseHandler();

        Uri selectedImageUri = this.selectedImageUri;

        fb.uploadImage(selectedImageUri, FirebaseAuth.getInstance().getCurrentUser())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String imageUrl = task.getResult();

                        fb.registerAndSaveUser(email, password, full_name, imageUrl, instagram_handle)
                                .addOnCompleteListener(registerTask -> {
                                    if (registerTask.isSuccessful()) {
                                        FirebaseUser user = registerTask.getResult();
                                        if (user != null) {
                                            Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(Register.this, com.example.projectandroid1.Home.class);
                                            intent.putExtra("username", full_name);
                                            intent.putExtra("userId", user.getUid());
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Exception e = registerTask.getException();
                                    }
                                });
                    } else {
                        Exception e = task.getException();
                    }
                });
    }

    private boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) && username.length() >= 3;
    }

    private boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6 && password.length() <= 14;
    }

    private boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && phone.length() == 10 && TextUtils.isDigitsOnly(phone);
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
