package me.ngarak.cita.ads;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import me.ngarak.cita.R;

public class SupportAd {
    private final String TAG = getClass().getSimpleName();
    AdRequest adRequest;
    private RewardedInterstitialAd interstitialAd;

    public SupportAd(AdRequest adRequest) {
        this.adRequest = adRequest;
    }

    public void showAd(Context context) {
        new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("D053C585B4444ACDE1198E4AFFE3334E"));

        RewardedInterstitialAd.load(context, context.getString(R.string.AFTER_QUOTE_VIEW), adRequest, new RewardedInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedInterstitialAd mInterstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                interstitialAd = mInterstitialAd;

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
//                        loadAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NotNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        interstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                interstitialAd = null;
            }
        });
    }

    public void showInterstitial(FragmentActivity fragmentActivity, Context context) {
        if (interstitialAd != null) {
            interstitialAd.show(fragmentActivity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull @NotNull RewardItem rewardItem) {
                    Toast.makeText(context, "Thank you for using the app", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d(TAG, "showInterstitial: Failed to load Ad");
//            Toast.makeText(context, "Failed to load Ad", Toast.LENGTH_SHORT).show();
        }
    }
}
