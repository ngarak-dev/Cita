package me.ngarak.cita.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import me.ngarak.cita.ShareLinks;
import me.ngarak.cita.ads.AdsPolicy;
import me.ngarak.cita.ads.SupportAd;
import me.ngarak.cita.billing.RemoveAdsBilling;
import me.ngarak.cita.databinding.ActivityMainBinding;
import me.ngarak.cita.notify.DailyLinePrefs;
import me.ngarak.cita.notify.DailyLineScheduler;
import me.ngarak.cita.perm;
import me.ngarak.cita.ui.random.RandomFragment;
import me.ngarak.cita.CitaPlus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_OPEN_DAILY_DROP = "open_daily_drop";

    private ActivityMainBinding binding;
    private NavController navController;
    private boolean pendingOpenDaily;
    private RemoveAdsBilling removeAdsBilling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.container, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            binding.container.setPadding(0, systemBars.top, 0, 0);
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
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navView, navController);

        new perm().reQuestStorage(MainActivity.this);

        if (RemoveAdsBilling.isEnabled()) {
            removeAdsBilling = new RemoveAdsBilling(this);
            removeAdsBilling.start(null);
        }

        DailyLinePrefs notifyPrefs = new DailyLinePrefs(this);
        if (notifyPrefs.isEnabled()) {
            DailyLineScheduler.scheduleNext(this);
        }

        pendingOpenDaily = getIntent() != null
                && getIntent().getBooleanExtra(EXTRA_OPEN_DAILY_DROP, false);
        binding.getRoot().post(() -> {
            OnboardingHelper.maybeShow(MainActivity.this, null);
            if (pendingOpenDaily) {
                openDailyDrop();
                pendingOpenDaily = false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (removeAdsBilling != null) {
            removeAdsBilling.destroy();
            removeAdsBilling = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.getBooleanExtra(EXTRA_OPEN_DAILY_DROP, false)) {
            openDailyDrop();
        }
    }

    public void openDailyDrop() {
        if (navController != null) {
            navController.navigate(R.id.navigation_random);
        }
        binding.getRoot().postDelayed(() -> {
            NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_activity_main);
            if (host == null) return;
            for (androidx.fragment.app.Fragment f : host.getChildFragmentManager().getFragments()) {
                if (f instanceof RandomFragment) {
                    ((RandomFragment) f).focusDailyDrop();
                    break;
                }
            }
        }, 200);
    }

    /** Overflow menu (About / Invite / Redeem / Copy links) — Home info button. */
    public void showAppMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        MenuItem dailyLine = popup.getMenu().findItem(R.id.daily_line_reminders);
        if (dailyLine != null) {
            boolean on = new DailyLinePrefs(this).isEnabled();
            dailyLine.setTitle(on ? R.string.daily_line_reminders_on : R.string.daily_line_reminders_off);
        }
        MenuItem removeAds = popup.getMenu().findItem(R.id.remove_ads);
        if (removeAds != null) {
            CitaPlus plus = new CitaPlus(this);
            if (plus.adsRemoved()) {
                removeAds.setTitle(R.string.ads_already_removed);
            } else if (!RemoveAdsBilling.isEnabled()) {
                removeAds.setTitle(R.string.remove_ads_coming_soon);
            }
        }
        popup.setOnMenuItemClickListener(this::onAppMenuItem);
        popup.show();
    }

    private boolean onAppMenuItem(MenuItem item) {
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
        if (id == R.id.copy_play_link) {
            copyText(ShareLinks.contentPlayUrl("in_app_menu"), R.string.play_link_copied);
            return true;
        }
        if (id == R.id.copy_invite_code) {
            ReferralStore referral = new ReferralStore(this);
            copyText(referral.myCode(), R.string.invite_code_copied);
            return true;
        }
        if (id == R.id.daily_line_reminders) {
            toggleDailyLineReminders();
            return true;
        }
        if (id == R.id.remove_ads) {
            onRemoveAdsClicked();
            return true;
        }
        return false;
    }

    private void toggleDailyLineReminders() {
        DailyLinePrefs prefs = new DailyLinePrefs(this);
        boolean next = !prefs.isEnabled();
        if (next && Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 4401);
            }
        }
        DailyLineScheduler.apply(this, next);
        Toast.makeText(this,
                next ? R.string.daily_line_reminders_enabled : R.string.daily_line_reminders_disabled,
                Toast.LENGTH_SHORT).show();
    }

    private void onRemoveAdsClicked() {
        CitaPlus plus = new CitaPlus(this);
        if (plus.adsRemoved()) {
            Toast.makeText(this, R.string.ads_already_removed, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!RemoveAdsBilling.isEnabled()) {
            Toast.makeText(this, R.string.remove_ads_coming_soon, Toast.LENGTH_LONG).show();
            return;
        }
        if (removeAdsBilling == null) {
            removeAdsBilling = new RemoveAdsBilling(this);
        }
        removeAdsBilling.start(new RemoveAdsBilling.Listener() {
            @Override
            public void onReady(boolean available) {
                if (available) {
                    removeAdsBilling.launchPurchase(MainActivity.this);
                } else {
                    Toast.makeText(MainActivity.this, R.string.remove_ads_unavailable,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPurchased() {
                Toast.makeText(MainActivity.this, R.string.remove_ads_thanks, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, R.string.remove_ads_unavailable,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void copyText(String text, int toastRes) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) return;
        clipboard.setPrimaryClip(ClipData.newPlainText("Cita", text));
        Toast.makeText(this, toastRes, Toast.LENGTH_SHORT).show();
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
        MaterialButton copyInvite = dialogView.findViewById(R.id.copyInvite);
        MaterialButton copyPlay = dialogView.findViewById(R.id.copyPlayLink);
        TextView cita = dialogView.findViewById(R.id.cita);

        AlertDialog alertDialog = dialogBuilder.create();

        if (!AdsPolicy.shouldShowAds(this)) {
            showAd.setVisibility(View.GONE);
        } else {
            showAd.setOnClickListener(v -> {
                alertDialog.dismiss();
                attemptToShowAd();
            });
        }
        if (copyInvite != null) {
            copyInvite.setOnClickListener(v -> {
                ReferralStore referral = new ReferralStore(this);
                copyText(referral.myCode() + "\n" + ShareLinks.invitePlayUrl(referral.myCode()),
                        R.string.invite_code_copied);
            });
        }
        if (copyPlay != null) {
            copyPlay.setOnClickListener(v ->
                    copyText(ShareLinks.contentPlayUrl("about_dialog"), R.string.play_link_copied));
        }
        cita.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("https://github.com/Ngara-K/Cita"))));

        alertDialog.show();
    }

    private void attemptToShowAd() {
        Toast.makeText(this, R.string.loading_ad, Toast.LENGTH_SHORT).show();
        new SupportAd().loadAd(this, this, null);
    }
}
