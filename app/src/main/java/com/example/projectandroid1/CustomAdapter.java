package com.example.projectandroid1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>{
    private final ArrayList<Post> dataSet;
    private final Context context;
    public static List<CustomAdapter> adapters = new ArrayList<>();

    public CustomAdapter(ArrayList<Post> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
        adapters.add(this);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView LikeButton;
        TextView Text_LocationName;
        TextView Text_TimeUploaded;
        TextView Text_NumberOfLikes,TextDistanceFromUser, TextUsernameUploaded,Text_Probability;
        ImageView imageView;
        LinearLayout open;
        boolean LikeStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Text_LocationName = itemView.findViewById(R.id.TextLocation);
            Text_TimeUploaded = itemView.findViewById(R.id.TextTimeUploaded);
            TextDistanceFromUser = itemView.findViewById(R.id.TextDistanceFromUser);
            TextUsernameUploaded = itemView.findViewById(R.id.TextPostFullName);
            Text_Probability = itemView.findViewById(R.id.Text_Probability);
            imageView = itemView.findViewById(R.id.imageView);
            Text_NumberOfLikes = itemView.findViewById(R.id.NumberOfLikes);
            LikeButton = itemView.findViewById(R.id.button_Like);
            open = itemView.findViewById(R.id.open);

        }
    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardrow, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        Post dataModel = dataSet.get(position);
        holder.Text_LocationName.setText(String.format("\uD83D\uDCCD %s", dataModel.getAddress()));
        holder.Text_TimeUploaded.setText(String.format("⏰ %s", dataModel.getEpoch()));
        holder.Text_NumberOfLikes.setText(String.format("%s", dataModel.getTotalLikes()));
        dataModel.setFullName(holder.TextUsernameUploaded);
        if(dataModel.getImage()!=null) {
            Picasso.get().load(dataModel.getImage()).error(R.drawable.default_parking).placeholder(R.drawable.progress_animation).into(holder.imageView);
        }

        LocationHelper locationHelper = new LocationHelper(context);
        locationHelper.setDistanceToLocation(holder.TextDistanceFromUser, dataModel.getLocation());
        holder.LikeButton.setOnClickListener(v -> B_Like(dataModel, holder));
        holder.open.setOnClickListener(v -> B_OpenParking(dataModel));

        //algorithm for Probability
        String dateString = dataModel.getEpoch();
        DateFormat format = new SimpleDateFormat("MMM d, yyyy h:mm", Locale.ENGLISH);
        long epochMillis;
        try {
            Date date = format.parse(dateString);
            epochMillis = date.getTime(); // Get the time in milliseconds since the epoch
        } catch (ParseException e) {
            // Handle parsing exception
            e.printStackTrace();
            return; // Exit method or handle appropriately
        }
        long currentTimeMillis = System.currentTimeMillis();
        long differenceInMillis = currentTimeMillis - epochMillis;
        long differenceInMinutes = differenceInMillis / (60 * 1000);
        if(dataModel.getLikeStatus()){
            holder.LikeButton.setColorFilter(Color.RED);}
        else{
            holder.LikeButton.setColorFilter(Color.BLACK);
        }
        // Check if the difference is greater than 15 minutes
        if (differenceInMinutes > 15) {
            holder.Text_Probability.setText("Low Chance");
            holder.Text_Probability.setTextColor(Color.parseColor("#f00505"));
        } else {
            holder.Text_Probability.setText("High Chance");
            holder.Text_Probability.setTextColor(Color.parseColor("#05f028"));

        }
    }



    private void B_Like(Post dataModel, MyViewHolder holder) {
        Animation rotate = AnimationUtils.loadAnimation(context, R.anim.hearbeat_anim);
        holder.LikeButton.startAnimation(rotate);

        String post_id = dataModel.getPostID();
        if(dataModel.getLikeStatus()){
            holder.LikeButton.setColorFilter(Color.BLACK);
            FireBaseHandler fireBaseHandler = new FireBaseHandler();
            fireBaseHandler.unlikePost(post_id);
            dataModel.setTotalLikes(String.valueOf(Integer.parseInt(dataModel.getTotalLikes())-1));
            dataModel.setLikeStatus(false);
        }else{
            holder.LikeButton.setColorFilter(Color.RED);
            FireBaseHandler fireBaseHandler = new FireBaseHandler();
            fireBaseHandler.likePost(post_id);
            dataModel.setTotalLikes(String.valueOf(Integer.parseInt(dataModel.getTotalLikes())+1));
            dataModel.setLikeStatus(true);
        }

        for (CustomAdapter adapter : adapters) {
            adapter.notifyDataSetChanged();
        }
    }



    private void B_OpenParking(Post dataModel) {

        Intent intent = new Intent(context, Parking.class);
        //crate a json for the parking

        intent.putExtra("Parking", dataModel.toString());
        //intent.putExtra("user", user.toString());
        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}


