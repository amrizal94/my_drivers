package com.example.mydrivers.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydrivers.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    ImageView Roadmap, Car;
    TextView text1, text2;

    Animation Top_anim, Bottom_anim, Left_anim;
    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Roadmap = findViewById(R.id.roadmap);
        Car = findViewById(R.id.car);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.desc);

        Top_anim = AnimationUtils.loadAnimation(this, R.anim.topanim);
        Bottom_anim = AnimationUtils.loadAnimation(this, R.anim.bottomanim);
        Left_anim = AnimationUtils.loadAnimation(this, R.anim.leftanim);

        Roadmap.setAnimation(Top_anim);
        Car.setAnimation(Left_anim);
        text1.setAnimation(Bottom_anim);
        text2.setAnimation(Bottom_anim);

        // Ambil SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        long lastLoginTime = sharedPreferences.getLong("lastLoginTime", 0);
        long currentTimeMillis = System.currentTimeMillis();

        // Hitung waktu login terakhir
        if (lastLoginTime != 0 && (currentTimeMillis - lastLoginTime) < (3 * 30 * 24 * 60 * 60 * 1000L)) {
            // Jika belum 3 bulan, lanjutkan ke MainActivity
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, SPLASH_TIME_OUT);
        } else {
            // Jika sudah 3 bulan atau tidak ada waktu login terakhir, arahkan ke SignInActivity
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashScreen.this, Authentication.class);
                startActivity(intent);
                finish();
            }, SPLASH_TIME_OUT);
        }


    }
}
