package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private ArrayList<Item> dataSet;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ImageView profileImage, BMenu;
    private TextView fullNameTextView, likesTextView, latestPostTextView,textZone,TextPosts;
    private Button BeditProfile, BLogOut;
    private JSONObject userData;
    @SuppressLint({"WrongViewCast", "MissingInflatedId", "SetTextI18n", "ShowToast"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        fullNameTextView = rootView.findViewById(R.id.textFname);
        textZone = rootView.findViewById(R.id.textZone);
        likesTextView = rootView.findViewById(R.id.textLikes);
        TextPosts = rootView.findViewById(R.id.TextPosts);
        BMenu = rootView.findViewById(R.id.B_options);
        // Initialize views
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userDataString = bundle.getString("userData");
            try {
                userData = new JSONObject(userDataString);
                // Update UI with user data
                fullNameTextView.setText(userData.getString("full_name"));
                textZone.setText(userData.getString("country") + ", " + userData.getString("city"));
                likesTextView.setText(userData.getString("total_likes"));
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

        // Add items to the dataSet (Replace with your own data)
        for (int i = 0; i < myData.addressArray.length; i++) {
            dataSet.add(new Item(
                    myData.addressArray[i],
                    myData.epochsArray[i],
                    myData.likesArray[i],
                    myData.drawableArray[i]
            ));
        }
        adapter = new CustomAdapter(dataSet, getActivity());
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
