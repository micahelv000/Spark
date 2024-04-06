package com.example.projectandroid1;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class AddParking extends AppCompatActivity {
    ImageView Parking_photo;
    Uri selectedImage;
    SwitchCompat Switch1;
    RadioGroup radioGroup;
    Location selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking);

        Parking_photo = findViewById(R.id.imageView3);
        Switch1 = findViewById(R.id.FreeParkingSwitch);
        radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Uncheck all other radio buttons when one is checked
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) group.getChildAt(i);
                radioButton.setChecked(radioButton.getId() == checkedId);
            }
        });
        Switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Switch1.setText(getResources().getString(R.string.paid_parking));
            } else {
                Switch1.setText(getResources().getString(R.string.free_parking));
            }
        });

        Button submitButton = findViewById(R.id.b_Add_this);
        submitButton.setOnClickListener(this::onSubmitButtonClick);
        
        LocationHelper locationHelper = new LocationHelper(this);
        locationHelper.setAddressToTextView(findViewById(R.id.addressEditText), locationHelper.getLocation());
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
                    LocationHelper locationHelper = new LocationHelper(AddParking.this);
                    locationHelper.setAddressToTextView(findViewById(R.id.addressEditText), selectedLocation);
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
