package com.example.projectandroid1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    private ArrayList<Item> dataSet;
    private ArrayList<Item> filteredDataSet;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CustomAdapter adapter;
    private EditText editTextSearch;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String username = getIntent().getStringExtra("username");

        dataSet = new ArrayList<>();
        filteredDataSet = new ArrayList<>();
        recyclerView = findViewById(R.id.resView);
        editTextSearch = findViewById(R.id.editText);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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
        for (int i = 0; i < myData.nameArray.length; i++) {
            dataSet.add(new Item(
                    myData.nameArray[i],
                    Integer.parseInt(myData.amount[i]),
                    Double.parseDouble(myData.price[i]),
                    myData.drawableArray[i]
            ));
        }
        filteredDataSet.addAll(dataSet);

        adapter = new CustomAdapter(filteredDataSet, this);
        recyclerView.setAdapter(adapter);
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

    public void AddParking(View view) {
        Intent intent = new Intent(this, AddParking.class);
        startActivity(intent);
    }
    public void Profile(View view) {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }
    public void Parking(View view) {
        Intent intent = new Intent(this, Parking.class);
        startActivity(intent);
    }
}
