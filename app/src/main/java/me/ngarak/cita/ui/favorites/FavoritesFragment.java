package me.ngarak.cita.ui.favorites;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;

import java.util.List;

import me.ngarak.cita.CollectionsStore;
import me.ngarak.cita.FavoritesStore;
import me.ngarak.cita.R;
import me.ngarak.cita.WeeklyShareTracker;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.FragmentFavoritesBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.ui.QuoteDetailActivity;

public class FavoritesFragment extends Fragment {

    private static final String FILTER_ALL = "__all__";

    private FragmentFavoritesBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    private FavoritesStore favoritesStore;
    private CollectionsStore collectionsStore;
    private WeeklyShareTracker wsc;
    private String activeFilter = FILTER_ALL;

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
        collectionsStore = new CollectionsStore(requireContext());
        wsc = new WeeklyShareTracker(requireContext());

        binding.favoritesRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter(quote ->
                startActivity(QuoteDetailActivity.intent(requireContext(), quote)));
        binding.favoritesRv.setAdapter(quotesRVAdapter);

        binding.btnCreateCollection.setOnClickListener(v -> showCreateDialog());
        setupCollectionChips();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupCollectionChips();
        refresh();
        updateWscHint();
    }

    private void updateWscHint() {
        if (binding == null) return;
        int shares = wsc.getSharesThisWeek();
        int streak = wsc.getStreakWeeks();
        if (shares <= 0 && streak <= 0) {
            binding.wscHint.setVisibility(View.GONE);
            return;
        }
        binding.wscHint.setVisibility(View.VISIBLE);
        if (streak > 1) {
            binding.wscHint.setText(getString(R.string.wsc_hint_streak, shares, streak));
        } else {
            binding.wscHint.setText(getString(R.string.wsc_hint, shares));
        }
    }

    private void setupCollectionChips() {
        if (binding == null) return;
        binding.collectionChips.removeAllViews();
        binding.collectionChips.setOnCheckedStateChangeListener(null);

        Chip all = new Chip(requireContext());
        all.setId(View.generateViewId());
        all.setText(R.string.collection_all);
        all.setCheckable(true);
        all.setChecked(FILTER_ALL.equals(activeFilter));
        all.setTag(FILTER_ALL);
        binding.collectionChips.addView(all);

        for (CollectionsStore.Collection c : collectionsStore.getAll()) {
            Chip chip = new Chip(requireContext());
            chip.setId(View.generateViewId());
            int n = c.quoteKeys != null ? c.quoteKeys.size() : 0;
            chip.setText(getString(R.string.collection_chip, c.name, n));
            chip.setCheckable(true);
            chip.setChecked(c.id.equals(activeFilter));
            chip.setTag(c.id);
            chip.setOnLongClickListener(v -> {
                confirmDelete(c);
                return true;
            });
            binding.collectionChips.addView(chip);
        }

        binding.collectionChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            View chipView = group.findViewById(checkedIds.get(0));
            if (chipView == null || !(chipView.getTag() instanceof String)) return;
            activeFilter = (String) chipView.getTag();
            refresh();
        });
    }

    private void showCreateDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setHint(R.string.collection_name_hint);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.create_collection)
                .setView(input)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    CollectionsStore.Collection created = collectionsStore.create(input.getText().toString());
                    if (created == null) {
                        Toast.makeText(requireContext(), R.string.collection_name_invalid, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    activeFilter = created.id;
                    setupCollectionChips();
                    refresh();
                    Toast.makeText(requireContext(), R.string.collection_created, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void confirmDelete(CollectionsStore.Collection c) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_collection)
                .setMessage(getString(R.string.delete_collection_msg, c.name))
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    collectionsStore.delete(c.id);
                    if (c.id.equals(activeFilter)) activeFilter = FILTER_ALL;
                    setupCollectionChips();
                    refresh();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void refresh() {
        if (binding == null) return;
        List<QuoteResponse> all = favoritesStore.getAll();
        List<QuoteResponse> shown;
        if (FILTER_ALL.equals(activeFilter)) {
            shown = all;
            binding.savedCount.setText(getString(R.string.saved_quotes_count, all.size()));
        } else {
            shown = collectionsStore.quotesIn(activeFilter, all);
            CollectionsStore.Collection c = collectionsStore.findById(activeFilter);
            String name = c != null ? c.name : "";
            binding.savedCount.setText(getString(R.string.collection_quotes_count, name, shown.size()));
        }
        quotesRVAdapter.clear();
        if (shown.isEmpty()) {
            binding.emptyFavorites.setVisibility(View.VISIBLE);
            binding.favoritesRv.setVisibility(View.GONE);
            if (!FILTER_ALL.equals(activeFilter)) {
                binding.emptyFavorites.setText(R.string.collection_empty);
                binding.emptyFavorites.setOnClickListener(null);
            } else {
                binding.emptyFavorites.setText(getString(R.string.no_favorites_yet)
                        + "\n\n" + getString(R.string.empty_favorites_cta));
                binding.emptyFavorites.setOnClickListener(v -> {
                    if (requireActivity() instanceof me.ngarak.cita.ui.MainActivity) {
                        ((me.ngarak.cita.ui.MainActivity) requireActivity()).openDailyDrop();
                    }
                });
            }
        } else {
            binding.emptyFavorites.setVisibility(View.GONE);
            binding.favoritesRv.setVisibility(View.VISIBLE);
            quotesRVAdapter.setQuoteList(shown);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
