package com.example.mydrivers.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.mydrivers.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class  MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FirebaseFirestore firestore;
    private String currentUserId;
    private LocationViewModel locationViewModel;
    ActivityMainBinding binding;
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, locationListener);
        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                showPermissionExplanation();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void updateLocation(double latitude, double longitude) {
        String location = ("Latitude: " + latitude + ",\nLongitude: " + longitude);
        locationViewModel.setLocation(location);
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("latitude", latitude);
        updateData.put("longitude", longitude);

        firestore.collection("Users").document(currentUserId)
                .update(updateData)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Location updated", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}