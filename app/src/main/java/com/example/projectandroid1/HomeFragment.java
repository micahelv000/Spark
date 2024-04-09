package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment implements Dialog_filter.FilterListener, DialogSortBy.SortByListener {
    private ArrayList<Post> dataSet;
    private ArrayList<Post> filteredDataSet;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView filterButton;
    ImageView sortButton;
    boolean isBigCarSelected, isRegularCarSelected, isSmallCarSelected, isParallelParkingSelected,
            isPerpendicularParkingSelected, isFreeParkingSelected, isPaidParkingSelected;
    int selectedDistanceType, selectedSortType;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        isBigCarSelected = true;
        isRegularCarSelected = true;
        isSmallCarSelected = true;
        isParallelParkingSelected = true;
        isPerpendicularParkingSelected = true;
        isFreeParkingSelected = true;
        isPaidParkingSelected = true;
        selectedDistanceType = 2;
        selectedSortType = 2;
        dataSet = new ArrayList<>();
        filteredDataSet = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.resView);
        EditText editTextSearch = rootView.findViewById(R.id.editText);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        filterButton = rootView.findViewById(R.id.B_filter);
        sortButton = rootView.findViewById(R.id.B_SortBy);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter == null)
                    return;
                filterDataSet(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        PostDataProcessor data = new PostDataProcessor();
        data.populateArrays(() -> {
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
            filteredDataSet.addAll(dataSet);
            filteredDataSet.sort((post1, post2) -> post1.getEpoch().compareToIgnoreCase(post2.getEpoch()));
            adapter = new CustomAdapter(filteredDataSet, getActivity());
            recyclerView.setAdapter(adapter);
        });

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::repopulateArrays);

        return rootView;
    }

    private void repopulateArrays() {
        PostDataProcessor data = new PostDataProcessor();
        data.populateArrays(() -> {
            dataSet.clear();
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
            filteredDataSet.clear();
            filteredDataSet.addAll(dataSet);
            filteredDataSet.sort((post1, post2) -> post1.getEpoch().compareToIgnoreCase(post2.getEpoch()));
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false); // Stop the refresh indicator
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filterButton.setOnClickListener(this::openFilter);
        sortButton.setOnClickListener(this::openSort);

    }

    public void openSort(View view) {
        DialogSortBy dialog = DialogSortBy.newInstance(selectedSortType);
        dialog.setFilterListener(this);
        dialog.show(getParentFragmentManager(), "Dialog_sort");
    }

    public void openFilter(View view) {
        Dialog_filter dialog = Dialog_filter.newInstance(isBigCarSelected, isRegularCarSelected, isSmallCarSelected,
                isParallelParkingSelected, isPerpendicularParkingSelected, isFreeParkingSelected,
                isPaidParkingSelected, selectedDistanceType);
        dialog.setFilterListener(this);
        dialog.show(getParentFragmentManager(), "Dialog_filter");
    }

    private void filterDataSet(String query) {
        filteredDataSet.clear();

        for (Post dataModel : dataSet) {
            if (dataModel.getAddress().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                filteredDataSet.add(dataModel);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFilterApplied(boolean isBigCar, boolean isRegularCar, boolean isSmallCar, boolean isParallelP,
            boolean isPerpendicularP, boolean isFreeP, boolean isPaidP, int typeDistance) {
        isBigCarSelected = isBigCar;
        isRegularCarSelected = isRegularCar;
        isSmallCarSelected = isSmallCar;
        isParallelParkingSelected = isParallelP;
        isPerpendicularParkingSelected = isPerpendicularP;
        isFreeParkingSelected = isFreeP;
        isPaidParkingSelected = isPaidP;
        selectedDistanceType = typeDistance;

        filteredDataSet.clear();
        for (Post dataModel : dataSet) {
            boolean shouldAdd1 = false;
            boolean shouldAdd2 = false;
            boolean shouldAdd3 = false;
            boolean shouldAdd4 = false;

            // Big small or Regular car
            if ((dataModel.getCarType().equals("Big Car") && isBigCar) ||
                    (dataModel.getCarType().equals("Regular Car") && isRegularCar) ||
                    (dataModel.getCarType().equals("Small Car") && isSmallCar)) {
                shouldAdd1 = true;
            }

            // horizontal or vertical

            String[] parkingType = dataModel.getParkingType();
            if (parkingType != null) {
                if ((parkingType.length > 0 && parkingType[0] != null && parkingType[0].equals("Parallel")
                        && isParallelP) ||
                        (parkingType.length > 1 && parkingType[1] != null && parkingType[1].equals("Perpendicular")
                                && isPerpendicularP)) {
                    shouldAdd2 = true;
                }
            }
            // free or paid
            if ((dataModel.getIsFree() && isFreeP) ||
                    (!dataModel.getIsFree() && isPaidP)) {
                shouldAdd3 = true;
            }

            LocationHelper locationHelper = new LocationHelper(requireContext());
            float distance = locationHelper.getDistanceToLocation(dataModel.getLocation());
            if (distance != -1) { // Check if distance retrieval was successful
                if (distance <= 10 && typeDistance == 1) {
                    shouldAdd4 = true;
                } else if (distance <= 5 && typeDistance == 0) {
                    shouldAdd4 = true;
                } else if (typeDistance == 2) {
                    shouldAdd4 = true;
                }
            }

            if (shouldAdd1 && shouldAdd2 && shouldAdd3 && shouldAdd4) {
                filteredDataSet.add(dataModel);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFilterApplied(int sort) {
        selectedSortType = sort;
        if (sort == 0) {
            // Sort alphabetically (ABC)
            filteredDataSet.sort((post1, post2) -> post1.getAddress().compareToIgnoreCase(post2.getAddress()));
        } else if (sort == 1) {
            // filteredDataSet.sort(Distance);
            filteredDataSet.sort((post1, post2) -> {
                LocationHelper locationHelper = new LocationHelper(requireContext());
                float distance1 = locationHelper.getDistanceToLocation(post1.getLocation());
                float distance2 = locationHelper.getDistanceToLocation(post2.getLocation());
                return Float.compare(distance1, distance2);
            });
        } else {
            filteredDataSet.sort((post1, post2) -> post1.getEpoch().compareToIgnoreCase(post2.getEpoch()));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        for (CustomAdapter adapter : CustomAdapter.adapters) {
            adapter.notifyDataSetChanged();
        }
    }
}