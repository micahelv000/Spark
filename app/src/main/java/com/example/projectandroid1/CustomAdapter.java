package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private final ArrayList<com.example.projectandroid1.Item> dataSet;
    private final Context context;
//   private final DatabaseReference mDatabase;
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

        public View LikeButton;
        TextView Text_LocationName;
        TextView Text_TimeUploaded;
        TextView Text_NumberOfLikes;
        ImageView imageView;
        LinearLayout open;
        ImageView ReportButton;
        boolean LikeStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Text_LocationName = itemView.findViewById(R.id.TextLocation);
            Text_TimeUploaded = itemView.findViewById(R.id.textTimeUplode);
            imageView = itemView.findViewById(R.id.imageView);
            Text_NumberOfLikes = itemView.findViewById(R.id.NumberOfLikes);
            LikeButton = itemView.findViewById(R.id.button_Like);
            ReportButton = itemView.findViewById(R.id.button_Report);
            open = itemView.findViewById(R.id.open);

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
        final Item dataModel = dataSet.get(position);
        holder.Text_LocationName.setText(dataModel.getName());
        holder.Text_TimeUploaded.setText("Uploaded at: "+ dataModel.getEpoch());
        holder.Text_NumberOfLikes.setText(dataModel.getLikes() +" Likes");
        holder.imageView.setImageResource(dataModel.getImage());

        holder.ReportButton.setOnClickListener(v -> showConfirmationDialogReport(position, holder.ReportButton));
        holder.LikeButton.setOnClickListener(v -> B_Like(dataModel, holder));
        holder.open.setOnClickListener(v -> B_OpenParking(position));
        //holder.ReportButton.setOnClickListener(v -> removeAmount(dataModel));
    }

    private void showConfirmationDialogReport(int position, View ReportButton) {
        Animation rotate = AnimationUtils.loadAnimation(context, R.anim.hearbeat_anim);
        ReportButton.startAnimation(rotate);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to Report that the parking is taken?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            //update status
            notifyItemChanged(position);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void B_Like(Item dataModel, MyViewHolder holder) {
        Animation rotate = AnimationUtils.loadAnimation(context, R.anim.hearbeat_anim);
        holder.LikeButton.startAnimation(rotate);

        if(holder.LikeStatus){
            //remove -1
        }else{
            //add + 1
        }

        //dataModel.setAmount(dataModel.getAmount() + 1);
        //mDatabase.child("users").child(this.userid).child("amounts").child(dataModel.getName()).setValue(dataModel.getAmount());
        //notifyDataSetChanged();
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

    private void B_OpenParking(int position) {

        Intent intent = new Intent(context, Parking.class);
        context.startActivity(intent);
        //dataModel.setAmount(dataModel.getAmount() + 1);
        //mDatabase.child("users").child(this.userid).child("amounts").child(dataModel.getName()).setValue(dataModel.getAmount());
        //notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}


