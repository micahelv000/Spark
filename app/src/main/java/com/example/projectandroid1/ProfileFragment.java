package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ArrayList<Post> dataSet;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private ImageView optionsMenuButton;
    String userDataString;
    TextView textPosts,likesTextView,fullNameTextView,textZone,textIG;
    ImageView profileImage;
    SwipeRefreshLayout swipeRefreshLayout;

    public ProfileFragment() {
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
        textPosts = rootView.findViewById(R.id.TextPosts);
        optionsMenuButton = rootView.findViewById(R.id.B_options);
        textIG = rootView.findViewById(R.id.TextIG);
        profileImage = rootView.findViewById(R.id.profileIMG);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userDataString = bundle.getString("userData");
            try {
                JSONObject userData = new JSONObject(Objects.requireNonNull(userDataString));
                fullNameTextView.setText(userData.getString("full_name"));
                textIG.setText("@" + userData.getString("instagram_handle"));

                int totalLikes = userData.getInt("total_likes");
                likesTextView.setText(String.valueOf(totalLikes));

                JSONObject location = userData.getJSONObject("location");
                textZone.setText(location.getString("country") + ", " + location.getString("city"));

                if (userData.has("posts")) {
                    int totalPosts = userData.getJSONObject("posts").length();
                    textPosts.setText(String.valueOf(totalPosts));
                } else {
                    textPosts.setText("0");
                }
                if (userData.has("profile_picture") && !userData.getString("profile_picture").isEmpty()) {
                    Picasso.get().load(userData.getString("profile_picture")).placeholder(R.drawable.progress_animation)
                            .error(R.drawable.default_profile).into(profileImage);
                }
            } catch (JSONException ignored) {
            }
        } else {
            Toast.makeText(getActivity(), "Bundle is null", Toast.LENGTH_SHORT).show();
        }

        dataSet = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.resView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        optionsMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), optionsMenuButton);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                Intent intent;
                // Handle menu item clicks

                CharSequence title = item.getTitle();

                if (Objects.equals(title, "Edit Profile")) {
                    intent = new Intent(getActivity(), EditProfileActivity.class);
                    intent.putExtra("user", userDataString);
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
                } else if (Objects.equals(title, "Email Password Reset")) {
                    Toast.makeText(getActivity(), "You've received a link in your mailbox", Toast.LENGTH_SHORT).show();
                    FireBaseHandler.sendPasswordResetEmail();
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
                        data.userIdArray[i],
                        data.postIdsArray[i],
                        data.likeStatusArray[i],
                        data.carTypeArray[i],
                        data.isFreeArray[i],
                        data.parkingTypeArray[i]));
            }
            dataSet.sort((post1, post2) -> post2.getEpoch().compareToIgnoreCase(post1.getEpoch()));

            adapter = new CustomAdapter(dataSet, getActivity());
            recyclerView.setAdapter(adapter);
        });

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::onResume);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        for (CustomAdapter adapter : CustomAdapter.adapters) {
            adapter.notifyDataSetChanged();

        }
        PostDataProcessor data = new PostDataProcessor();
        data.populateUserArrays(FireBaseHandler.getCurrentUser(), () -> {
            textPosts.setText(String.valueOf(data.addressArray.length));
            int sum =0;
            for (int i = 0; i < data.addressArray.length; i++) {
                sum += Integer.parseInt(data.likesArray[i]);
            }
            likesTextView.setText(String.valueOf(sum));
        });

        swipeRefreshLayout.setRefreshing(false); // Stop the refresh indicator
    }
}
