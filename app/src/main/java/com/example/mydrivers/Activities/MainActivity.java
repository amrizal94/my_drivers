package com.example.mydrivers.Activities;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.mydrivers.Fragments.MapFragment;
import com.example.mydrivers.Fragments.PilotFragment;
import com.example.mydrivers.Fragments.ProfileFragment;
import com.example.mydrivers.Model.LocationViewModel;
import com.example.mydrivers.R;
import com.example.mydrivers.Services.LocationService;
import com.example.mydrivers.Utils.PushLocation;
import com.example.mydrivers.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class  MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FirebaseFirestore firestore;
    private String currentUserId, address;
    private LocationViewModel locationViewModel;
    public ActivityMainBinding binding;
    LocationManager locationManager;
    LocationListener locationListener;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        binding.bottomNavigationView.setSelectedItemId(R.id.item_driver);
        replaceFragment(new PilotFragment());

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        firestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.item_map:
                    replaceFragment(new MapFragment());
                    break;
                case R.id.item_driver:
                    replaceFragment(new PilotFragment());
                    break;
                case R.id.item_profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationListener = location -> {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                updateLocation(latitude, longitude);
            };
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, locationListener);
            }catch (Exception e){
                Toast.makeText(this, "Reqeust GPS failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            try {
                Intent serviceIntent = new Intent(this, LocationService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "background service failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionExplanation(Manifest.permission.ACCESS_FINE_LOCATION,
                        "Location access is required to track your location.",
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getCurrentLocation();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showSettingsDialog();
                }
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Location Permission");
        builder.setMessage("This app needs location permission. You can grant it in app settings.");
        builder.setPositiveButton("Go to Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            closeApp();
        });
        builder.show();
    }

    private void closeApp() {
        Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        finishAffinity();
        System.exit(0);
    }

    private void showPermissionExplanation(String permission, String message, final int requestCode) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Request the permission after explaining
                    requestPermissions(new String[]{permission}, requestCode);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the cancel action if necessary
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                })
                .create()
                .show();
    }

    private void updateLocation(double latitude, double longitude) {
        address = PushLocation.updateLocation(this, firestore, currentUserId, latitude,longitude);
        String location = ("Latitude: " + latitude + "\nLongitude: " + longitude + "\n" + address);
        locationViewModel.setLocation(location);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}