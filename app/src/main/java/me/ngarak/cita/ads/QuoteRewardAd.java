package me.ngarak.cita.ads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.R;

public class QuoteRewardAd {

    private final String TAG = getClass().getSimpleName();
    private final AdRequest adRequest = new AdRequest.Builder().build();
    private RewardedInterstitialAd rewardAd;
    int quote_views;
    private boolean rewarded;
    private SharedPreferences pref;

    public void loadAd(Context context, ProgressDialog progressDialog, Activity activity, boolean reward) {
        pref = context.getSharedPreferences("quote_views", Context.MODE_PRIVATE);

        RewardedInterstitialAd.load(context, context.getString(R.string.AFTER_QUOTE_VIEW), adRequest, new RewardedInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedInterstitialAd mInterstitialAd) {
                rewardAd = mInterstitialAd;

                if (reward) {
                    showRewardedAd(activity);
                }
                else {
                    showAd(activity);
                }
                rewardAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d(TAG, "The ad was dismissed.");
                        Toast.makeText(context, "Thank you for support", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NotNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't show it a second time.
                        rewardAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.i(TAG, loadAdError.getMessage());
                rewardAd = null;
                progressDialog.dismiss();
                Toast.makeText(context, "Ad Failed try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getQuote_views() {
        return quote_views;
    }

    public void setQuote_views(int quote_views) {
        this.quote_views = quote_views;
    }

    public boolean isRewarded() {
        return rewarded;
    }

    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }

    public void showAd (FragmentActivity fragmentActivity) {
        if (rewardAd != null) {
            rewardAd.show(fragmentActivity, rewardItem -> {
                quote_views = rewardItem.getAmount();
                rewarded = true;
            });
        }
        else {
            Toast.makeText(fragmentActivity, "Failed to load Ad", Toast.LENGTH_SHORT).show();
        }
    }

    public void showRewardedAd (Activity activity) {
        if (rewardAd != null) {
            rewardAd.show(activity, rewardItem -> {
                quote_views = rewardItem.getAmount();
                pref.edit().putInt("quote_views", pref.getInt("quote_views", 0) + rewardItem.getAmount()).apply();
                rewarded = true;

                Toast.makeText(activity, "Added more " + rewardItem + " quotes views" , Toast.LENGTH_SHORT).show();
            });
        }
        else {
            Toast.makeText(activity, "Failed to load Ad", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAd (Activity activity) {
        if (rewardAd != null) {
            rewardAd.show(activity, rewardItem -> {
                quote_views = rewardItem.getAmount();
                rewarded = true;
            });
        }
        else {
            Toast.makeText(activity, "Failed to load Ad", Toast.LENGTH_SHORT).show();
        }
    }
}
