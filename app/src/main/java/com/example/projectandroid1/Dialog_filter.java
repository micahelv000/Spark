package com.example.projectandroid1;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Dialog_filter extends DialogFragment {

    // Define an interface
    public interface FilterListener
    {
        void onFilterApplied(boolean isBigCar, boolean isRegularCar, boolean isSmallCar,
                             boolean isParallelP, boolean isPerpendicularP, boolean isFreeP,
                             boolean isPaidP,int typedistance);
    }
    private FilterListener filterListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_dialog_filter, null);

        // Find checkboxes

        CheckBox checkbox00 = dialogView.findViewById(R.id.checkBox00);
        CheckBox checkbox01 = dialogView.findViewById(R.id.checkBox01);
        CheckBox checkbox02 = dialogView.findViewById(R.id.checkBox02);
        CheckBox checkbox10 = dialogView.findViewById(R.id.ParallelParkingCheckBox);
        CheckBox checkbox11 = dialogView.findViewById(R.id.PerpendicularParkingCheckBox);
        CheckBox checkbox20 = dialogView.findViewById(R.id.checkBox20);
        CheckBox checkbox21 = dialogView.findViewById(R.id.checkBox21);
        RadioButton radioButton30 = dialogView.findViewById(R.id.checkBox30);
        RadioButton radioButton31 = dialogView.findViewById(R.id.checkBox31);
        RadioButton radioButton32 =  dialogView.findViewById(R.id.checkBox32);
        // Enable all checkboxes by default
        checkbox00.setChecked(true);
        checkbox01.setChecked(true);
        checkbox02.setChecked(true);
        checkbox10.setChecked(true);
        checkbox11.setChecked(true);
        checkbox20.setChecked(true);
        checkbox21.setChecked(true);
        radioButton32.setChecked(true);


// need also to save the state of them when pressing again the button.
        builder.setView(dialogView)
        .setTitle("Filter Properties")
        .setPositiveButton("Save", (dialog, which) -> {

            boolean isBigCar = checkbox00.isChecked();
            boolean isRegularCar = checkbox01.isChecked();
            boolean isSmallCar = checkbox02.isChecked();
            boolean isParllerP = checkbox10.isChecked();
            boolean isPerpendicularP = checkbox11.isChecked();
            boolean isFreeP = checkbox20.isChecked();
            boolean isPaidP = checkbox21.isChecked();

            boolean isFive = radioButton30.isChecked();
            boolean isTen = radioButton31.isChecked();
            boolean isAll = radioButton32.isChecked();
            int typeDistance;
            if(isFive){
                typeDistance =0;
            }else if(isTen){
                typeDistance =1;
            }else{
                typeDistance =2;
            }
            // filter or pass to the parent java class and then filter the properties
            if (filterListener != null) {
                filterListener.onFilterApplied(isBigCar, isRegularCar, isSmallCar,
                        isParllerP, isPerpendicularP, isFreeP, isPaidP,typeDistance);
            }
        })
        .setNegativeButton("Cancel", null); // Cancel button, no action

        return builder.create();
    }
    public void setFilterListener(FilterListener listener) {
        this.filterListener = listener;
    }
}
