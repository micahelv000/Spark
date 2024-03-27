package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private final ArrayList<com.example.projectandroid1.Item> dataSet;
    private final Context context;
//    private final DatabaseReference mDatabase;
//   private final String userid;

    public CustomAdapter(ArrayList<com.example.projectandroid1.Item> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
/*      this.mDatabase = FirebaseDatabase.getInstance().getReference();
        userid = ((Activity) context).getIntent().getStringExtra("userId");

        mDatabase.child("users").child(userid).child("amounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Loop through all children of the dataSnapshot
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Get the name and amount of each item
                    String itemName = childSnapshot.getKey();
                    int itemAmount = childSnapshot.getValue(Integer.class);

                    // Find the item in the dataSet with the same name
                    for (com.example.projectandroid1.Item item : dataSet) {
                        if (item.getName().equals(itemName)) {
                            // Update the amount of the item
                            item.setAmount(itemAmount);
                        }
                    }
                }

                // Notify the adapter that the data set has changed
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
        */
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textAmount;
        TextView textPrice;
        ImageView imageView;
        Button addButton;
        Button removeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textView);
            textAmount = itemView.findViewById(R.id.textTimeUplode);
            imageView = itemView.findViewById(R.id.imageView);
            textPrice = itemView.findViewById(R.id.NumberOfLikes);
            addButton = itemView.findViewById(R.id.button_Add);
            removeButton = itemView.findViewById(R.id.button_Remove);
        }
    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardrow, parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        final com.example.projectandroid1.Item dataModel = dataSet.get(position);
        holder.textViewName.setText(dataModel.getName());
        holder.textAmount.setText("amount have: "+ dataModel.getAmount());
        holder.textPrice.setText(dataModel.getPrice() +" ₪");
        holder.imageView.setImageResource(dataModel.getImage());

        //holder.addButton.setOnClickListener(v -> addAmount(dataModel));
        //holder.removeButton.setOnClickListener(v -> removeAmount(dataModel));
    }
/*
    private void removeAmount(com.example.projectandroid1.Item dataModel) {
        if (dataModel.getAmount() > 0) {
            dataModel.setAmount(dataModel.getAmount() - 1);
            mDatabase.child("users").child(this.userid).child("amounts").child(dataModel.getName()).setValue(dataModel.getAmount());
            notifyDataSetChanged();
        }
    }

    private void addAmount(com.example.projectandroid1.Item dataModel) {
        dataModel.setAmount(dataModel.getAmount() + 1);
        mDatabase.child("users").child(this.userid).child("amounts").child(dataModel.getName()).setValue(dataModel.getAmount());
        notifyDataSetChanged();
    }
*/
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}


