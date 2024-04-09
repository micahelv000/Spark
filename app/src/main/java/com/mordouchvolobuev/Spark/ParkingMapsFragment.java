package com.mordouchvolobuev.Spark;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Objects;

public class ParkingMapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<LatLng> locations;
    private String[] addressArray;
    private String[] epochsArray;
    private String[] likesArray;
    private String[] postPicturesArray;
    private Location[] locationArray;
    private String[] userIdArray;
    private String[] postIDArray;
    private boolean[] likeStatusArray;
    private boolean[] isFreeArray;
    private String[] carTypeArray;
    private String[][] parkingTypeArray;
    private Post[] posts;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        PostDataProcessor postDataProcessor = new PostDataProcessor();
        postDataProcessor.populateArrays(() -> {
            addressArray = postDataProcessor.getAddressArray();
            epochsArray = postDataProcessor.getEpochsArray();
            likesArray = postDataProcessor.getLikesArray();
            postPicturesArray = postDataProcessor.getPostPicturesArray();
            locationArray = postDataProcessor.getLocationArray();
            userIdArray = postDataProcessor.getUserIdArray();
            postIDArray = postDataProcessor.getPostIdsArray();
            likeStatusArray = postDataProcessor.getLikeStatusArray();
            carTypeArray = postDataProcessor.getCarTypeArray();
            isFreeArray = postDataProcessor.getIsFreeArray();
            parkingTypeArray = postDataProcessor.getParkingTypeArray();

            // Access locationArray only after it's populated
            locations = new ArrayList<>();
            for (Location location : locationArray) {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    locations.add(latLng);
                }
            }
            posts = new Post[locations.size()]; // Initialize posts array here

            initializeMap(); // Call initializeMap after locationArray is populated
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        initializeMap();
                    }
                });

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            initializeMap();
        }

        return view;
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        // Check if locations ArrayList is null or not
        if (locations != null) {
            for (int i = 0; i < locations.size(); i++) {
                posts[i] = new Post(addressArray[i], epochsArray[i], likesArray[i], postPicturesArray[i],
                        locationArray[i], userIdArray[i], postIDArray[i], likeStatusArray[i], carTypeArray[i],
                        isFreeArray[i], parkingTypeArray[i]);
                LatLng location = locations.get(i);
                mMap.addMarker(new MarkerOptions().position(location).title(String.valueOf(i)));
            }
        }
        mMap.setOnMarkerClickListener(marker -> {
            try {
                int markerIndex = Integer.parseInt(Objects.requireNonNull(marker.getTitle()));
                Intent intent = new Intent(getActivity(), Parking.class);
                intent.putExtra("Parking", posts[markerIndex].toString());
                startActivity(intent);
            } catch (NumberFormatException e) {
                // Handle the case where title is not a valid integer
                Toast.makeText(requireContext(), "Invalid marker title", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                        }
                    });
        }
    }
}
