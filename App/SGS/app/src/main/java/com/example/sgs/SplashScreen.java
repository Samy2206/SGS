package com.example.sgs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        LottieAnimationView animOpen = findViewById(R.id.animOpen);
        animOpen.setAnimation(R.raw.app_opening_anim_1);
        animOpen.playAnimation();

        TextView txtSGS= findViewById(R.id.txtSGS);
        Animation zoomOpen = AnimationUtils.loadAnimation(this,R.anim.zoom_open);
        txtSGS.setAnimation(zoomOpen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, StartActivity.class));
                finish();
            }
        },3000);

    }
}