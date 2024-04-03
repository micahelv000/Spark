package com.example.projectandroid1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class AddParking extends AppCompatActivity {
    ImageView Parking_photo;
    Uri selectedImage;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch Switch1;
    RadioGroup radioGroup;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_SELECT_IMAGE = 2;
    static final int MAP_POPUP_REQUEST_CODE = 101;

    Location selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking);

        Parking_photo = findViewById(R.id.imageView3);
        Switch1 = findViewById(R.id.FreeParkingSwitch);
        radioGroup = findViewById(R.id.radio_group);
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
        Switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Switch1.setText("Paid parking \uD83D\uDE22");
                } else {
                    Switch1.setText("Free parking \uD83E\uDD73");
                }
            }
        });

        Button submitButton = findViewById(R.id.b_Add_this);
        submitButton.setOnClickListener(this::onSubmitButtonClick);
    }

    public void onSubmitButtonClick(View view) {
        String[] parkingType = new String[2];
        if (findViewById(R.id.ParallelParkingCheckBox).isSelected()) {
            parkingType[0] = "Parallel";
        }
        if (findViewById(R.id.PerpendicularParkingCheckBox).isSelected()) {
            parkingType[1] = "Perpendicular";
        }

        String carType = "";
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                carType = radioButton.getText().toString();
                break;
            }
        }

        boolean isFree = Switch1.isChecked();

        if (selectedLocation == null) {
            String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();
            selectedLocation = LocationHelper.getLocationFromAddress(address, this);
        }

        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        fireBaseHandler.uploadPost(selectedImage, carType, parkingType, isFree, selectedLocation, FireBaseHandler.getCurrentUser(), this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(this, LayoutFragments.class);
                        startActivity(intent);
                    } else {
                        Log.e("AddParking", "Failed to upload post", task.getException());
                    }
                });
    } 

    ActivityResultLauncher<Intent> mGetContent2 = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    double latitude = data.getDoubleExtra("selected_latitude", 0.0);
                    double longitude = data.getDoubleExtra("selected_longitude", 0.0);
                    selectedLocation = new Location("");
                    selectedLocation.setLatitude(latitude);
                    selectedLocation.setLongitude(longitude);
                }
            }
        }
    });

    public void openMapPopup(View view) {
        Intent intent = new Intent(this, MapPopupActivity.class);
        mGetContent2.launch(intent);
    }

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImage = data.getData();
                            Parking_photo.setImageURI(selectedImage);
                        }
                    }
                }
            });
    
    public void AddPhotoFromGal(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mGetContent.launch(intent);
    }

    public void TakePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mGetContent.launch(takePictureIntent);
        } else {
            Log.e("AddParking", "No camera app available to handle photo capture intent");
            showNoCameraAppAlert();
        }
    }

    private void showNoCameraAppAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No camera app available. its an emulator problem")
                .setTitle("Error")
                .setPositiveButton("OK", null); // You can handle button click if needed
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
