package me.ngarak.cita.ui.random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.chip.Chip;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.DailyDrop;
import me.ngarak.cita.FavoritesStore;
import me.ngarak.cita.Mood;
import me.ngarak.cita.QuoteAnalytics;
import me.ngarak.cita.QuoteSheetController;
import me.ngarak.cita.R;
import me.ngarak.cita.TasteModel;
import me.ngarak.cita.UserTaste;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.FragmentRandomBinding;
import me.ngarak.cita.databinding.LayoutDailyDropHeroBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.perm;
import me.ngarak.cita.ui.MainActivity;
import me.ngarak.cita.ui.QuoteDetailActivity;

public class RandomFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private FragmentRandomBinding binding;
    private LayoutDailyDropHeroBinding dailyBinding;
    private QuotesRVAdapter quotesRVAdapter;
    private RandomViewModel randomViewModel;
    private QuoteSheetController sheetController;
    private FavoritesStore favorites;
    private TasteModel tasteModel;
    private UserTaste taste;
    private QuoteAnalytics analytics;
    private Mood activeMood = Mood.ALL;
    private QuoteResponse dailyQuote;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRandomBinding.inflate(inflater, container, false);
        dailyBinding = binding.dailyDrop;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taste = new UserTaste(requireContext());
        tasteModel = new TasteModel(requireContext());
        favorites = new FavoritesStore(requireContext());
        analytics = new QuoteAnalytics(requireContext());
        activeMood = taste.lastMood();
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

        binding.btnMenu.setOnClickListener(v -> {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).showAppMenu(v);
            }
        });

        setupMoodChips();
        bindDailyDrop();
        showRefreshing();
        settingUpAdapter();
        loadSmartAd();
        randomQuotes();

        binding.refreshingLayout.setOnRefreshListener(() -> {
            if (quotesRVAdapter.getQuoteList() != null) {
                quotesRVAdapter.clear();
            }
            settingUpAdapter();
            bindDailyDrop();
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

    private void setupMoodChips() {
        binding.moodChips.removeAllViews();
        for (Mood mood : Mood.values()) {
            Chip chip = new Chip(requireContext());
            chip.setId(View.generateViewId());
            chip.setText(mood.titleRes);
            chip.setCheckable(true);
            chip.setChecked(mood == activeMood);
            chip.setTag(mood);
            binding.moodChips.addView(chip);
        }
        binding.moodChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            View chipView = group.findViewById(checkedIds.get(0));
            if (chipView == null || !(chipView.getTag() instanceof Mood)) return;
            activeMood = (Mood) chipView.getTag();
            taste.setMood(activeMood);
            analytics.moodSelected(activeMood.name());
            bindDailyDrop();
            if (quotesRVAdapter != null) quotesRVAdapter.clear();
            randomQuotes();
        });
    }

    private void bindDailyDrop() {
        dailyQuote = DailyDrop.today(requireContext(),
                activeMood == Mood.ALL ? Mood.ALL : activeMood, taste.favoriteAnime());
        if (dailyQuote == null) {
            dailyBinding.getRoot().setVisibility(View.GONE);
            return;
        }
        dailyBinding.getRoot().setVisibility(View.VISIBLE);
        dailyBinding.setQuote(dailyQuote);
        dailyBinding.dailyDate.setText(getString(R.string.daily_drop_subtitle, DailyDrop.dayLabel()));
        String meta = (dailyQuote.getCharacter() != null ? dailyQuote.getCharacter() : "")
                + (dailyQuote.getAnime() != null ? " · " + dailyQuote.getAnime() : "");
        dailyBinding.dailyMeta.setText(meta);
        updateDailyFavoriteUi(favorites.contains(dailyQuote));

        dailyBinding.dailyDropCard.setOnClickListener(v -> {
            analytics.dailyDropOpen();
            startActivity(QuoteDetailActivity.intent(requireContext(), dailyQuote));
        });
        dailyBinding.btnFavorite.setOnClickListener(v -> {
            boolean on = favorites.toggle(dailyQuote);
            updateDailyFavoriteUi(on);
            analytics.favoriteToggle(dailyQuote, on);
            tasteModel.recordFavorite(dailyQuote, on);
            Toast.makeText(requireContext(),
                    on ? R.string.added_to_favorites : R.string.removed_from_favorites,
                    Toast.LENGTH_SHORT).show();
        });
        dailyBinding.btnShare.setOnClickListener(v -> {
            analytics.dailyDropOpen();
            sheetController.open(dailyQuote);
        });
        dailyBinding.btnNewQuote.setOnClickListener(v -> {
            if (quotesRVAdapter != null) quotesRVAdapter.clear();
            settingUpAdapter();
            randomQuotes();
            bindDailyDrop();
        });
    }

    private void updateDailyFavoriteUi(boolean on) {
        dailyBinding.btnFavorite.setText(on ? R.string.favorited : R.string.favorite);
        dailyBinding.btnFavorite.setIconResource(
                on ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    private void settingUpAdapter() {
        binding.randomRv.setHasFixedSize(false);
        quotesRVAdapter = new QuotesRVAdapter(quote ->
                startActivity(QuoteDetailActivity.intent(requireContext(), quote)));
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

        randomViewModel.getQuote(activeMood, taste.favoriteAnime())
                .observe(getViewLifecycleOwner(), quoteResponses -> {
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
        dailyBinding = null;
    }
}
