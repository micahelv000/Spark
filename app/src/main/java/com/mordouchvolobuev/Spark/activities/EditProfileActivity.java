package com.mordouchvolobuev.Spark.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.mordouchvolobuev.Spark.firebase.FireBaseHandler;
import com.mordouchvolobuev.Spark.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editTextFullName;
    private EditText editTextInstagramHandle;
    private EditText editTextCity;
    private EditText editTextCountry;
    private ImageView ProfilePIC;
    private Uri selectedImageUri;
    Intent intent;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextInstagramHandle = findViewById(R.id.editTextInstagramHandle);
        editTextCity = findViewById(R.id.editTextCity);
        editTextCountry = findViewById(R.id.editTextCountry);
        ProfilePIC = findViewById(R.id.profileIMG);
        intent = getIntent();
        String userDataString = intent.getStringExtra("user");
        JSONObject userData;
        try {
            userData = new JSONObject(Objects.requireNonNull(userDataString));

            editTextFullName.setText(userData.getString("full_name"));
            editTextInstagramHandle.setText(userData.getString("instagram_handle"));
            JSONObject location = userData.getJSONObject("location");
            editTextCity.setText(location.getString("city"));
            editTextCountry.setText(location.getString("country"));
            if (userData.has("profile_picture") && !userData.getString("profile_picture").isEmpty()) {
                Picasso.get().load(userData.getString("profile_picture")).placeholder(R.drawable.progress_animation)
                        .error(R.drawable.default_profile).into(ProfilePIC);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
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

    public void UploaderPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mGetContent.launch(intent);
    }

    private boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) && username.length() >= 3;
    }

    private boolean isValidIG(String instagramHandle) {

        return (instagramHandle.length() > 2 && instagramHandle.charAt(0) != '@');
    }

    private boolean isValidCountry(String country) {
        return (country.length() > 2);
    }

    private boolean isValidCity(String city) {
        return (city.length() > 2);
    }

    public void B_Register(View view) {
        final String full_name = editTextFullName.getText().toString();
        final String instagram_handle = editTextInstagramHandle.getText().toString();
        final String city = editTextCity.getText().toString();
        final String country = editTextCountry.getText().toString();

        // Reset flag
        flag = false;

        // Validate inputs
        if (!isValidUsername(full_name)) {
            editTextFullName.setError("Invalid Full name");
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

        FireBaseHandler fb = new FireBaseHandler();

        Uri selectedImageUri = this.selectedImageUri;
        fb.updateUserData(full_name, "", instagram_handle, city, country, selectedImageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent1 = new Intent(this, MainActivity.class);
                        startActivity(intent1);
                    }
                });
    }

}
