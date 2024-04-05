package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private ArrayList<Item> dataSet;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ImageView profileImage, BMenu;
    private TextView fullNameTextView, likesTextView, latestPostTextView, textZone, TextPosts, TextIG;
    private Button BeditProfile, BLogOut;
    private JSONObject userData, location;
    private JSONArray postsArray;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported.
                String result = bundle.getString("bundleKey");
                // Do something with the result.
            }
        });
    }

    @SuppressLint({ "WrongViewCast", "MissingInflatedId", "SetTextI18n", "ShowToast" })
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        fullNameTextView = rootView.findViewById(R.id.textFname);
        textZone = rootView.findViewById(R.id.textZone);
        likesTextView = rootView.findViewById(R.id.textLikes);
        TextPosts = rootView.findViewById(R.id.TextPosts);
        BMenu = rootView.findViewById(R.id.B_options);
        TextIG = rootView.findViewById(R.id.TextIG);
        profileImage = rootView.findViewById(R.id.profileIMG);
        // Initialize views

        Bundle bundle = getArguments();
        if (bundle != null) {
            String userDataString = bundle.getString("userData");
            try {
                userData = new JSONObject(userDataString); // {"instagram_handle":"asdca","full_name":"askk","bio":"","location":"{country=Israel,
                                                           // cordinates={latitude=32.015833,
                                                           // longitude=34.787383999999996},
                                                           // city=Holon}","profile_picture":"https:\/\/firebasestorage.googleapis.com\/v0\/b\/projectandroid1-3dfb0.appspot.com\/o\/images%2Fl47J2K5mNPcO0zJnE03l4pfKz4i1%2F2117381231?alt=media&token=8b788b4d-6031-4518-8079-5f9f6d7dfe63","total_likes":0,"posts":"{-NudWJf8Al75ERzggDEk=true}"}
                // Update UI with user data
                fullNameTextView.setText(userData.getString("full_name"));
                TextIG.setText("@" + userData.getString("instagram_handle"));

                int totalLikes = userData.getInt("total_likes");
                likesTextView.setText(String.valueOf(totalLikes));

                location = userData.getJSONObject("location");
                textZone.setText(location.getString("country") + ", " + location.getString("city"));

                postsArray = userData.getJSONArray("posts");
                int totalPosts = postsArray.length();
                TextPosts.setText(String.valueOf(totalPosts));

                // Uri uri = Uri.parse(userData.getString("profile_picture"));
                // profileImage.setImageURI(uri);
                Toast.makeText(getActivity(), "all good", Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where bundle is null (e.g., display an error message)
            Toast.makeText(getActivity(), "Bundle is null", Toast.LENGTH_SHORT).show();
        }

        dataSet = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.resView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        BMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating a PopupMenu instance
                PopupMenu popupMenu = new PopupMenu(getContext(), BMenu);

                // Inflating the menu layout
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                // Adding click listener for menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        // Handle menu item clicks

                        CharSequence title = item.getTitle();

                        if (title.equals("Edit Profile")) {// EditProfile
                            intent = new Intent(getActivity(), EditProfileActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (title.equals("Sign Out")) {// sign out
                            FirebaseAuth.getInstance().signOut();
                            // Redirect to login screen
                            intent = new Intent(getActivity(), Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            getActivity().finish();
                            return true;
                        }
                        return false;
                    }
                });

                // Showing the PopupMenu
                popupMenu.show();
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        myData data = new myData();
        data.populateUserArrays(FireBaseHandler.getCurrentUser(), () -> {
            for (int i = 0; i < data.addressArray.length; i++) {
                dataSet.add(new Item(
                        data.addressArray[i],
                        data.epochsArray[i],
                        data.likesArray[i],
                        data.drawableArray[i]));
            }

            adapter = new CustomAdapter(dataSet, getActivity());
            recyclerView.setAdapter(adapter);
        });

        return rootView;
    }
}
