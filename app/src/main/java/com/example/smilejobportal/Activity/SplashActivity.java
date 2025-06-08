package com.example.smilejobportal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smilejobportal.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private TextView logoText, subtitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoImageView = findViewById(R.id.logoImageView);
        logoText = findViewById(R.id.logoText);
        subtitleText = findViewById(R.id.subtitleText);

        Animation bounceFade = AnimationUtils.loadAnimation(this, R.anim.fade_zoom);

        // Start animations
        logoImageView.startAnimation(bounceFade);
        logoText.startAnimation(bounceFade);
        subtitleText.startAnimation(bounceFade);

        // Move to MainActivity after animation
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 2000);
    }
}

