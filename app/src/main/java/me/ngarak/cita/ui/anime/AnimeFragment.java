package me.ngarak.cita.ui.anime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAnimeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prepareRVAdapter();
        loadSmartAd();
        loadAnime();
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

    private void loadAnime() {
        new AnimeViewModel().getAnime().observe(getViewLifecycleOwner(), animeList -> {
            if (animeList != null && !animeList.isEmpty()) {
                Log.d(TAG, "loadAnime() returned: " + animeList.size());
//                animeRVAdapter.setAnimeList(animeList.subList(0, 100));
                animeRVAdapter.setAnimeList(animeList);

                binding.progressBar.setVisibility(View.GONE);
                binding.animeRv.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}