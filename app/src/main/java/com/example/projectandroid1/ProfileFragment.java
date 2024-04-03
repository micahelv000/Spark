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


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private TextView fullNameTextView, likesTextView, latestPostTextView;
    private Button BeditProfile, BLogOut;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userDataString = bundle.getString("userData");
            // Parse user data JSON string and update UI accordingly
            try {
                JSONObject userData = new JSONObject(userDataString);
                // Update UI with user data
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        profileImage = rootView.findViewById(R.id.profileIMG);
        fullNameTextView = rootView.findViewById(R.id.textView2);
        //likesTextView = rootView.findViewById(R.id.textView3);
        latestPostTextView = rootView.findViewById(R.id.textView4);
        BeditProfile = rootView.findViewById(R.id.BEditProfile);
        BMenu = rootView.findViewById(R.id.B_options);

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
        for (int i = 0; i < myData.nameArray.length; i++) {
            dataSet.add(new Item(
                    myData.nameArray[i],
                    Integer.parseInt(myData.amount[i]),
                    Double.parseDouble(myData.price[i]),
                    myData.drawableArray[i]
            ));
        }
        adapter = new CustomAdapter(dataSet, getActivity());
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
