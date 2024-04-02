package com.example.projectandroid1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = findViewById(R.id.editTextEmailLogin);
        passwordEditText = findViewById(R.id.editTextNumberPassword2);

        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        if (fireBaseHandler.getmAuth().getCurrentUser() != null) {
            FirebaseUser user = fireBaseHandler.getmAuth().getCurrentUser();
            Intent intent = new Intent(this, Home.class);
            fireBaseHandler.getUserData(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    JSONObject userJson = task.getResult();
                    intent.putExtra("user", userJson.toString());
                    startActivity(intent);
                }
            });
        }
    }

    public void B_Login(View view) {
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        fireBaseHandler.loginUser(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult();
                if (user != null) {
                    Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, Home.class);
                    fireBaseHandler.getUserData(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            JSONObject userJson = task1.getResult();
                            intent.putExtra("user", userJson.toString());
                            startActivity(intent);
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Register(View view) {
        Intent intent2 = new Intent(this, Register.class);
        startActivity(intent2);
    }
}