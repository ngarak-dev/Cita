package me.ngarak.cita.ui.quotes;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.R;
import me.ngarak.cita.adapters.AutoScroll;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.FragmentQuotesBinding;
import me.ngarak.cita.databinding.LayoutBottomSheetBinding;
import me.ngarak.cita.models.QuoteResponse;

public class QuotesFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private final int currentPage = 1;
    RecyclerView quotesRecyclerView;
    private FragmentQuotesBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    private int maxPages = 200;
    private int onErrorPage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentQuotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quotesRecyclerView = view.findViewById(R.id.random_rv);

        settingUpAdapter();

        loadSmartAd();

        binding.quotesRv.addOnScrollListener(new AutoScroll(binding.quotesRv.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView recyclerView) {
                quotesRecyclerView = recyclerView;
                loadPage(page + 1);
            }
        });

        retrieveQuotes(currentPage);

        binding.layoutError.reloadPage.setOnClickListener(v -> {
            if (quotesRVAdapter.getQuoteList() != null) {
                quotesRVAdapter.getQuoteList().clear();
                quotesRVAdapter.notifyDataSetChanged();
            }
            settingUpAdapter();
            retrieveQuotes(currentPage);
            loadSmartAd();
        });

        binding.loadMoreBtn.setOnClickListener(v -> loadPage(onErrorPage));
    }

    private void loadPage(int page) {
        if (page < maxPages) {
            retrieveQuotes(page);
        } else {
            /*End of Pages*/
            Toast.makeText(requireContext(), "End of Quotes", Toast.LENGTH_LONG).show();
        }
    }

    private void settingUpAdapter() {
        binding.quotesRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter(this::openBottomSheet);

        binding.quotesRv.setAdapter(quotesRVAdapter);
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
    }

    private void loadSmartAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                /*dismiss view on AdLoadError*/
                binding.adView.setVisibility(View.GONE);
            }
        });
    }

    private void retrieveQuotes(int page) {
        Log.d(TAG, "retrieveQuotes() called with: page = [" + page + "]");
        if (page == 1) {
            new QuotesViewModel().getQuotes(page).observe(getViewLifecycleOwner(), quoteResponses -> {

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
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
                else if (isThrowable) {
                    //return throwable
                    binding.layoutError.getRoot().setVisibility(View.VISIBLE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
                else if (quoteResponses == null) {
                    binding.layoutError.getRoot().setVisibility(View.VISIBLE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    //return quotes
                    Log.d(TAG, "randomQuotes() returned: " + quoteResponses.size());
                    quotesRVAdapter.setQuoteList(quoteResponses);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.quotesRv.setVisibility(View.VISIBLE);

                    binding.layoutError.getRoot().setVisibility(View.GONE);
                }
            });
        } else {
            binding.progressLoadMore.setVisibility(View.VISIBLE);

            new QuotesViewModel().getQuotes(page).observe(getViewLifecycleOwner(), quoteResponses -> {

                boolean isErrorCode = false, isThrowable = false;

                for (QuoteResponse quoteResponse : quoteResponses) {
                    if (quoteResponse.getError_code() >= 300) {
                        Log.e(TAG, "randomQuotes: Error" );
                        isErrorCode = true;
                        onErrorPage = page;
                    }

                    if (quoteResponse.getThrowable() != null) {
                        Log.e(TAG, "randomQuotes: Throwable" );
                        isThrowable = true;
                        onErrorPage = page;
                    }
                }

                if (isErrorCode) {
                    //return Error
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                }
                else if (isThrowable) {
                    //return throwable
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                }
                else if (quoteResponses == null) {
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                }
                else {
                    //return quotes
                    quotesRVAdapter.setQuoteList(quoteResponses);

                    /*checking list size then set last page*/
                    if (quoteResponses.size() < 10) {
                        maxPages = page;
                    }
                    binding.loadMoreBtn.setVisibility(View.GONE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}