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

    public interface FilterListener {
        void onFilterApplied(boolean isBigCar, boolean isRegularCar, boolean isSmallCar,
                             boolean isParallelP, boolean isPerpendicularP, boolean isFreeP,
                             boolean isPaidP, int typeDistance);
    }

    private FilterListener filterListener;

    // Factory method to create Dialog_filter instance with arguments
    public static Dialog_filter newInstance(boolean isBigCar, boolean isRegularCar, boolean isSmallCar,
                                            boolean isParallelP, boolean isPerpendicularP, boolean isFreeP,
                                            boolean isPaidP, int typeDistance) {
        Dialog_filter fragment = new Dialog_filter();
        Bundle args = new Bundle();
        args.putBoolean("isBigCar", isBigCar);
        args.putBoolean("isRegularCar", isRegularCar);
        args.putBoolean("isSmallCar", isSmallCar);
        args.putBoolean("isParallelP", isParallelP);
        args.putBoolean("isPerpendicularP", isPerpendicularP);
        args.putBoolean("isFreeP", isFreeP);
        args.putBoolean("isPaidP", isPaidP);
        args.putInt("typeDistance", typeDistance);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_dialog_filter, null);

        // Find checkboxes and radio buttons
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

        Bundle args = getArguments();
        if (args != null) {
            checkbox00.setChecked(args.getBoolean("isBigCar"));
            checkbox01.setChecked(args.getBoolean("isRegularCar"));
            checkbox02.setChecked(args.getBoolean("isSmallCar"));
            checkbox10.setChecked(args.getBoolean("isParallelP"));
            checkbox11.setChecked(args.getBoolean("isPerpendicularP"));
            checkbox20.setChecked(args.getBoolean("isFreeP"));
            checkbox21.setChecked(args.getBoolean("isPaidP"));
            radioButton30.setChecked(args.getInt("typeDistance") == 0);
            radioButton31.setChecked(args.getInt("typeDistance") == 1);
            radioButton32.setChecked(args.getInt("typeDistance") == 2);
        }


        builder.setView(dialogView)
                .setTitle("Filter Properties")
                .setPositiveButton("Save", (dialog, which) -> {
                    boolean isBigCar = checkbox00.isChecked();
                    boolean isRegularCar = checkbox01.isChecked();
                    boolean isSmallCar = checkbox02.isChecked();
                    boolean isParallelP = checkbox10.isChecked();
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

                    if (filterListener != null) {
                        filterListener.onFilterApplied(isBigCar, isRegularCar, isSmallCar,
                                isParallelP, isPerpendicularP, isFreeP, isPaidP, typeDistance);
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    public void setFilterListener(FilterListener listener) {
        this.filterListener = listener;
    }
}
