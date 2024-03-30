package com.example.projectandroid1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.editTextTextusername);
        passwordEditText = findViewById(R.id.editTextNumberPassword2);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    public void B_Login(View view) {
        Intent intent = new Intent(Login.this, Home.class);
        startActivity(intent);
        /*
        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            // Username or password is empty, show error
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query the database to retrieve the user's password
        mDatabase.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.getPassword().equals(password)) {
                            // Password matches, login successful
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            intent.putExtra("username", username);
                            intent.putExtra("userId", snapshot.getKey());
                            startActivity(intent);
                            return;
                        }
                    }
                    // Password does not match
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                } else {
                    // User does not exist
                    Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error handling
                Toast.makeText(MainActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });

         */
    }

    public void Register(View view) {
        Intent intent2 = new Intent(this, Register.class);
        startActivity(intent2);
    }
}