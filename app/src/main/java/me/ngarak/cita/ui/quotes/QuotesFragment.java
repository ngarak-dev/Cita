package me.ngarak.cita.ui.quotes;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.R;
import me.ngarak.cita.adapters.AutoScroll;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.ads.QuoteRewardAd;
import me.ngarak.cita.databinding.FragmentQuotesBinding;
import me.ngarak.cita.databinding.LayoutBgBottomSheetBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.perm;
import me.ngarak.layout_image.ActionListeners;
import me.ngarak.layout_image.ViewToImage;

public class QuotesFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private final int currentPage = 1;
    RecyclerView quotesRecyclerView;
    private FragmentQuotesBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    private int maxPages = 200;
    private int onErrorPage;

    private SharedPreferences preferences;
    private BottomSheetDialog bottomSheetDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentQuotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = requireContext().getSharedPreferences("quote_views", Context.MODE_PRIVATE);

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
        bottomSheetDialog = new BottomSheetDialog(requireContext());
//        LayoutBottomSheetBinding binding = LayoutBottomSheetBinding.inflate(LayoutInflater.from(requireContext()));
        LayoutBgBottomSheetBinding bg_binding = LayoutBgBottomSheetBinding.inflate(LayoutInflater.from(requireContext()));
        bottomSheetDialog.setContentView(bg_binding.getRoot());

        bg_binding.setQuote(quoteResponse);
        bottomSheetDialog.show();

        /*show ad*/
        AdRequest adRequest = new AdRequest.Builder().build();
        bg_binding.adView.loadAd(adRequest);
        bg_binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                loadAdError.getResponseInfo();
            }
        });

        bg_binding.saveQuoteBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "REWARD: " + preferences.getInt("quote_views", 0) );
                    if (preferences.getInt("quote_views", 0) > 0) {
                        saveLayout(bg_binding);
                    }
                    else {
                        showDialog(bg_binding);
                    }
                }
                else {
                    new perm().reQuestStorage(requireActivity());
                }
            }
        });
    }

    private void showDialog(LayoutBgBottomSheetBinding bg_binding) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_ad_consent, null);
        dialogBuilder.setView(dialogView);

        MaterialButton showAd = dialogView.findViewById(R.id.showAd);
        MaterialButton getReward = dialogView.findViewById(R.id.getReward);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        showAd.setOnClickListener(v -> {
            showAdFirst(false, bg_binding);
        });

        getReward.setOnClickListener(v -> {
            showAdFirst(true, bg_binding);
        });
    }

    private void showAdFirst(boolean reward, LayoutBgBottomSheetBinding bg_binding) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        Log.d(TAG, "Show Ad");
        new QuoteRewardAd().loadAd(requireContext(), progressDialog, requireActivity(), reward);

        if (reward) {
            new Handler().postDelayed(() -> {
                if (new QuoteRewardAd().isRewarded()) {
                    /*after reward save layout*/
                    saveLayout(bg_binding);
                }
            }, 3000);
        }
        else {
            saveLayout(bg_binding);
        }
    }

    private void saveLayout (LayoutBgBottomSheetBinding bg_binding) {
        new ViewToImage(requireContext(), bg_binding.toBeConverted, new ActionListeners() {
            @Override
            public void convertedWithSuccess(Bitmap bitmap, String filePath, String absolutePath) {
                Toast.makeText(requireContext(), "Quote saved " + filePath, Toast.LENGTH_SHORT).show();

                preferences.edit().putInt("quote_views", preferences.getInt("quote_views", 0) - 1).apply();
                bottomSheetDialog.dismiss();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(absolutePath));
                startActivity(Intent.createChooser(intent, "Share Quote"));
            }

            @Override
            public void convertedWithError(String error) {
                Toast.makeText(requireContext(), "Error :" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSmartAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                loadAdError.getResponseInfo();
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