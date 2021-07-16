package me.ngarak.cita.ui.anime;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.adapters.AnimeRVAdapter;
import me.ngarak.cita.databinding.FragmentAnimeBinding;

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

        loadAnime();
    }

    private void prepareRVAdapter() {
        binding.animeRv.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.animeRv.setLayoutManager(gridLayoutManager);

        animeRVAdapter = new AnimeRVAdapter();
        binding.animeRv.setAdapter(animeRVAdapter);
    }

    private void loadAnime() {
        new AnimeViewModel().getAnime().observe(getViewLifecycleOwner(), animeList -> {
            if (animeList != null && !animeList.isEmpty()) {
                Log.d(TAG, "loadAnime() returned: " + animeList.size());
                animeRVAdapter.setAnimeList(animeList.subList(0, 100));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}