package com.example.projectandroid1;

import android.location.Location;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class PostDataProcessor {
    String[] addressArray;
    String[] epochsArray;
    String[] likesArray;
    String[] postPicturesArray;
    Location[] locationArray;
    String[] userIdArray;
    String[] postIdsArray;
    boolean[] likeStatusArray;
    String[] carTypeArray;
    String[][] parkingTypeArray;
    boolean[] isFreeArray;

    public boolean[] getLikeStatusArray() {
        return this.likeStatusArray;
    }

    public interface OnArraysPopulatedListener {
        void onArraysPopulated();
    }

    private void populateArraysFromPosts(JSONArray posts, OnArraysPopulatedListener listener) {
        addressArray = new String[posts.length()];
        epochsArray = new String[posts.length()];
        likesArray = new String[posts.length()];
        postPicturesArray = new String[posts.length()];
        locationArray = new Location[posts.length()];
        userIdArray = new String[posts.length()];
        postIdsArray = new String[posts.length()];
        likeStatusArray = new boolean[posts.length()];
        carTypeArray = new String[posts.length()];
        isFreeArray = new boolean[posts.length()];
        parkingTypeArray = new String[posts.length()][];

        for (int i = 0; i < posts.length(); i++) {
            try {
                JSONObject post = posts.getJSONObject(i);

                post.getString("post_id");
                postIdsArray[i] = post.getString("post_id");
                if (post.has("user_id")) {
                    userIdArray[i] = post.getString("user_id");
                } else {
                    userIdArray[i] = null;
                }

                if (post.has("location")) {
                    JSONObject location = post.getJSONObject("location");
                    addressArray[i] = location.has("address") ? location.getString("address") : null;
                    if (location.has("coordinates")) {
                        JSONObject coordinates = location.getJSONObject("coordinates");
                        double latitude = coordinates.has("latitude") ? coordinates.getDouble("latitude") : 0;
                        double longitude = coordinates.has("longitude") ? coordinates.getDouble("longitude") : 0;
                        locationArray[i] = new Location("");
                        locationArray[i].setLatitude(latitude);
                        locationArray[i].setLongitude(longitude);
                    } else {
                        locationArray[i] = null;
                    }
                } else {
                    addressArray[i] = null;
                }

                if (post.has("epoch_time")) {
                    long epochTime = Long.parseLong(post.getString("epoch_time"));
                    String date = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM,
                            java.text.DateFormat.SHORT, Locale.getDefault())
                            .format(new java.util.Date(epochTime * 1000));
                    epochsArray[i] = date;
                } else {
                    epochsArray[i] = null;
                }

                likesArray[i] = post.has("total_likes") ? String.valueOf(post.getLong("total_likes")) : null;

                if (post.has("image_url")) {
                    postPicturesArray[i] = post.getString("image_url");
                } else {
                    postPicturesArray[i] = null;
                }

                if (post.has("likes")) {
                    JSONArray likes = post.getJSONArray("likes");
                    for (int j = 0; j < likes.length(); j++) {
                        if (likes.getString(j).equals(FireBaseHandler.getCurrentUser().getUid())) {
                            likeStatusArray[i] = true;
                            break;
                        } else {
                            likeStatusArray[i] = false;
                        }
                    }
                } else {
                    likeStatusArray[i] = false;
                }

                carTypeArray[i] = post.has("car_type") ? post.getString("car_type") : null;
                isFreeArray[i] = post.has("is_free") && post.getBoolean("is_free");

                if (post.has("parking_type")) {
                    JSONArray parkingTypes = post.getJSONArray("parking_type");
                    parkingTypeArray[i] = new String[parkingTypes.length()];
                    for (int j = 0; j < parkingTypes.length(); j++) {
                        if (parkingTypes.isNull(j)) {
                            parkingTypeArray[i][j] = null;
                        } else {
                            parkingTypeArray[i][j] = parkingTypes.getString(j);
                        }
                    }
                } else {
                    parkingTypeArray[i] = null;
                }

            } catch (JSONException ignored) {
            }
        }

        if (listener != null) {
            listener.onArraysPopulated();
        }
    }

    public void populateArrays(OnArraysPopulatedListener listener) {
        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        fireBaseHandler.getAllPosts().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                JSONArray posts = task.getResult();
                if (posts != null) {
                    populateArraysFromPosts(posts, listener);
                }
            }
        });
    }

    public void populateUserArrays(FirebaseUser user, OnArraysPopulatedListener listener) {
        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        fireBaseHandler.getUserPosts(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                JSONArray posts = task.getResult();
                if (posts != null) {
                    populateArraysFromPosts(posts, listener);
                }
            }
        });
    }

    public String[] getAddressArray() {
        return addressArray;
    }

    public String[] getEpochsArray() {
        return epochsArray;
    }

    public String[] getLikesArray() {
        return likesArray;
    }

    public String[] getPostPicturesArray() {
        return postPicturesArray;
    }

    public Location[] getLocationArray() {
        return locationArray;
    }

    public String[] getUserIdArray() {
        return userIdArray;
    }

    public String[] getPostIdsArray() {
        return postIdsArray;
    }

    public String[] getCarTypeArray() {
        return carTypeArray;
    }

    public boolean[] getIsFreeArray() {
        return isFreeArray;
    }

    public String[][] getParkingTypeArray() {
        return parkingTypeArray;
    }
}
