package com.example.projectandroid1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    private EditText editTextUserName, editTextPass, editTextPhone, editTextEmail;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPass = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    public void Login(View view) {
        Intent intent = new Intent(this, com.example.projectandroid1.MainActivity.class);
        startActivity(intent);
    }

    public void B_Register(View view) {
        final String username = editTextUserName.getText().toString();
        final String password = editTextPass.getText().toString();
        final String phone = editTextPhone.getText().toString();
        final String email = editTextEmail.getText().toString();

        // Check if username is taken
        mDatabase.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username already exists
                    Toast.makeText(Register.this, "Username already taken", Toast.LENGTH_SHORT).show();
                } else {
                    // Username is available, proceed with other validations
                    if (isValidUsername(username) && isValidPassword(password) && isValidPhone(phone) && isValidEmail(email)) {
                        // Push user details to Firebase Realtime Database
                        String userId = mDatabase.push().getKey();
                        User user = new User(username, email, phone, password);
                        mDatabase.child(userId).setValue(user);

                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG).show();

                        // Start Home activity or perform any other desired action
                        Intent intent = new Intent(Register.this, com.example.projectandroid1.Home.class);
                        intent.putExtra("username", username);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    } else {
                        // Validation failed, display a message to the user
                        if (!isValidUsername(username)) {
                            Toast.makeText(Register.this, "Username must be at least 3 characters long", Toast.LENGTH_SHORT).show();
                        } else if (!isValidPassword(password)) {
                            Toast.makeText(Register.this, "Password must be between 6 and 14 characters long", Toast.LENGTH_SHORT).show();
                        } else if (!isValidPhone(phone)) {
                            Toast.makeText(Register.this, "Please enter a valid phone number (10 digits)", Toast.LENGTH_SHORT).show();
                        } else if (!isValidEmail(email)) {
                            Toast.makeText(Register.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(Register.this, "Database error", Toast.LENGTH_SHORT).show();
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
