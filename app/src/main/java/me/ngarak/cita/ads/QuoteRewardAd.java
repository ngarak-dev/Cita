package me.ngarak.cita.ads;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.R;

public class QuoteRewardAd {

    public interface Listener {
        /** Called when the user earns a reward (reward path). */
        void onRewardEarned(int amount);

        /**
         * Called after the ad is dismissed.
         * @param earnedReward true if a reward was granted during this ad session
         */
        void onAdDismissed(boolean earnedReward);

        /** Called when the ad failed to load or show. */
        void onAdFailed();
    }

    private final String TAG = getClass().getSimpleName();
    private final AdRequest adRequest = new AdRequest.Builder().build();
    private RewardedInterstitialAd rewardAd;
    private boolean rewarded;
    private SharedPreferences pref;
    @Nullable
    private Listener listener;

    public void loadAd(Context context, Activity activity, boolean reward, @NonNull Listener listener) {
        this.listener = listener;
        this.rewarded = false;
        pref = context.getSharedPreferences("quote_views", Context.MODE_PRIVATE);

        RewardedInterstitialAd.load(context, context.getString(R.string.AFTER_QUOTE_VIEW), adRequest,
                new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd mInterstitialAd) {
                        rewardAd = mInterstitialAd;
                        rewardAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "The ad was dismissed.");
                                Toast.makeText(context, R.string.thank_you_support, Toast.LENGTH_SHORT).show();
                                if (QuoteRewardAd.this.listener != null) {
                                    QuoteRewardAd.this.listener.onAdDismissed(rewarded);
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NotNull AdError adError) {
                                Log.d(TAG, "The ad failed to show.");
                                rewardAd = null;
                                notifyFailed(context);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                rewardAd = null;
                                Log.d(TAG, "The ad was shown.");
                            }
                        });

                        if (reward) {
                            showRewardedAd(activity);
                        } else {
                            showAd(activity);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.w(TAG, "rewarded failed code=" + loadAdError.getCode()
                                + " msg=" + loadAdError.getMessage());
                        rewardAd = null;
                        notifyFailed(context);
                    }
                });
    }

    private void notifyFailed(Context context) {
        Toast.makeText(context, R.string.ad_failed_try_later, Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onAdFailed();
        }
    }

    public void showAd(Activity activity) {
        if (rewardAd != null) {
            rewardAd.show(activity, rewardItem -> {
                // Non-reward path: acknowledge completion via dismiss callback only
            });
        } else {
            Toast.makeText(activity, R.string.ad_failed_to_load, Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onAdFailed();
            }
        }
    }

    public void showRewardedAd(Activity activity) {
        if (rewardAd != null) {
            rewardAd.show(activity, rewardItem -> {
                rewarded = true;
                int next = Math.max(0, pref.getInt("quote_views", 0)) + rewardItem.getAmount();
                pref.edit().putInt("quote_views", next).apply();

                Toast.makeText(activity,
                        activity.getString(R.string.reward_added, rewardItem.getAmount()),
                        Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onRewardEarned(rewardItem.getAmount());
                }
            });
        } else {
            Toast.makeText(activity, R.string.ad_failed_to_load, Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onAdFailed();
            }
        }
    }
}
