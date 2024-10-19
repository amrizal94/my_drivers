package com.example.mydrivers.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydrivers.Model.LocationViewModel;
import com.example.mydrivers.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private LocationViewModel locationViewModel;

    private TextView tv_location;
    private Uri uriImage;
    private String uriPhoto;
    private Bitmap bitmap;
    private ImageView ivProfile;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ivProfile = view.findViewById(R.id.image_profile);
        tv_location = view.findViewById(R.id.tx_location);
        MaterialCardView selectPhoto = view.findViewById(R.id.cv_photo);
        selectPhoto.setOnClickListener(v -> CheckStoragePermission());

        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        firestore = FirebaseFirestore.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        locationViewModel.getLocation().observe(getViewLifecycleOwner(), location -> tv_location.setText(location));
        return view;
    }

    private void CheckStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            PickImageFromGallery();
        }
    }

    private void PickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }

    ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            if (data != null && data.getData() != null){
                                uriImage = data.getData();
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(
                                      requireActivity().getContentResolver(), uriImage
                                    );
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "failed convert to bitmap "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (uriImage != null){
                                ivProfile.setImageBitmap(bitmap);
                                Toast.makeText(getContext(), "running upload image", Toast.LENGTH_LONG).show();
                                UploadImage();
                            }
                        }
                    });
    private void UploadImage(){
        if (uriImage != null){
            final StorageReference myRef = storageReference.child("photo/" + uriImage.getLastPathSegment());
            myRef.putFile(uriImage)
                    .addOnSuccessListener(taskSnapshot -> myRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                if (uri != null){
                                    uriPhoto = uri.toString();
                                    UpdateImage();
                                }
                            }).addOnFailureListener(e -> Toast.makeText(getContext(), "DownloadUrl failed "+e.getMessage(), Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "put file failed " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void UpdateImage(){
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("imageProfile", uriPhoto);

        firestore.collection("Users").document(currentUserId)
                .update(updateData)
                .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Photo profile updated", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}