package com.mordouchvolobuev.Spark.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.mordouchvolobuev.Spark.firebase.FireBaseHandler;
import com.mordouchvolobuev.Spark.R;

import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    boolean isInputValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInputValid = true;
        if (!isConnectedToInternet()) {
            Intent intent = new Intent(this, NoInternet.class);
            startActivity(intent);
            return;
        }

        setContentView(R.layout.activity_login);
        emailEditText = findViewById(R.id.editTextEmailLogin);
        passwordEditText = findViewById(R.id.editTextNumberPassword2);

    }

    boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) Login.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network == null)
                return false;
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    public void B_Login(View view) {
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please enter your email");
            isInputValid = false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter your password");
            isInputValid = false;
        }
        if (!isInputValid) {
            return;
        }

        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        fireBaseHandler.loginUser(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult();
                if (user != null) {
                    Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, LayoutFragments.class);
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