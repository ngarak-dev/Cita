package me.ngarak.cita.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import me.ngarak.cita.QuoteAnalytics;
import me.ngarak.cita.R;
import me.ngarak.cita.ReferralStore;
import me.ngarak.cita.ads.SupportAd;
import me.ngarak.cita.databinding.ActivityMainBinding;
import me.ngarak.cita.perm;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.container, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            binding.appBarLayout.setPadding(0, systemBars.top, 0, 0);
            binding.navView.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment == null) {
            throw new IllegalStateException("Nav host fragment missing");
        }
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navView, navController);

        new perm().reQuestStorage(MainActivity.this);

        // Phase 2: first-run taste onboarding (after UI is ready).
        binding.getRoot().post(() -> OnboardingHelper.maybeShow(MainActivity.this, null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            onAboutDialog();
            return true;
        }
        if (id == R.id.invite) {
            ReferralStore referral = new ReferralStore(this);
            new QuoteAnalytics(this).referralShared();
            Toast.makeText(this, getString(R.string.referral_my_code, referral.myCode()),
                    Toast.LENGTH_LONG).show();
            startActivity(referral.shareInviteIntent());
            return true;
        }
        if (id == R.id.redeem) {
            showRedeemDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRedeemDialog() {
        ReferralStore referral = new ReferralStore(this);
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint(R.string.referral_redeem_hint);
        input.setSingleLine(true);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(this)
                .setTitle(R.string.referral_redeem_title)
                .setView(input)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    ReferralStore.RedeemResult result = referral.redeem(input.getText().toString());
                    QuoteAnalytics analytics = new QuoteAnalytics(this);
                    if (result == ReferralStore.RedeemResult.OK) {
                        analytics.referralRedeemed(true);
                        Toast.makeText(this, R.string.referral_redeem_ok, Toast.LENGTH_LONG).show();
                    } else if (result == ReferralStore.RedeemResult.ALREADY) {
                        analytics.referralRedeemed(false);
                        Toast.makeText(this, R.string.referral_redeem_already, Toast.LENGTH_SHORT).show();
                    } else {
                        analytics.referralRedeemed(false);
                        Toast.makeText(this, R.string.referral_redeem_invalid, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void onAboutDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_about, null);
        dialogBuilder.setTitle(R.string.about_title);
        dialogBuilder.setView(dialogView);

        MaterialButton showAd = dialogView.findViewById(R.id.showAd);
        TextView cita = dialogView.findViewById(R.id.cita);

        AlertDialog alertDialog = dialogBuilder.create();

        showAd.setOnClickListener(v -> {
            alertDialog.dismiss();
            attemptToShowAd();
        });
        cita.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("https://github.com/Ngara-K/Cita"))));

        alertDialog.show();
    }

    private void attemptToShowAd() {
        Toast.makeText(this, R.string.loading_ad, Toast.LENGTH_SHORT).show();
        new SupportAd().loadAd(this, this, null);
    }
}
