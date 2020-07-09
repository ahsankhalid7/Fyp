package com.example.uclone;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    long ANIMATION_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //SplashRun();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
//
//    public void SplashRun(){
//        TextView logo = findViewById(R.id.logo);
//        ObjectAnimator animationFade = ObjectAnimator.ofFloat(logo, View.ALPHA,00f,1.0f);
//        animationFade.setDuration(ANIMATION_TIME);
//        animationFade.start();
//    }

}
