package com.example.projectandroid1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class AddParkingFragment extends Fragment {
    ImageView Parking_photo,Add_from_gal,Add_from_cam;
    Button PinPoint;
    Switch Switch1;
    RadioGroup radioGroup;
    static final int REQUEST_SELECT_IMAGE = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MAP_POPUP_REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_parking, container, false);

        Add_from_gal = rootView.findViewById(R.id.B_AddPhotoFromGal);
        PinPoint = rootView.findViewById(R.id.B_PinPoint);
        Add_from_cam = rootView.findViewById(R.id.B_TakePhoto);
        Parking_photo = rootView.findViewById(R.id.imageView3);
        Switch1 = rootView.findViewById(R.id.switch1);
        radioGroup = rootView.findViewById(R.id.radio_group);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Switch1.setText("Paid parking");
                } else {
                    Switch1.setText("Free parking");
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Uncheck all other radio buttons when one is checked
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) group.getChildAt(i);
                    radioButton.setChecked(radioButton.getId() == checkedId);
                }
            }
        });

        Add_from_gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });
        Add_from_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        PinPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapPopup();
            }
        });
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            mGetContent.launch(takePictureIntent);
        } else {
            Log.e("AddParking", "No camera app available to handle photo capture intent");
            showNoCameraAppAlert();
        }
    }
    private void showNoCameraAppAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("No camera app available. its an emulator problem")
                .setTitle("Error")
                .setPositiveButton("OK", null); // You can handle button click if needed
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mGetContent.launch(intent);
    }
    public void openMapPopup() {
        Intent intent = new Intent(requireContext(), MapPopupActivity.class);
        mapPopupLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImage = data.getData();
                            Parking_photo.setImageURI(selectedImage);
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> mapPopupLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // Handle the result of map popup activity here if needed
                    }
                }
            });

}
