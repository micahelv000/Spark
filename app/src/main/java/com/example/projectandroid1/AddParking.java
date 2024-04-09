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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class AddParking extends AppCompatActivity {
    ImageView parkingPhotoImageView;
    Uri selectedImage;
    SwitchCompat parkingFeeSwitch;
    RadioGroup radioGroup;
    Location selectedLocation;
    EditText addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking);

        parkingPhotoImageView = findViewById(R.id.imageView3);
        parkingFeeSwitch = findViewById(R.id.FreeParkingSwitch);
        radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Uncheck all other radio buttons when one is checked
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) group.getChildAt(i);
                radioButton.setChecked(radioButton.getId() == checkedId);
            }
        });
        parkingFeeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                parkingFeeSwitch.setText(getResources().getString(R.string.paid_parking));
            } else {
                parkingFeeSwitch.setText(getResources().getString(R.string.free_parking));
            }
        });

        Button submitButton = findViewById(R.id.b_Add_this);
        submitButton.setOnClickListener(this::onSubmitButtonClick);
        addressEditText = findViewById(R.id.addressEditText);
        LocationHelper locationHelper = new LocationHelper(this);
        locationHelper.setAddressToTextView(addressEditText, locationHelper.getLocation());
    }

    public void onSubmitButtonClick(View view) {
        if (Validation(view)) {
            String[] parkingType = new String[2];
            if (((CheckBox) findViewById(R.id.ParallelParkingCheckBox)).isChecked()) {
                parkingType[0] = "Parallel";
            }
            if (((CheckBox) findViewById(R.id.PerpendicularParkingCheckBox)).isChecked()) {
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

            boolean isFree = !parkingFeeSwitch.isChecked();

            if (selectedLocation == null) {
                String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();
                selectedLocation = LocationHelper.getLocationFromAddress(address, this);
            }

            FireBaseHandler fireBaseHandler = new FireBaseHandler();
            fireBaseHandler
                    .uploadPost(selectedImage, carType, parkingType, isFree, selectedLocation,
                            FireBaseHandler.getCurrentUser(), this)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(this, LayoutFragments.class);
                            intent.putExtras(getIntent());
                            startActivity(intent);
                        } else {
                            Log.e("AddParking", "Failed to upload post", task.getException());
                        }
                    });
        }
    }

    private boolean Validation(View view) {
        // check that photo had been uploaded
        if (selectedImage == null) {
            Toast.makeText(AddParking.this, "You must upload a photo", Toast.LENGTH_SHORT).show();
            return false;
        }
        // check address is not empty
        if (addressEditText.getText().toString().isEmpty()) {
            Toast.makeText(AddParking.this, "You must add an address", Toast.LENGTH_SHORT).show();
            return false;
        }
        // check picked line 1
        boolean flag = false;
        for (int i = 0; i < radioGroup.getChildCount() && (!flag); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                flag = true;
            }
        }
        if (!flag) {
            Toast.makeText(AddParking.this, "Choose Parking size", Toast.LENGTH_SHORT).show();
            return false;
        }

        // check picked line 2
        boolean op1 = (((CheckBox) findViewById(R.id.ParallelParkingCheckBox)).isChecked());
        boolean op2 = (((CheckBox) findViewById(R.id.PerpendicularParkingCheckBox)).isChecked());

        if ((!op1) && (!op2)) {
            Toast.makeText(AddParking.this, "You must choose the type of the parking", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
                            parkingPhotoImageView.setImageURI(selectedImage);
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
