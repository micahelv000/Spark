package com.example.projectandroid1;

import android.content.Context;
import android.location.Location;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FireBaseHandler {
    private final DatabaseReference mDatabase;
    private final FirebaseAuth mAuth;
    private final FirebaseStorage mStorage;

    public FireBaseHandler() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
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

    public Task<JSONObject> getPostData(String post_id) {
        return mDatabase.child("posts").child(post_id).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        JSONObject postJson = new JSONObject();
                        if (task.getResult().getValue() != null) {
                            Object result = task.getResult().getValue();
                            if (result instanceof Map<?, ?>) {
                                Map<?, ?> resultMap = (Map<?, ?>) result;
                                for (Map.Entry<?, ?> entry : resultMap.entrySet()) {
                                    postJson.put(entry.getKey().toString(), convertToJSONObject(entry.getValue()));
                                }
                            }
                        }
                        return Tasks.forResult(postJson);
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }

    public Task<JSONArray> getUserPosts(FirebaseUser user) {
        return mDatabase.child("users").child(user.getUid()).child("posts").get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        JSONArray posts = new JSONArray();
                        if (task.getResult().getValue() != null) {
                            Object result = task.getResult().getValue();
                            if (result instanceof Map<?, ?>) {
                                Map<?, ?> resultMap = (Map<?, ?>) result;
                                for (Map.Entry<?, ?> entry : resultMap.entrySet()) {
                                    posts.put(getPostData(entry.getKey().toString()).getResult());
                                }
                            }
                        }
                        return Tasks.forResult(posts);
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }

    public Task<JSONObject> getUserData(FirebaseUser user) {
        return mDatabase.child("users").child(user.getUid()).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        JSONObject userJson = new JSONObject();
                        if (task.getResult().getValue() != null) {
                            Object result = task.getResult().getValue();
                            if (result instanceof Map<?, ?>) {
                                Map<?, ?> resultMap = (Map<?, ?>) result;
                                for (Map.Entry<?, ?> entry : resultMap.entrySet()) {
                                    userJson.put(entry.getKey().toString(), convertToJSONObject(entry.getValue()));
                                }
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
    private Object convertToJSONObject(Object value) throws JSONException {
        if (value instanceof Map<?, ?>) {
            JSONObject json = new JSONObject();
            Map<?, ?> map = (Map<?, ?>) value;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                json.put(entry.getKey().toString(), convertToJSONObject(entry.getValue()));
            }
            return json;
        } else {
            return value;
        }
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
            JSONObject coordinates = new JSONObject();
            if (user_location != null) {
                coordinates.put("latitude", user_location.getLatitude());
                coordinates.put("longitude", user_location.getLongitude());
            } else {
                coordinates.put("latitude", 0);
                coordinates.put("longitude", 0);
            }
            location.put("coordinates", coordinates);
            location.put("city", city);
            location.put("country", country);
            json.put("location", location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject buildPostJson(String user_id, String image_url, String car_type, String[] parking_type,
            boolean is_free, Location location, String address, int total_likes, int reports) {
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
            locationJson.put("address", address);
            json.put("location", locationJson);
            json.put("total_likes", total_likes);
            json.put("reports", reports);
            json.put("epoch_time", System.currentTimeMillis() / 1000L);
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
        if (imageUri == null) {
            return Tasks.forResult(null);
        }
        String uid = user.getUid();
        StorageReference imageRef = mStorage.getReference().child("images").child(uid)
                .child(Objects.requireNonNull(imageUri.getLastPathSegment()));

        return imageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
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
                                post_location, address, 0, 0);
                        SavePostJsonData(user, postJson);
                    }
                    return Tasks.forResult(null);
                });

    }

    public Task<JSONArray> getAllPosts() {
        return mDatabase.child("posts").get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        JSONArray posts = new JSONArray();
                        if (task.getResult().getValue() != null) {
                            Object result = task.getResult().getValue();
                            if (result instanceof Map<?, ?>) {
                                Map<?, ?> resultMap = (Map<?, ?>) result;
                                for (Map.Entry<?, ?> entry : resultMap.entrySet()) {
                                    JSONObject postJson = new JSONObject();
                                    postJson.put("post_id", entry.getKey());
                                    postJson.put("user_id", ((Map<?, ?>) entry.getValue()).get("user_id"));
                                    postJson.put("image_url", ((Map<?, ?>) entry.getValue()).get("image_url"));
                                    postJson.put("car_type", ((Map<?, ?>) entry.getValue()).get("car_type"));
                                    postJson.put("parking_type", ((Map<?, ?>) entry.getValue()).get("parking_type"));
                                    postJson.put("is_free", ((Map<?, ?>) entry.getValue()).get("is_free"));
                                    
                                    Map<?, ?> locationMap = (Map<?, ?>) ((Map<?, ?>) entry.getValue()).get("location");
                                    assert locationMap != null;
                                    JSONObject locationJson = new JSONObject(locationMap);
                                    postJson.put("location", locationJson);
                                    
                                    postJson.put("total_likes", ((Map<?, ?>) entry.getValue()).get("total_likes"));
                                    postJson.put("reports", ((Map<?, ?>) entry.getValue()).get("reports"));
                                    postJson.put("epoch_time", ((Map<?, ?>) entry.getValue()).get("epoch_time"));
                                    posts.put(postJson);
                                }
                            }
                        }
                        return Tasks.forResult(posts);
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }
}