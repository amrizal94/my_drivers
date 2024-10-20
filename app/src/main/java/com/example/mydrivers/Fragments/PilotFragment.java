package com.example.mydrivers.Fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mydrivers.Adapter.driverInfoAdapter;
import com.example.mydrivers.Model.userModel;
import com.example.mydrivers.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PilotFragment extends Fragment {
    private FirebaseFirestore firestore;
    private driverInfoAdapter adapter;
    private userModel model;
    private RecyclerView driverRecyclerView;
    private String longitude, latitude, location;
    private ArrayList<userModel> userInfoList;
    private ListenerRegistration userListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pilot, container, false);
        driverRecyclerView = view.findViewById(R.id.rv_driver);
        firestore = FirebaseFirestore.getInstance();
        userInfoList = new ArrayList<>();
        driverRecyclerView.setHasFixedSize(true);
        GetDriverInfo();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onDestroyView();
        stopListening(); // Atau hentikan listener ketika view dihancurkan
    }

    private void stopListening() {
        if (userListener != null) {
            userListener.remove(); // Hentikan Firestore listener
            userListener = null;
        }
    }

    private void GetDriverInfo() {
        userListener = firestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "Listen failed "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (snapshots != null && !snapshots.isEmpty()) {
                    List<DocumentSnapshot> docs = snapshots.getDocuments();
                    userInfoList.clear(); // Clear the list before adding new data

                    for (DocumentSnapshot document : docs) {
                        model = document.toObject(userModel.class);

                        if (model != null) {
                            String longitude = model.getLongitude();
                            String latitude = model.getLatitude();

                            // Validasi apakah longitude dan latitude ada dan valid
                            if (longitude != null && latitude != null) {
                                try {
                                    double lat = Double.parseDouble(latitude);
                                    double longi = Double.parseDouble(longitude);

                                    // Mendapatkan alamat dari latitude dan longitude
                                    getAddressFromLatLong(getContext(), lat, longi);

                                    // Tambahkan lokasi asli ke model dan list
                                    model.setRealLocation(location);
                                    userInfoList.add(model);

                                } catch (NumberFormatException e) {
                                    Toast.makeText(getContext(), "GetDriverInfo: "+"Invalid latitude/longitude format " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    driverInfoAdapter adapter = new driverInfoAdapter(userInfoList, getContext());
                    driverRecyclerView.setAdapter(adapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    driverRecyclerView.setLayoutManager(layoutManager);
                    // Refresh the adapter or UI here
                    adapter.notifyDataSetChanged();
                }

            }
        });
//        firestore.collection("Users").get().addOnSuccessListener(queryDocumentSnapshots -> {
//            if (!queryDocumentSnapshots.isEmpty()) {
//                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                for (DocumentSnapshot document : list) {
//                    model = document.toObject(userModel.class);
//
//                    if (model != null) {
//                        String longitude = model.getLongitude();
//                        String latitude = model.getLatitude();
//
//                        // Validasi apakah longitude dan latitude ada dan valid
//                        if (longitude != null && latitude != null) {
//                            try {
//                                double lat = Double.parseDouble(latitude);
//                                double longi = Double.parseDouble(longitude);
//
//                                // Mendapatkan alamat dari latitude dan longitude
//                                getAddressFromLatLong(getContext(), lat, longi);
//
//                                // Tambahkan lokasi asli ke model dan list
//                                model.setRealLocation(location);
//                                userInfoList.add(model);
//
//                            } catch (NumberFormatException e) {
//                                Toast.makeText(getContext(), "GetDriverInfo: "+"Invalid latitude/longitude format " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                }
//
//                // Buat adapter dan layoutManager setelah loop selesai
//                driverInfoAdapter adapter = new driverInfoAdapter(userInfoList, getContext());
//                driverRecyclerView.setAdapter(adapter);
//                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//                driverRecyclerView.setLayoutManager(layoutManager);
//
//                // Beritahu adapter bahwa data telah berubah
//                adapter.notifyDataSetChanged();
//            }
//        }).addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());
    }

    public void getAddressFromLatLong(Context context, double latitude, double longitude){
        String address = "";
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0){
                address = addresses.get(0).getAddressLine(0);
            }
            location = address;
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}