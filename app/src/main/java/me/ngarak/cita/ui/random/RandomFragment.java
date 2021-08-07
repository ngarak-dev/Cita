package me.ngarak.cita.ui.random;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.R;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.FragmentRandomBinding;
import me.ngarak.cita.databinding.LayoutBottomSheetBinding;
import me.ngarak.cita.models.QuoteResponse;

public class RandomFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    AdRequest adRequest = new AdRequest.Builder().build();
    private FragmentRandomBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    private RewardedInterstitialAd interstitialAd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRandomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showRefreshing();

        settingUpAdapter();
//        new SupportAd(adRequest).showAd(requireContext());
        loadSmartAd();

        randomQuotes();

        binding.refreshingLayout.setOnRefreshListener(() -> {
            if (quotesRVAdapter.getQuoteList() != null) {
                quotesRVAdapter.getQuoteList().clear();
                quotesRVAdapter.notifyDataSetChanged();
            }
            settingUpAdapter();
            randomQuotes();
            loadSmartAd();
        });

        binding.layoutError.reloadPage.setOnClickListener(v -> {

            showRefreshing();

            if (quotesRVAdapter.getQuoteList() != null) {
                quotesRVAdapter.getQuoteList().clear();
                quotesRVAdapter.notifyDataSetChanged();
            }
            settingUpAdapter();
            randomQuotes();
            loadSmartAd();
        });
    }

    private void settingUpAdapter() {
        binding.randomRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter(this::openBottomSheet);

        binding.randomRv.setAdapter(quotesRVAdapter);
    }

    private void openBottomSheet(QuoteResponse quoteResponse) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        LayoutBottomSheetBinding binding = LayoutBottomSheetBinding.inflate(LayoutInflater.from(requireContext()));
        bottomSheetDialog.setContentView(binding.getRoot());

        binding.setQuote(quoteResponse);
        bottomSheetDialog.show();

        /*show ad*/
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                /*dismiss view on AdLoadError*/
                loadAdError.getResponseInfo();
                binding.adView.setVisibility(View.GONE);
            }
        });

        binding.copyBtn.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(requireContext().getString(R.string.app_name), quoteResponse.getQuote());
            clipboard.setPrimaryClip(clip);

            binding.copyBtn.setIconTint(ColorStateList.valueOf(Color.GREEN));
            Toast.makeText(requireContext(), "Quote Copied", Toast.LENGTH_SHORT).show();
        });

//        /*show ad*/
//        bottomSheetDialog.setOnDismissListener(dialog -> {
//            new SupportAd(adRequest).showInterstitial(requireActivity(), requireContext());
//        });
    }

    private void loadSmartAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                /*dismiss view on AdLoadError*/
                loadAdError.getResponseInfo();
                binding.adView.setVisibility(View.GONE);
            }
        });
    }

    private void randomQuotes() {
        binding.layoutError.getRoot().setVisibility(View.GONE);

        new RandomViewModel().getQuote().observe(getViewLifecycleOwner(), quoteResponses -> {

            boolean isErrorCode = false, isThrowable = false;

            for (QuoteResponse quoteResponse : quoteResponses) {
                if (quoteResponse.getError_code() >= 300) {
                    Log.e(TAG, "randomQuotes: Error" );
                    isErrorCode = true;
                }

                if (quoteResponse.getThrowable() != null) {
                    Log.e(TAG, "randomQuotes: Throwable" );
                    isThrowable = true;
                }
            }

            if (isErrorCode) {
                //return Error
                binding.layoutError.getRoot().setVisibility(View.VISIBLE);
                binding.randomRv.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
            else if (isThrowable) {
                //return throwable
                binding.layoutError.getRoot().setVisibility(View.VISIBLE);
                binding.randomRv.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
            else if (quoteResponses == null) {
                binding.layoutNoQuotes.getRoot().setVisibility(View.VISIBLE);
                binding.randomRv.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
            else {
                //return quotes
                Log.d(TAG, "randomQuotes() returned: " + quoteResponses.size());
                quotesRVAdapter.setQuoteList(quoteResponses);
                binding.progressBar.setVisibility(View.GONE);
                binding.randomRv.setVisibility(View.VISIBLE);

                binding.layoutError.getRoot().setVisibility(View.GONE);
            }

            dismissRefreshing();
        });
    }

    private void showRefreshing() {
        if (!binding.refreshingLayout.isRefreshing()) {
            binding.refreshingLayout.setRefreshing(true);
        }
    }

    private void dismissRefreshing() {
        if (binding.refreshingLayout.isRefreshing()) {
            binding.refreshingLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}