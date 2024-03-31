package com.example.projectandroid1;

import static com.example.projectandroid1.AddParking.REQUEST_SELECT_IMAGE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText editTextFullName, editTextPass, editTextInstagramHandle, editTextEmail;
    private DatabaseReference mDatabase;
    private ImageView ProfilePIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPass = findViewById(R.id.editTextPassword);
        editTextInstagramHandle = findViewById(R.id.editTextInstagramHandle);
        editTextEmail = findViewById(R.id.editTextEmail);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        ProfilePIC = findViewById(R.id.profileIMG);
    }

    public void UploaderPhoto(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }
    //update the profile photo to the new the user picked.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            ProfilePIC.setImageURI(selectedImage);

        }
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
        fb.registerAndSaveUser(email, password, full_name, instagram_handle)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult();
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
