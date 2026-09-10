package me.ngarak.cita.ui.favorites;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import me.ngarak.cita.FavoritesStore;
import me.ngarak.cita.QuoteSheetController;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.FragmentFavoritesBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.perm;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    private FavoritesStore favoritesStore;
    private QuoteSheetController sheetController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favoritesStore = new FavoritesStore(requireContext());
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

        binding.favoritesRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter(quote -> sheetController.open(quote));
        binding.favoritesRv.setAdapter(quotesRVAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        if (binding == null) return;
        List<QuoteResponse> all = favoritesStore.getAll();
        quotesRVAdapter.clear();
        if (all.isEmpty()) {
            binding.emptyFavorites.setVisibility(View.VISIBLE);
            binding.favoritesRv.setVisibility(View.GONE);
        } else {
            binding.emptyFavorites.setVisibility(View.GONE);
            binding.favoritesRv.setVisibility(View.VISIBLE);
            quotesRVAdapter.setQuoteList(all);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
