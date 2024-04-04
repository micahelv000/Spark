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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private ArrayList<Item> dataSet;
    private ArrayList<Item> filteredDataSet;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CustomAdapter adapter;
    private EditText editTextSearch;
    ImageView Bfilter,Bhome;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);



        dataSet = new ArrayList<>();
        filteredDataSet = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.resView);
        editTextSearch = rootView.findViewById(R.id.editText);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        Bfilter = rootView.findViewById(R.id.B_filter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDataSet(s.toString().trim()); // Trim the query
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Perform any necessary actions after text has changed
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Add items to the dataSet
        myData.populateArrays(() -> {
            for (int i = 0; i < myData.addressArray.length; i++) {
                dataSet.add(new Item(
                        myData.addressArray[i],
                        myData.epochsArray[i],
                        myData.likesArray[i],
                        myData.drawableArray[i]
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
        Bfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Openfilter(v);
            }
        });
    }
    public void Openfilter(View view) {
        Dialog_filter dialog = new Dialog_filter();
        dialog.show(getChildFragmentManager(), "Dialog_filter");
    }
    private void filterDataSet(String query) {
        filteredDataSet.clear();

        for (Item dataModel : dataSet) {
            if (dataModel.getName().toLowerCase().contains(query.toLowerCase())) {
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



}
