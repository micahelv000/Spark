package com.example.projectandroid1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
        TextView TextMSGHello = findViewById(R.id.userDetailMSG);
        TextMSGHello.setText("Hi, " + username + "    Welcome to home page");

        dataSet = new ArrayList<>();
        filteredDataSet = new ArrayList<>();
        recyclerView = findViewById(R.id.resView);
        editTextSearch = findViewById(R.id.editText); // Correctly reference the EditText
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDataSet(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        for (int i = 0; i < myData.nameArray.length; i++) {
            dataSet.add(new Item(
                    myData.nameArray[i],
                    Integer.parseInt(myData.amount[i]), // add later pull from the firebase of actual amount.
                    Double.parseDouble(myData.price[i]),
                    myData.drawableArray[i]
            ));
        }
        filteredDataSet.addAll(dataSet);

        adapter = new CustomAdapter(filteredDataSet, this); // Pass context here
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
}