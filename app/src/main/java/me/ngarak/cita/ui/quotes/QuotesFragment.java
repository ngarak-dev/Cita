package me.ngarak.cita.ui.quotes;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.QuoteAnalytics;
import me.ngarak.cita.QuoteSheetController;
import me.ngarak.cita.R;
import me.ngarak.cita.adapters.AutoScroll;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.FragmentQuotesBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.perm;

public class QuotesFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private FragmentQuotesBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    private int maxPages = 200;
    private int onErrorPage;
    private QuotesViewModel quotesViewModel;
    private QuoteSheetController sheetController;
    private QuoteAnalytics analytics;
    private String activeQuery = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quotesViewModel = new ViewModelProvider(this).get(QuotesViewModel.class);
        analytics = new QuoteAnalytics(requireContext());
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

        settingUpAdapter();
        loadSmartAd();
        setupSearch();

        binding.quotesRv.addOnScrollListener(new AutoScroll(binding.quotesRv.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView recyclerView) {
                loadPage(page + 1);
            }
        });

        retrieveQuotes(1);

        binding.layoutError.reloadPage.setOnClickListener(v -> {
            if (quotesRVAdapter.getQuoteList() != null) {
                quotesRVAdapter.clear();
            }
            settingUpAdapter();
            retrieveQuotes(1);
            loadSmartAd();
        });

        binding.loadMoreBtn.setOnClickListener(v -> loadPage(onErrorPage));
    }

    private void setupSearch() {
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                activeQuery = s != null ? s.toString().trim() : "";
                maxPages = 200;
                if (quotesRVAdapter != null) {
                    quotesRVAdapter.clear();
                }
                retrieveQuotes(1);
                if (activeQuery.length() >= 2) {
                    final String q = activeQuery;
                    new Thread(() -> analytics.search(q,
                            me.ngarak.cita.QuotesCatalog.get().countSearchQuotes(q)),
                            "cita-search-analytics").start();
                }
            }
        });
    }

    private void loadPage(int page) {
        if (page < maxPages) {
            retrieveQuotes(page);
        } else {
            Toast.makeText(requireContext(), R.string.end_of_quotes, Toast.LENGTH_LONG).show();
        }
    }

    private void settingUpAdapter() {
        binding.quotesRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter(quote ->
                startActivity(me.ngarak.cita.ui.QuoteDetailActivity.intent(requireContext(), quote)));
        binding.quotesRv.setAdapter(quotesRVAdapter);
    }

    private void loadSmartAd() {
        if (!me.ngarak.cita.ads.AdsPolicy.shouldShowAds(requireContext())) {
            binding.adView.setVisibility(View.GONE);
            return;
        }
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

    private void retrieveQuotes(int page) {
        if (page == 1) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.layoutError.getRoot().setVisibility(View.GONE);
            binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);

            quotesViewModel.getQuotes(activeQuery, page).observe(getViewLifecycleOwner(), quoteResponses -> {
                binding.progressBar.setVisibility(View.GONE);
                applyFirstPage(quoteResponses, page);
            });
        } else {
            binding.progressLoadMore.setVisibility(View.VISIBLE);
            quotesViewModel.getQuotes(activeQuery, page).observe(getViewLifecycleOwner(), quoteResponses -> {
                boolean isErrorCode = false, isThrowable = false;
                if (quoteResponses != null) {
                    for (QuoteResponse quoteResponse : quoteResponses) {
                        if (quoteResponse.getError_code() >= 300) {
                            isErrorCode = true;
                            onErrorPage = page;
                        }
                        if (quoteResponse.getThrowable() != null) {
                            isThrowable = true;
                            onErrorPage = page;
                        }
                    }
                }
                if (isErrorCode || isThrowable || quoteResponses == null) {
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                } else {
                    quotesRVAdapter.setQuoteList(quoteResponses);
                    if (quoteResponses.size() < 10) {
                        maxPages = page;
                    }
                    binding.loadMoreBtn.setVisibility(View.GONE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                }
            });
        }
    }

    private void applyFirstPage(java.util.List<QuoteResponse> quoteResponses, int page) {
        boolean isErrorCode = false, isThrowable = false;
        if (quoteResponses != null) {
            for (QuoteResponse quoteResponse : quoteResponses) {
                if (quoteResponse.getError_code() >= 300) isErrorCode = true;
                if (quoteResponse.getThrowable() != null) isThrowable = true;
            }
        }
        if (isErrorCode || isThrowable) {
            binding.layoutError.getRoot().setVisibility(View.VISIBLE);
            binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);
            binding.quotesRv.setVisibility(View.INVISIBLE);
        } else if (quoteResponses == null || quoteResponses.isEmpty()) {
            binding.layoutNoQuotes.getRoot().setVisibility(View.VISIBLE);
            binding.layoutError.getRoot().setVisibility(View.GONE);
            binding.quotesRv.setVisibility(View.INVISIBLE);
        } else {
            quotesRVAdapter.setQuoteList(quoteResponses);
            if (quoteResponses.size() < 10) {
                maxPages = page;
            }
            binding.quotesRv.setVisibility(View.VISIBLE);
            binding.layoutError.getRoot().setVisibility(View.GONE);
            binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
