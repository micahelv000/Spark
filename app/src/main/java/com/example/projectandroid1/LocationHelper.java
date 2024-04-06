package com.example.projectandroid1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class LocationHelper {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private final LocationManager locationManager;
    private final Geocoder geocoder;
    private final Context context;

    public LocationHelper(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(context, Locale.getDefault());
        this.context = context;
    }

    public boolean isLocationPermissionMissing() {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    public void checkLocationPermission() {
        if (isLocationPermissionMissing()) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    public Location getLocation() {
        try {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            return null;
        }
    }

    public void LocationUpdater(EditText editTextCity, EditText editTextCountry) {
        Consumer<Location> locationConsumer = location -> {
            String[] cityCountry = getCityCountryFromLocation(location);
            if (cityCountry != null) {
                editTextCity.setText(cityCountry[0]);
                editTextCountry.setText(cityCountry[1]);
            }
        };

        updateLocation(locationConsumer);
    }

    public void setDistanceToLocation(TextView textView, Location targetLocation) {
        Consumer<Location> locationConsumer = location -> {
            float distanceInMeters = location.distanceTo(targetLocation);
            float distanceInKilometers = distanceInMeters / 1000;
            textView.setText(String.format(Locale.getDefault(), "\uD83D\uDCCF %.2f km", distanceInKilometers));
        };

        updateLocation(locationConsumer);
    }

    public void setAddressToTextView(TextView textView, Location location) {
        String address = getAddressFromLocation(location, context);
        if (address != null) {
            textView.setText(address);
        }
    }

    private void updateLocation(Consumer<Location> locationConsumer) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null,
                    context.getMainExecutor(), locationConsumer);
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
        } catch (IOException ignored) {
        }
        return null;
    }

    public static Location getLocationFromAddress(String address, Context context) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address firstAddressResult = addresses.get(0);
                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(firstAddressResult.getLatitude());
                location.setLongitude(firstAddressResult.getLongitude());
                return location;
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public static String getAddressFromLocation(Location location, Context context) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public Location getLocationFromCityCountry(String city, String country) {
        try {
            List<Address> addresses = geocoder.getFromLocationName(city + ", " + country, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(address.getLatitude());
                location.setLongitude(address.getLongitude());
                return location;
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public int getRequestLocationPermissionCode() {
        return REQUEST_LOCATION_PERMISSION;
    }

}
