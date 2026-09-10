package me.ngarak.cita.ui.anime;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.adapters.AnimeRVAdapter;
import me.ngarak.cita.databinding.FragmentAnimeBinding;
import me.ngarak.cita.ui.QuoteByAnimeActivity;

public class AnimeFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private FragmentAnimeBinding binding;
    private AnimeRVAdapter animeRVAdapter;
    private AnimeViewModel animeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnimeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animeViewModel = new ViewModelProvider(this).get(AnimeViewModel.class);
        prepareRVAdapter();
        loadSmartAd();
        loadAnime(null);

        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadAnime(s != null ? s.toString().trim() : null);
            }
        });

        binding.layoutError.reloadPage.setOnClickListener(v -> {
            binding.layoutError.getRoot().setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.animeRv.setVisibility(View.INVISIBLE);
            loadAnime(binding.searchInput.getText() != null
                    ? binding.searchInput.getText().toString().trim() : null);
            loadSmartAd();
        });
    }

    private void prepareRVAdapter() {
        binding.animeRv.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.animeRv.setLayoutManager(gridLayoutManager);

        animeRVAdapter = new AnimeRVAdapter(anime -> {
            Intent intent = new Intent(requireContext(), QuoteByAnimeActivity.class);
            intent.putExtra("anime", anime);
            startActivity(intent);
        });
        binding.animeRv.setAdapter(animeRVAdapter);
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

    private void loadAnime(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutError.getRoot().setVisibility(View.GONE);

        animeViewModel.searchAnime(query).observe(getViewLifecycleOwner(), animeList -> {
            binding.progressBar.setVisibility(View.GONE);
            if (animeList != null && !animeList.isEmpty()) {
                animeRVAdapter.setAnimeList(animeList);
                binding.animeRv.setVisibility(View.VISIBLE);
                binding.layoutError.getRoot().setVisibility(View.GONE);
            } else {
                animeRVAdapter.setAnimeList(animeList);
                binding.animeRv.setVisibility(View.INVISIBLE);
                binding.layoutError.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
