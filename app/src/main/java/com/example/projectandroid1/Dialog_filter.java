package com.example.projectandroid1;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Dialog_filter extends DialogFragment {

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


// need also to save the state of them when pressing again the button.
                builder.setView(dialogView)
                .setTitle("Filter Properties")
                .setPositiveButton("Save", (dialog, which) -> {

                    boolean isChecked00 = checkbox00.isChecked();
                    boolean isChecked01 = checkbox01.isChecked();
                    boolean isChecked02 = checkbox02.isChecked();
                    boolean isChecked10 = checkbox10.isChecked();
                    boolean isChecked11 = checkbox11.isChecked();
                    boolean isChecked20 = checkbox20.isChecked();
                    boolean isChecked21 = checkbox21.isChecked();

                    // filter or pass to the parent java class and then filter the properties

                })
                .setNegativeButton("Cancel", null); // Cancel button, no action

        return builder.create();
    }
}
