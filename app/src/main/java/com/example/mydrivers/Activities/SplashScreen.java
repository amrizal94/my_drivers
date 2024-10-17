package com.example.mydrivers.Activities;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydrivers.R;

public class SplashScreen extends AppCompatActivity {
    ImageView Roadmap, Car;
    TextView text1, text2;

    Animation Top_anim, Bottom_anim, Left_anim;

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
    }
}
