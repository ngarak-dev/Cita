package me.ngarak.cita.ui.quotes;

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
import me.ngarak.cita.databinding.FragmentQuotesBinding;

public class QuotesFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private FragmentQuotesBinding binding;
    private QuotesRVAdapter quotesRVAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentQuotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingUpAdapter();

        retrieveQuotes();
    }

    private void settingUpAdapter() {
        binding.quotesRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter();

        binding.quotesRv.setAdapter(quotesRVAdapter);
    }

    private void retrieveQuotes() {
        new QuotesViewModel().getQuotes(1).observe(getViewLifecycleOwner(), quoteResponses -> {
            if (quoteResponses != null && !quoteResponses.isEmpty()) {
                Log.d(TAG, "retrieveQuotes() called" + quoteResponses.size());
                quotesRVAdapter.setQuoteList(quoteResponses);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}