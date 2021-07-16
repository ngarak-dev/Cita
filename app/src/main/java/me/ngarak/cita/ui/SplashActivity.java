package me.ngarak.cita.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import me.ngarak.cita.R;

public class SplashActivity extends AppCompatActivity {

    private final static int SPLASH_TIME = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*delaying activity*/
        new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, MainActivity.class)), SPLASH_TIME);
    }
}