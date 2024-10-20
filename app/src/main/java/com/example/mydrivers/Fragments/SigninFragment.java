package com.example.mydrivers.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.example.mydrivers.Activities.MainActivity;
import com.example.mydrivers.Model.LocationViewModel;
import com.example.mydrivers.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
public class SigninFragment extends Fragment {
    private EditText edEmail, edPassword;
    private FirebaseAuth firebaseAuth;
    private KProgressHUD kProgressHUD;

    public SigninFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        edEmail = view.findViewById(R.id.textEmail);
        edPassword = view.findViewById(R.id.textPassword);
        AppCompatButton signInButton = view.findViewById(R.id.button_sign_in);
        TextView textView = view.findViewById(R.id.info);

        textView.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(v -> SignInUser());

        view.setOnTouchListener((v, event) -> {
            hideKeyboard();
            return false;
        });
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
    private void SignInUser() {
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(getContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
        }else {
            Progressbar();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                kProgressHUD.dismiss();
                if (task.isSuccessful()){
                    long currentTimeMillis = System.currentTimeMillis();
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("lastLoginTime", currentTimeMillis);
                    editor.apply();

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(e -> {
                kProgressHUD.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                edEmail.setText("");
                edPassword.setText("");
            });
        }
    }
}