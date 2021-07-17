package me.ngarak.cita.ui.random;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.FragmentRandomBinding;

public class RandomFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private FragmentRandomBinding binding;
    private final RandomViewModel randomViewModel = new RandomViewModel();

    private QuotesRVAdapter quotesRVAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRandomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRefreshing();

        settingUpAdapter();

        randomQuotes();

        binding.refreshingLayout.setOnRefreshListener(() -> {
            if (quotesRVAdapter.getQuoteList() != null) {
                quotesRVAdapter.getQuoteList().clear();
                quotesRVAdapter.notifyDataSetChanged();
            }
            settingUpAdapter();
            randomQuotes();
        });
    }

    private void settingUpAdapter() {
        binding.randomRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter();

        binding.randomRv.setAdapter(quotesRVAdapter);
    }

    private void randomQuotes() {
        randomViewModel.getQuote().observe(getViewLifecycleOwner(), quoteResponses -> {
            if (quoteResponses!= null && !quoteResponses.isEmpty()) {
                Log.d(TAG, "randomQuotes() returned: " + quoteResponses.size());
                quotesRVAdapter.setQuoteList(quoteResponses);

                binding.progressBar.setVisibility(View.GONE);
                binding.randomRv.setVisibility(View.VISIBLE);
            }
            stopRefreshing();
        });
    }

    private void setRefreshing () {
        if (!binding.refreshingLayout.isRefreshing()) {
            binding.refreshingLayout.setRefreshing(true);
        }
    }

    private void stopRefreshing () {
        if (binding.refreshingLayout.isRefreshing()) {
            binding.refreshingLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}