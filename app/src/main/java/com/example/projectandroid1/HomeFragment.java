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



import java.util.ArrayList;
//public class HomeFragment extends Fragment implements Dialog_filter.FilterListener {
public class HomeFragment extends Fragment {
    private ArrayList<Post> dataSet;
    private ArrayList<Post> filteredDataSet;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    ImageView Bfilter,Bhome;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);



        dataSet = new ArrayList<>();
        filteredDataSet = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.resView);
        EditText editTextSearch = rootView.findViewById(R.id.editText);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        Bfilter = rootView.findViewById(R.id.B_filter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter == null) return;
                filterDataSet(s.toString().trim()); // Trim the query
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Perform any necessary actions after text has changed
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
                        data.likeStatusArray[i]
                ));
            }
            filteredDataSet.addAll(dataSet);

            adapter = new CustomAdapter(filteredDataSet, getActivity());
            recyclerView.setAdapter(adapter);
        });
    
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bfilter.setOnClickListener(this::Openfilter);
    }
    public void Openfilter(View view) {
        Dialog_filter dialog = new Dialog_filter();
        //dialog.setFilterListener(this); // Set the listener
        dialog.show(getChildFragmentManager(), "Dialog_filter");

    }
    private void filterDataSet(String query) {
        filteredDataSet.clear();

        for (Post dataModel : dataSet) {
            if (dataModel.getAddress().toLowerCase().contains(query.toLowerCase())) {
                filteredDataSet.add(dataModel);
            }
        }

        adapter.notifyDataSetChanged();
    }


    public void scrollToTop() {
        if (recyclerView != null && adapter != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    /*
    @Override
    public void onFilterApplied(boolean isBigCar, boolean isRegularCar, boolean isSmallCar,
                                boolean isParallelP, boolean isPerpendicularP, boolean isFreeP,
                                boolean isPaidP) {

        for (Post dataModel : dataSet) {
            if (dataModel.getTypeCar().equalsto("BigCar") && isBigCar ) {
                filteredDataSet.add(dataModel);
                return;
            }
            if (dataModel.getTypeCar().equalsto("RegularCar") && isRegularCar ) {
                filteredDataSet.add(dataModel);
                return;
            }
            if (dataModel.getTypeCar().equalsto("SmallCar") && isSmallCar ) {
                filteredDataSet.add(dataModel);
                return;
            }
            if (dataModel.getParking().equalsto("X") && isParallelP ) {
                filteredDataSet.add(dataModel);
                return;
            }
            if (dataModel.getParking().equalsto("Y") && isPerpendicularP ) {
                filteredDataSet.add(dataModel);
                return;
            }
            if (dataModel.getPrice().equalsto("Free") && isFreeP ) {
                filteredDataSet.add(dataModel);
                return;
            }
            if (dataModel.getPrice().equalsto("Free") && isPaidP ) {
                filteredDataSet.add(dataModel);
                return;
            }


        }
        adapter.notifyDataSetChanged();

    }

     */
}
