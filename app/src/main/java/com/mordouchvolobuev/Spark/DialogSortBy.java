package com.mordouchvolobuev.Spark;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogSortBy extends DialogFragment {
    public interface SortByListener {
        void onFilterApplied(int sort);
    }

    private SortByListener sortByListener;

    public static DialogSortBy newInstance(int sort) {
        DialogSortBy fragment = new DialogSortBy();
        Bundle args = new Bundle();
        args.putInt("Sort", sort);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_dialog_sort_by, null);
        RadioButton radioButton00 = dialogView.findViewById(R.id.checkBox00);
        RadioButton radioButton01 = dialogView.findViewById(R.id.checkBox01);
        RadioButton radioButton02 = dialogView.findViewById(R.id.checkBox02);

        Bundle args = getArguments();
        if (args != null) {
            radioButton00.setChecked(args.getInt("Sort") == 0);
            radioButton01.setChecked(args.getInt("Sort") == 1);
            radioButton02.setChecked(args.getInt("Sort") == 2);
        }

        builder.setView(dialogView)
                .setTitle("Sort By")
                .setPositiveButton("Save", (dialog, which) -> {
                    int sort = 0;
                    if (radioButton01.isChecked()) {
                        sort = 1;
                    } else if (radioButton02.isChecked()) {
                        sort = 2;
                    }
                    if (sortByListener != null) {
                        sortByListener.onFilterApplied(sort);
                    }
                });

        return builder.create();
    }

    public void setFilterListener(SortByListener listener) {
        this.sortByListener = listener;
    }
}
