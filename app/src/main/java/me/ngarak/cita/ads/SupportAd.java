package me.ngarak.cita.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.R;

public class SupportAd {
    private final String TAG = getClass().getSimpleName();
    private final AdRequest adRequest = new AdRequest.Builder().build();
    private InterstitialAd interstitialAd;

    public interface Listener {
        void onFinished();
    }

    public void loadAd(Context context, Activity activity, @Nullable Listener listener) {
        if (!AdsPolicy.shouldShowAds(context)) {
            Toast.makeText(context, R.string.ads_already_removed, Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onFinished();
            return;
        }
        InterstitialAd.load(context, context.getString(R.string.SUPPORT_AD_UNIT), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd mInterstitialAd) {
                interstitialAd = mInterstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d(TAG, "The ad was dismissed.");
                        Toast.makeText(context, R.string.thank_you_support, Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onFinished();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NotNull AdError adError) {
                        Log.d(TAG, "The ad failed to show.");
                        if (listener != null) listener.onFinished();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        interstitialAd = null;
                        Log.d(TAG, "The ad was shown.");
                    }
                });
                showInterstitial(activity, listener);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.w(TAG, "interstitial failed code=" + loadAdError.getCode()
                        + " msg=" + loadAdError.getMessage());
                interstitialAd = null;
                Toast.makeText(context, R.string.ad_failed_try_later, Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onFinished();
            }
        });
    }

    public void showInterstitial(Activity activity, @Nullable Listener listener) {
        if (interstitialAd != null) {
            interstitialAd.show(activity);
        } else {
            Toast.makeText(activity, R.string.ad_failed_to_load, Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onFinished();
        }
    }
}
