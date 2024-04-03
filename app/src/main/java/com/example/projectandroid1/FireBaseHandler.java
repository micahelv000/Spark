package com.example.projectandroid1;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class FireBaseHandler {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;

    public FireBaseHandler() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public static FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }

    public void logout() {
        mAuth.signOut();
    }

    public void SaveUserJsonData(FirebaseUser user, JSONObject json) {
        String uid = user.getUid();
        Map<String, Object> userMap = new Gson().fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {
        }.getType());
        mDatabase.child("users").child(uid).setValue(userMap);
    }

    public void SavePostJsonData(FirebaseUser user, JSONObject json) {
        String uid = user.getUid();
        String key = mDatabase.child("posts").push().getKey();
        Map<String, Object> postMap = new Gson().fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {
        }.getType());
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postMap);
        childUpdates.put("/users/" + uid + "/posts/" + key, true);
        mDatabase.updateChildren(childUpdates);
    }

    public Task<JSONObject> getUserData(FirebaseUser user) {
        return mDatabase.child("users").child(user.getUid()).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        JSONObject userJson = new JSONObject();
                        if (task.getResult().getValue() != null) {
                            Map<String, Object> userMap = (Map<String, Object>) task.getResult().getValue();
                            for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                                userJson.put(entry.getKey(), entry.getValue());
                            }
                        }
                        return Tasks.forResult(userJson);
                    } else {
                        return Tasks.forResult(null);
                    }
                });
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

    public JSONObject buildUserJson(String full_name, String bio, String instagram_handle, Location user_location,
            String city, String country) {
        JSONObject json = new JSONObject();
        try {
            json.put("full_name", full_name);
            json.put("profile_picture", "");
            json.put("bio", bio);
            json.put("instagram_handle", instagram_handle);
            json.put("posts", new JSONArray());
            json.put("total_likes", 0);
            JSONObject location = new JSONObject();
            JSONObject cordinates = new JSONObject();
            if (user_location != null) {
                cordinates.put("latitude", user_location.getLatitude());
                cordinates.put("longitude", user_location.getLongitude());
            } else {
                cordinates.put("latitude", 0);
                cordinates.put("longitude", 0);
            }
            location.put("cordinates", cordinates);
            location.put("city", city);
            location.put("country", country);
            json.put("location", location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject buildPostJson(String user_id, String image_url, String car_type, String[] parking_type,
            boolean is_free, Location location, int total_likes, int reports) {
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", user_id);
            json.put("image_url", image_url);
            json.put("car_type", car_type);
            json.put("parking_type", new JSONArray(parking_type));
            json.put("is_free", is_free);
            JSONObject locationJson = new JSONObject();
            JSONObject coordinates = new JSONObject();
            coordinates.put("latitude", location.getLatitude());
            coordinates.put("longitude", location.getLongitude());
            locationJson.put("coordinates", coordinates);
            locationJson.put("address", "");
            json.put("location", locationJson);
            json.put("total_likes", total_likes);
            json.put("reports", reports);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public Task<FirebaseUser> registerAndSaveUser(String email, String password, String full_name, String bio,
            String instagram_handle, Location user_location, String city, String country) {
        return registerUser(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult();
                        if (user != null) {
                            JSONObject userJson = buildUserJson(full_name, bio, instagram_handle, user_location, city,
                                    country);
                            SaveUserJsonData(user, userJson);
                        }
                        return Tasks.forResult(user);
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }

    public Task<FirebaseUser> registerAndSaveUser(String email, String password, String full_name,
            String instagram_handle, Location user_location, String city, String country) {
        return registerAndSaveUser(email, password, full_name, "", instagram_handle, user_location, city, country);
    }

    public Task<String> uploadImage(Uri imageUri, FirebaseUser user) {
        String uid = user.getUid();
        StorageReference imageRef = mStorage.getReference().child("images").child(uid)
                .child(Objects.requireNonNull(imageUri.getLastPathSegment()));

        return imageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return Tasks.forResult(task.getResult().toString());
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }

    public Task<Void> updateUserImage(Uri imageUri, FirebaseUser user) {
        return uploadImage(imageUri, user)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        String imageUrl = task.getResult();
                        return mDatabase.child("users").child(user.getUid()).child("profile_picture")
                                .setValue(imageUrl);
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }

    public Task<Void> uploadPost(Uri imageUri, String car_type, String[] parking_type, boolean is_free,
            Location post_location, FirebaseUser user, Context context) {
        String address = LocationHelper.getAddressFromLocation(post_location, context);
        return uploadImage(imageUri, user)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        String imageUrl = task.getResult();
                        JSONObject postJson = buildPostJson(user.getUid(), imageUrl, car_type, parking_type, is_free,
                                post_location, 0, 0);
                        SavePostJsonData(user, postJson);
                        return Tasks.forResult(null);
                    } else {
                        return Tasks.forResult(null);
                    }
                });

    }
}