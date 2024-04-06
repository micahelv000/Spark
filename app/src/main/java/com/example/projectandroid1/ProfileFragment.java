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
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ArrayList<Post> dataSet;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ImageView profileImage, BMenu;
    private TextView fullNameTextView, likesTextView, latestPostTextView, textZone, TextPosts, TextIG;
    private Button BeditProfile;
    private JSONObject userData, location;

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
                userData = new JSONObject(Objects.requireNonNull(userDataString));
                fullNameTextView.setText(userData.getString("full_name"));
                TextIG.setText("@" + userData.getString("instagram_handle"));

                int totalLikes = userData.getInt("total_likes");
                likesTextView.setText(String.valueOf(totalLikes));

                location = userData.getJSONObject("location");
                textZone.setText(location.getString("country") + ", " + location.getString("city"));

                if (userData.has("posts")) {
                    int totalPosts = userData.getJSONObject("posts").length();
                    TextPosts.setText(String.valueOf(totalPosts));
                } else {
                    TextPosts.setText("0");
                }

                Picasso.get().load(userData.getString("profile_picture")).placeholder(R.drawable.progress_animation).into(profileImage);

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

        BMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), BMenu);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                Intent intent;
                // Handle menu item clicks

                CharSequence title = item.getTitle();

                if (Objects.equals(title, "Edit Profile")) {
                    intent = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (Objects.equals(title, "Sign Out")) {
                    FireBaseHandler.logout();
                    // Redirect to login screen
                    intent = new Intent(getActivity(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;
                }
                return false;
            });

            // Showing the PopupMenu
            popupMenu.show();
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        PostDataProcessor data = new PostDataProcessor();
        data.populateUserArrays(FireBaseHandler.getCurrentUser(), () -> {
            for (int i = 0; i < data.addressArray.length; i++) {
                dataSet.add(new Post(
                        data.addressArray[i],
                        data.epochsArray[i],
                        data.likesArray[i],
                        data.postPicturesArray[i],
                        data.locationArray[i],
                        data.userIdArray[i]));
            }

            adapter = new CustomAdapter(dataSet, getActivity());
            recyclerView.setAdapter(adapter);
        });

        return rootView;
    }
}
