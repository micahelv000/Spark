package com.example.projectandroid1;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class myData {
    String[] addressArray = {"Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape", "Honeydew", "Iced Tea", "Juice"};
    String[] epochsArray = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
    String[] likesArray = {"0.99", "0.69", "3.50", "1.99", "2.50", "1.50", "2.99", "3.99", "1.50", "2.00"};

    Integer[] drawableArray = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};
    
    public interface OnArraysPopulatedListener {
        void onArraysPopulated();
    }

    private void populateArraysFromPosts(JSONArray posts, OnArraysPopulatedListener listener) {
        addressArray = new String[posts.length()];
        epochsArray = new String[posts.length()];
        likesArray = new String[posts.length()];
        drawableArray = new Integer[posts.length()];

        for (int i = 0; i < posts.length(); i++) {
            try {
                JSONObject post = posts.getJSONObject(i);
                if (post.has("location")) {
                    JSONObject location = post.getJSONObject("location");
                    addressArray[i] = location.has("address") ? location.getString("address") : null;
                } else {
                    addressArray[i] = null;
                }

                if (post.has("epoch_time")) {
                    long epochTime = Long.parseLong(post.getString("epoch_time"));
                    String date = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT, Locale.getDefault()).format(new java.util.Date (epochTime*1000));
                    epochsArray[i] = date;
                } else {
                    epochsArray[i] = null;
                }

                likesArray[i] = post.has("total_likes") ? String.valueOf(post.getLong("total_likes")) : null;
                drawableArray[i] = R.drawable.ic_launcher_background;
            } catch (JSONException e) {
                e.printStackTrace();
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
}
