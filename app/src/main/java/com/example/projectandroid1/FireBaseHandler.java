package com.example.projectandroid1;

import android.content.Context;
import android.location.Location;
import android.net.Uri;

import androidx.annotation.NonNull;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    
    public static Task<String> getFullName(String user_id) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase.child("users").child(user_id).child("full_name").get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return Tasks.forResult(Objects.requireNonNull(task.getResult().getValue()).toString());
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }

    public static Task<Void> sendPasswordResetEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = getCurrentUser();
        return auth.sendPasswordResetEmail(Objects.requireNonNull(currentUser.getEmail()));
    }

    public static Task<String> getUserName(String user_id) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase.child("users").child(user_id).child("instagram_handle").get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return Tasks.forResult(Objects.requireNonNull(task.getResult().getValue()).toString());
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }
    public static Task<String> getProfilePic(String user_id) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase.child("users").child(user_id).child("profile_picture").get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return Tasks.forResult(Objects.requireNonNull(task.getResult().getValue()).toString());
                    } else {
                        return Tasks.forResult(null);
                    }
                });
    }

    public void likePost(String post_id) {
        handlePost(post_id, true);
    }

    public void unlikePost(String post_id) {
        handlePost(post_id, false);
    }

    private void handlePost(String post_id, boolean isLike) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            Tasks.forResult(null);
            return;
        }
        String uid = user.getUid();
        mDatabase.child("users").child(uid).child("likes").child(post_id).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        if ((isLike && task.getResult().getValue() == null) || (!isLike && task.getResult().getValue() != null)) {
                            handleUserLike(uid, post_id, isLike);
                            handlePostLike(uid, post_id, isLike);
                        }
                    }
                    return Tasks.forResult(null);
                });
        Tasks.forResult(null);
    }

    private void handleUserLike(String uid, String post_id, boolean isLike) {
        if (isLike) {
            mDatabase.child("users").child(uid).child("likes").child(post_id).setValue(true);
        } else {
            mDatabase.child("users").child(uid).child("likes").child(post_id).removeValue();
        }
    }

    private void handlePostLike(String uid, String post_id, boolean isLike) {
        mDatabase.child("posts").child(post_id).child("total_likes").get()
                .continueWithTask(task1 -> {
                    if (task1.isSuccessful()) {
                        int total_likes = Integer.parseInt(Objects.requireNonNull(task1.getResult().getValue()).toString());
                        mDatabase.child("posts").child(post_id).child("total_likes").setValue(isLike ? total_likes + 1 : total_likes - 1);
                        if (isLike) {
                            mDatabase.child("posts").child(post_id).child("likes").child(uid).setValue(true);
                        } else {
                            mDatabase.child("posts").child(post_id).child("likes").child(uid).removeValue();
                        }
                        handleUserTotalLikes(uid, post_id, isLike);
                    }
                    return Tasks.forResult(null);
                });
    }

    private void handleUserTotalLikes(String uid, String post_id, boolean isLike) {
        mDatabase.child("posts").child(post_id).child("user_id").get()
                .continueWithTask(task2 -> {
                    if (task2.isSuccessful()) {
                        String user_id = Objects.requireNonNull(task2.getResult().getValue()).toString();
                        mDatabase.child("users").child(user_id).child("total_likes").get()
                                .continueWithTask(task3 -> {
                                    if (task3.isSuccessful()) {
                                        int user_total_likes = Integer.parseInt(Objects.requireNonNull(task3.getResult().getValue()).toString());
                                        mDatabase.child("users").child(user_id).child("total_likes").setValue(isLike ? user_total_likes + 1 : user_total_likes - 1);
                                    }
                                    return Tasks.forResult(null);
                                });
                    }
                    return Tasks.forResult(null);
                });
    }
    
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
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
                                    postJson.put("post_id", entry.getKey());
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
                                List<Task<JSONObject>> tasks = new ArrayList<>();
                                for (Map.Entry<?, ?> entry : resultMap.entrySet()) {
                                    tasks.add(getPostData(entry.getKey().toString()));
                                }
                                return Tasks.whenAllSuccess(tasks).continueWith(task1 -> {
                                    for (Object object : task1.getResult()) {
                                        posts.put(object);
                                    }
                                    return posts;
                                });
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
            JSONObject location = getJsonObject(user_location, city, country);
            json.put("location", location);
        } catch (Exception ignored) {
        }
        return json;
    }

    @NonNull
    private static JSONObject getJsonObject(Location user_location, String city, String country) throws JSONException {
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
        return location;
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
        } catch (Exception ignored) {
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
                                    Map<?, ?> likesMap = (Map<?, ?>) ((Map<?, ?>) entry.getValue()).get("likes");
                                    if (likesMap != null) {
                                        JSONArray likesArray = new JSONArray();
                                        for (Map.Entry<?, ?> like : likesMap.entrySet()) {
                                            likesArray.put(like.getKey());
                                        }
                                        postJson.put("likes", likesArray);
                                    }
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