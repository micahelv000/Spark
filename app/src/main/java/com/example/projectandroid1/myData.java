package com.example.projectandroid1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class myData {
    static String[] addressArray = {"Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape", "Honeydew", "Iced Tea", "Juice"};
    static String[] epochsArray = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
    static String[] likesArray = {"0.99", "0.69", "3.50", "1.99", "2.50", "1.50", "2.99", "3.99", "1.50", "2.00"};

    static Integer[] drawableArray = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};
    
    public interface OnArraysPopulatedListener {
        void onArraysPopulated();
    }

    public static void populateArrays(OnArraysPopulatedListener listener) {
        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        fireBaseHandler.getAllPosts().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                JSONArray posts = task.getResult();
                if (posts != null) {
                    addressArray = new String[posts.length()];
                    epochsArray = new String[posts.length()];
                    likesArray = new String[posts.length()];
                    drawableArray = new Integer[posts.length()];

                    for (int i = 0; i < posts.length(); i++) {
                        try {
                            JSONObject post = posts.getJSONObject(i);
                            addressArray[i] = post.has("location") ? post.getString("location") : null;
                            epochsArray[i] = post.has("epoch_time") ? post.getString("epoch_time") : null;
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
            }
        });
    }
}
