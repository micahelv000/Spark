package com.example.projectandroid1;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationManager locationManager;
    private Geocoder geocoder;

    public LocationHelper(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    public void checkLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    public Location getLocation() {
        try {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[] getCityCountryFromLocation(Location location) {
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                return new String[] { city, country };
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRequestLocationPermissionCode() {
        return REQUEST_LOCATION_PERMISSION;
    }

}
