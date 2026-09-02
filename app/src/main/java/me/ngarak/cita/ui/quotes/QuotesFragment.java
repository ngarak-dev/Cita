package me.ngarak.cita.ui.quotes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
    private FragmentQuotesBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    private int maxPages = 200;
    private int onErrorPage;

    private SharedPreferences preferences;
    private BottomSheetDialog bottomSheetDialog;
    private QuoteRewardAd quoteRewardAd;
    private AlertDialog consentDialog;
    private QuotesViewModel quotesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentQuotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = requireContext().getSharedPreferences("quote_views", Context.MODE_PRIVATE);
        quoteRewardAd = new QuoteRewardAd();
        quotesViewModel = new ViewModelProvider(this).get(QuotesViewModel.class);

        settingUpAdapter();

        loadSmartAd();

        binding.quotesRv.addOnScrollListener(new AutoScroll(binding.quotesRv.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView recyclerView) {
                loadPage(page + 1);
            }
        });

        retrieveQuotes(currentPage);

        binding.layoutError.reloadPage.setOnClickListener(v -> {
            if (quotesRVAdapter.getQuoteList() != null) {
                quotesRVAdapter.clear();
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
            Toast.makeText(requireContext(), R.string.end_of_quotes, Toast.LENGTH_LONG).show();
        }
    }

    private void settingUpAdapter() {
        binding.quotesRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter(this::openBottomSheet);

        binding.quotesRv.setAdapter(quotesRVAdapter);
    }

    private void openBottomSheet(QuoteResponse quoteResponse) {
        bottomSheetDialog = new BottomSheetDialog(requireContext());
        LayoutBgBottomSheetBinding bg_binding = LayoutBgBottomSheetBinding.inflate(LayoutInflater.from(requireContext()));
        bottomSheetDialog.setContentView(bg_binding.getRoot());

        bg_binding.setQuote(quoteResponse);
        bottomSheetDialog.show();

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
            perm storagePerm = new perm();
            boolean canWrite = !storagePerm.needsStoragePermission()
                    || requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (canWrite) {
                Log.e(TAG, "REWARD: " + preferences.getInt("quote_views", 0));
                if (preferences.getInt("quote_views", 0) > 0) {
                    saveLayout(bg_binding);
                } else {
                    showDialog(bg_binding);
                }
            } else {
                storagePerm.reQuestStorage(requireActivity());
            }
        });
    }

    private void showDialog(LayoutBgBottomSheetBinding bg_binding) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_ad_consent, null);
        dialogBuilder.setTitle(R.string.save_options_title);
        dialogBuilder.setView(dialogView);

        MaterialButton showAd = dialogView.findViewById(R.id.showAd);
        MaterialButton getReward = dialogView.findViewById(R.id.getReward);

        consentDialog = dialogBuilder.create();
        consentDialog.show();

        showAd.setOnClickListener(v -> showAdFirst(false, bg_binding));
        getReward.setOnClickListener(v -> showAdFirst(true, bg_binding));
    }

    private void showAdFirst(boolean reward, LayoutBgBottomSheetBinding bg_binding) {
        if (consentDialog != null && consentDialog.isShowing()) {
            consentDialog.dismiss();
        }

        Toast.makeText(requireContext(), R.string.loading_ad, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Show Ad");

        quoteRewardAd.loadAd(requireContext(), requireActivity(), reward, new QuoteRewardAd.Listener() {
            @Override
            public void onRewardEarned(int amount) {
                // Prefs updated in QuoteRewardAd; save after dismiss
            }

            @Override
            public void onAdDismissed(boolean earnedReward) {
                if (!isAdded()) {
                    return;
                }
                if (reward) {
                    if (earnedReward) {
                        saveLayout(bg_binding);
                    }
                } else {
                    saveLayout(bg_binding);
                }
            }

            @Override
            public void onAdFailed() {
                // Toast already shown by QuoteRewardAd
            }
        });
    }

    private void saveLayout(LayoutBgBottomSheetBinding bg_binding) {
        new ViewToImage(requireContext(), bg_binding.toBeConverted, new ActionListeners() {
            @Override
            public void convertedWithSuccess(Bitmap bitmap, String filePath, String absolutePath) {
                Toast.makeText(requireContext(), getString(R.string.quote_saved, filePath), Toast.LENGTH_SHORT).show();

                preferences.edit().putInt("quote_views", preferences.getInt("quote_views", 0) - 1).apply();
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(absolutePath));
                startActivity(Intent.createChooser(intent, "Share Quote"));
            }

            @Override
            public void convertedWithError(String error) {
                Toast.makeText(requireContext(), getString(R.string.error_prefix, error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSmartAd() {
        binding.adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i(TAG, "banner loaded");
                binding.adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                // 3 = ERROR_CODE_NO_FILL (no inventory) — not a crash / bad App ID
                Log.w(TAG, "banner failed code=" + loadAdError.getCode()
                        + " msg=" + loadAdError.getMessage());
                binding.adView.setVisibility(View.GONE);
            }
        });
    }

    private void retrieveQuotes(int page) {
        Log.d(TAG, "retrieveQuotes() called with: page = [" + page + "]");
        if (page == 1) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.layoutError.getRoot().setVisibility(View.GONE);
            binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);

            quotesViewModel.getQuotes(page).observe(getViewLifecycleOwner(), quoteResponses -> {

                boolean isErrorCode = false, isThrowable = false;

                if (quoteResponses != null) {
                    for (QuoteResponse quoteResponse : quoteResponses) {
                        if (quoteResponse.getError_code() >= 300) {
                            Log.e(TAG, "randomQuotes: Error");
                            isErrorCode = true;
                        }

                        if (quoteResponse.getThrowable() != null) {
                            Log.e(TAG, "randomQuotes: Throwable");
                            isThrowable = true;
                        }
                    }
                }

                if (isErrorCode || isThrowable) {
                    binding.layoutError.getRoot().setVisibility(View.VISIBLE);
                    binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                } else if (quoteResponses == null || quoteResponses.isEmpty()) {
                    binding.layoutNoQuotes.getRoot().setVisibility(View.VISIBLE);
                    binding.layoutError.getRoot().setVisibility(View.GONE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "randomQuotes() returned: " + quoteResponses.size());
                    quotesRVAdapter.setQuoteList(quoteResponses);

                    if (quoteResponses.size() < 10) {
                        maxPages = page;
                    }

                    binding.progressBar.setVisibility(View.GONE);
                    binding.quotesRv.setVisibility(View.VISIBLE);
                    binding.layoutError.getRoot().setVisibility(View.GONE);
                    binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);
                }
            });
        } else {
            binding.progressLoadMore.setVisibility(View.VISIBLE);

            quotesViewModel.getQuotes(page).observe(getViewLifecycleOwner(), quoteResponses -> {

                boolean isErrorCode = false, isThrowable = false;

                if (quoteResponses != null) {
                    for (QuoteResponse quoteResponse : quoteResponses) {
                        if (quoteResponse.getError_code() >= 300) {
                            Log.e(TAG, "randomQuotes: Error");
                            isErrorCode = true;
                            onErrorPage = page;
                        }

                        if (quoteResponse.getThrowable() != null) {
                            Log.e(TAG, "randomQuotes: Throwable");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
