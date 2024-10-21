package com.example.mydrivers.Utils;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PushLocation {
    public static String updateLocation(Context context, FirebaseFirestore firestore, String currentUserId, double latitude, double longitude) {
        String address = getAddressFromLatLong(context, latitude, longitude);
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("latitude", String.valueOf(latitude));
        updateData.put("longitude", String.valueOf(longitude));
        updateData.put("realLocation", address);

        firestore.collection("Users").document(currentUserId)
                .update(updateData)
                .addOnSuccessListener(unused -> Toast.makeText(context, "Location updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        return address;
    }

    // Assume this is your method to get address from latitude and longitude
    private static String getAddressFromLatLong(Context context, double latitude, double longitude) {
        String address = "";
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()){
                address = addresses.get(0).getAddressLine(0);
            }
            if (address.isEmpty()){
                address = "address unknown";
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }
}
