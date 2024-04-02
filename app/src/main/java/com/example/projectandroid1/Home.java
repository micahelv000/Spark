package com.example.projectandroid1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    private ArrayList<Item> dataSet;
    private ArrayList<Item> filteredDataSet;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CustomAdapter adapter;
    private EditText editTextSearch;
    private ImageView Bhome;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Change the color of the current page button
        Bhome = findViewById(R.id.B_Open_search);
        int color = Color.parseColor("#0064D1");
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        Bhome.setImageTintList(colorStateList);

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

    public void B_AddParking(View view) {
        Intent intent = new Intent(this, AddParking.class);
        startActivity(intent);
    }
    public void B_Logout(View view) {
        FireBaseHandler firebaseHandler = new FireBaseHandler();
        firebaseHandler.logout();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void B_Profile(View view) {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }
    public void B_Home(View view) {

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void Openfilter(View view) {
        Dialog_filter dialog = new Dialog_filter();
        dialog.show(getSupportFragmentManager(), "Dialog_filter");
    }
    public void OpenMap(View view) {
        Intent intent = new Intent(this, Loading_Map.class);
        startActivity(intent);
    }
}
