package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private ArrayList<Item> dataSet;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ImageView profileImage;
    private TextView fullNameTextView, likesTextView, latestPostTextView;
    private Button BeditProfile, BLogOut;

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profileImage = rootView.findViewById(R.id.profileIMG);
        fullNameTextView = rootView.findViewById(R.id.textView2);
        likesTextView = rootView.findViewById(R.id.textView3);
        latestPostTextView = rootView.findViewById(R.id.textView4);
        BeditProfile = rootView.findViewById(R.id.BEditProfile);
        BLogOut =rootView.findViewById(R.id.BLogOut);


        dataSet = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.resView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        BeditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the edit profile action here
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        BLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                // Redirect to login screen
                Intent intent = new Intent(getActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
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
