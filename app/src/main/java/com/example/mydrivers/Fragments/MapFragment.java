package com.example.mydrivers.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mydrivers.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    private double longitude, latitude;
    private String userName;

    private MapView map;

    public MapFragment() {
        // Required empty public constructor
    }
    public static MapFragment newInstance(String userName, double latitude, double longitude) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString("userName", userName);
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        if (getArguments() != null) {
            userName = getArguments().getString("userName");
            latitude = getArguments().getDouble("latitude", 0);
            longitude = getArguments().getDouble("longitude", 0);
        }

        // Inisialisasi peta
        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Aktifkan zoom dan multi-touch controls
        map.setMultiTouchControls(true);

        // Atur cache storage dan User-Agent
        Configuration.getInstance().setUserAgentValue("amrizal94/my_drivers:1.0"); // Menggunakan nama paket aplikasi

        // Set zoom level dan posisi
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);

        // Tambahkan marker untuk lokasi
        Marker marker = new Marker(map);
        marker.setPosition(startPoint);
        marker.setTitle(userName);
        map.getOverlays().add(marker);

        return view;
    }
}