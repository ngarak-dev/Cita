package me.ngarak.cita.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import me.ngarak.cita.R;

public class SplashActivity extends AppCompatActivity {

    public static final String EXTRA_OPEN_DAILY_DROP = "open_daily_drop";
    private static final int SPLASH_TIME_MS = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_splash);

        final boolean openDaily = getIntent() != null
                && getIntent().getBooleanExtra(EXTRA_OPEN_DAILY_DROP, false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isFinishing()) return;
            Intent main = new Intent(SplashActivity.this, MainActivity.class);
            if (openDaily) {
                main.putExtra(MainActivity.EXTRA_OPEN_DAILY_DROP, true);
            }
            startActivity(main);
            finish();
        }, openDaily ? 400 : SPLASH_TIME_MS);
    }
}
