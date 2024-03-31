package com.example.projectandroid1;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FireBaseHandler {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public FireBaseHandler() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void SaveUserJsonData(FirebaseUser user, JSONObject json) {
        String uid = user.getUid();
        Map<String, Object> userMap = new Gson().fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {
        }.getType());
        mDatabase.child("users").child(uid).setValue(userMap);
    }

    public Task<FirebaseUser> registerUser(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return Tasks.forResult(mAuth.getCurrentUser());
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }

    public Task<FirebaseUser> loginUser(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return Tasks.forResult(mAuth.getCurrentUser());
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }

    public JSONObject buildUserJson(String full_name, String profile_picture, String bio, String instagram_handle) {
        JSONObject json = new JSONObject();
        try {
            json.put("full_name", full_name);
            json.put("profile_picture", profile_picture);
            json.put("bio", bio);
            json.put("instagram_handle", instagram_handle);
            json.put("posts", new JSONArray());
            json.put("total_likes", 0);
            JSONObject location = new JSONObject();
            JSONObject cordinates = new JSONObject();
            cordinates.put("latitude", 0.0);
            cordinates.put("longitude", 0.0);
            location.put("cordinates", cordinates);
            location.put("city", "");
            location.put("country", "");
            json.put("location", location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public Task<FirebaseUser> registerAndSaveUser(String email, String password, String full_name,
            String profile_picture, String bio, String instagram_handle) {
        return registerUser(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult();
                        if (user != null) {
                            JSONObject userJson = buildUserJson(full_name, profile_picture, bio, instagram_handle);
                            SaveUserJsonData(user, userJson);
                        }
                        return Tasks.forResult(user);
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }

    public Task<FirebaseUser> registerAndSaveUser(String email, String password, String full_name,
            String instagram_handle) {
        return registerAndSaveUser(email, password, full_name, "", "", instagram_handle);
    }
}