package com.example.mydrivers.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydrivers.Model.userModel;
import com.example.mydrivers.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.util.Objects;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;

public class SignupFragment extends Fragment {
    private TextView tInfo;
    private EditText edName, edEmail, edNumber, edPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    String UserId;
    KProgressHUD kProgressHUD;



    public SignupFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        edName = view.findViewById(R.id.textName);
        edEmail = view.findViewById(R.id.textEmail);
        edNumber = view.findViewById(R.id.textNumber);
        edPassword = view.findViewById(R.id.textPassword);
        AppCompatButton signUpButton = view.findViewById(R.id.button_sign_up);
        tInfo = view.findViewById(R.id.text_info);
        tInfo.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        view.setOnTouchListener((v, event) -> {
            hideKeyboard();
            return false;
        });

        edEmail.setOnTouchListener((v, event) -> {
            tInfo.setVisibility(View.GONE);
            return false;
        });

        signUpButton.setOnClickListener(v -> SignUpUser());


        return view;
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (requireActivity().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), 0);
        }
    }

    private void Progressbar(){
        kProgressHUD = KProgressHUD.create(requireContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setMaxProgress(100)
                .setBackgroundColor(R.color.purple_200)
                .show();
        kProgressHUD.setProgress(90);
    }
    private void SignUpUser() {

        String name = edName.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
        String number = edNumber.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(number) || TextUtils.isEmpty(password)){
            Toast.makeText(getContext(), "Please fill all fields",Toast.LENGTH_SHORT).show();
        }else {
            Progressbar();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    kProgressHUD.dismiss();
                    if (firebaseAuth.getCurrentUser() != null){
                         UserId = firebaseAuth.getCurrentUser().getUid();
                    }
                    DocumentReference UserInfo =  firestore.collection("Users").document(UserId);
                    userModel model = new userModel(name, email,number, password, UserId);
                    UserInfo.set(model, SetOptions.merge())
                            .addOnSuccessListener(unused -> Toast.makeText(getContext(), "User registered successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).addOnFailureListener(e -> {
                kProgressHUD.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                String errorMessage = Objects.requireNonNull(e.getMessage());
                if (errorMessage.contains("email")) {
                    edEmail.setText("");
                    tInfo.setVisibility(View.VISIBLE);
                } else if (errorMessage.contains("password")) {
                    edPassword.setText("");
                }
            });
        }
    }
}