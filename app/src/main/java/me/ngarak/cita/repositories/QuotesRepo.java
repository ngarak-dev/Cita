package me.ngarak.cita.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.ngarak.cita.QuotesCatalog;
import me.ngarak.cita.models.QuoteResponse;

public class QuotesRepo {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public MutableLiveData<List<QuoteResponse>> getQuotes(int page) {
        MutableLiveData<List<QuoteResponse>> mutableLiveData = new MutableLiveData<>();
        EXECUTOR.execute(() -> {
            try {
                // Return only this page — adapter appends; do not accumulate here.
                mutableLiveData.postValue(new ArrayList<>(QuotesCatalog.get().getQuotes(page)));
            } catch (Exception e) {
                List<QuoteResponse> error = new ArrayList<>();
                error.add(new QuoteResponse(e));
                mutableLiveData.postValue(error);
            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<List<QuoteResponse>> getQuotesByAnime(String anime, int page) {
        MutableLiveData<List<QuoteResponse>> mutableLiveData = new MutableLiveData<>();
        EXECUTOR.execute(() -> {
            try {
                mutableLiveData.postValue(new ArrayList<>(QuotesCatalog.get().getQuotesByAnime(anime, page)));
            } catch (Exception e) {
                List<QuoteResponse> error = new ArrayList<>();
                error.add(new QuoteResponse(e));
                mutableLiveData.postValue(error);
            }
        });
        return mutableLiveData;
    }
}
