package me.ngarak.cita.ads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

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

    public void loadAd(Context context, ProgressDialog progressDialog, Activity activity) {
        InterstitialAd.load(context, context.getString(R.string.SUPPORT_AD_UNIT), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd mInterstitialAd) {
                interstitialAd = mInterstitialAd;
                showInterstitial(activity);

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
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
                        interstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.i(TAG, loadAdError.getMessage());
                interstitialAd = null;
                progressDialog.dismiss();

                Toast.makeText(context, "Ad Failed try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showInterstitial (FragmentActivity fragmentActivity) {
        if (interstitialAd != null) {
            interstitialAd.show(fragmentActivity);
        }
        else {
            Toast.makeText(fragmentActivity, "Failed to load Ad", Toast.LENGTH_SHORT).show();
        }
    }

    public void showInterstitial (Activity activity) {
        if (interstitialAd != null) {
            interstitialAd.show(activity);
        }
        else {
            Toast.makeText(activity, "Failed to load Ad", Toast.LENGTH_SHORT).show();
        }
    }
}
