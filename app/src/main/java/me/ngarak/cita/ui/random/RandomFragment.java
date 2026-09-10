package me.ngarak.cita.ui.random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.QuoteSheetController;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.FragmentRandomBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.perm;

public class RandomFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private FragmentRandomBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    private RandomViewModel randomViewModel;
    private QuoteSheetController sheetController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRandomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        randomViewModel = new ViewModelProvider(this).get(RandomViewModel.class);
        sheetController = new QuoteSheetController(new QuoteSheetController.Host() {
            @NonNull
            @Override
            public Activity activity() {
                return requireActivity();
            }

            @Override
            public boolean isActive() {
                return isAdded();
            }

            @Override
            public void requestStoragePermission() {
                new perm().reQuestStorage(requireActivity());
            }
        });

        showRefreshing();
        settingUpAdapter();
        loadSmartAd();
        randomQuotes();

        binding.refreshingLayout.setOnRefreshListener(() -> {
            if (quotesRVAdapter.getQuoteList() != null) {
                quotesRVAdapter.clear();
            }
            settingUpAdapter();
            randomQuotes();
            loadSmartAd();
        });

        binding.layoutError.reloadPage.setOnClickListener(v -> {
            showRefreshing();
            if (quotesRVAdapter.getQuoteList() != null) {
                quotesRVAdapter.clear();
            }
            settingUpAdapter();
            randomQuotes();
            loadSmartAd();
        });
    }

    private void settingUpAdapter() {
        binding.randomRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter(quote -> sheetController.open(quote));
        binding.randomRv.setAdapter(quotesRVAdapter);
    }

    private void loadSmartAd() {
        binding.adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                binding.adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                Log.w(TAG, "banner failed code=" + loadAdError.getCode()
                        + " msg=" + loadAdError.getMessage());
                binding.adView.setVisibility(View.GONE);
            }
        });
    }

    private void randomQuotes() {
        binding.layoutError.getRoot().setVisibility(View.GONE);
        binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        randomViewModel.getQuote().observe(getViewLifecycleOwner(), quoteResponses -> {
            boolean isErrorCode = false, isThrowable = false;
            if (quoteResponses != null) {
                for (QuoteResponse quoteResponse : quoteResponses) {
                    if (quoteResponse.getError_code() >= 300) isErrorCode = true;
                    if (quoteResponse.getThrowable() != null) isThrowable = true;
                }
            }

            binding.progressBar.setVisibility(View.GONE);
            if (isErrorCode || isThrowable) {
                binding.layoutError.getRoot().setVisibility(View.VISIBLE);
                binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);
                binding.randomRv.setVisibility(View.INVISIBLE);
            } else if (quoteResponses == null || quoteResponses.isEmpty()) {
                binding.layoutNoQuotes.getRoot().setVisibility(View.VISIBLE);
                binding.layoutError.getRoot().setVisibility(View.GONE);
                binding.randomRv.setVisibility(View.INVISIBLE);
            } else {
                quotesRVAdapter.setQuoteList(quoteResponses);
                binding.randomRv.setVisibility(View.VISIBLE);
                binding.layoutError.getRoot().setVisibility(View.GONE);
                binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);
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
