package me.ngarak.cita.ui.quotes;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.R;
import me.ngarak.cita.adapters.AutoScroll;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.FragmentQuotesBinding;

public class QuotesFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private FragmentQuotesBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    RecyclerView quotesRecyclerView;

    private final int currentPage = 1;
    private int maxPages = 200;

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

                if ((page + 1) < maxPages) {
                    retrieveQuotes(page + 1);
                }
                else {
                    /*End of Pages*/
                    Toast.makeText(requireContext(), "End of Quotes", Toast.LENGTH_LONG).show();
                }
            }
        });

        retrieveQuotes(currentPage);
    }

    private void settingUpAdapter() {
        binding.quotesRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter();

        binding.quotesRv.setAdapter(quotesRVAdapter);
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
                if (quoteResponses != null && !quoteResponses.isEmpty()) {
                    quotesRVAdapter.setQuoteList(quoteResponses);

                    binding.progressBar.setVisibility(View.GONE);
                    binding.quotesRv.setVisibility(View.VISIBLE);
                }
            });
        }
        else {
            binding.progressLoadMore.setVisibility(View.VISIBLE);

            new QuotesViewModel().getQuotes(page).observe(getViewLifecycleOwner(), quoteResponses -> {
                if (quoteResponses != null && !quoteResponses.isEmpty()) {
                    quotesRVAdapter.setQuoteList(quoteResponses);

                    /*checking list size then set last page*/
                    if (quoteResponses.size() < 10) {
                        maxPages = page;
                    }

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