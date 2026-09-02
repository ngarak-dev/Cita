package me.ngarak.cita.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.ngarak.cita.QuotesCatalog;
import me.ngarak.cita.models.QuoteResponse;

public class RandomRepo {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final List<QuoteResponse> responseList = new ArrayList<>();

    public MutableLiveData<List<QuoteResponse>> requestQuote() {
        MutableLiveData<List<QuoteResponse>> mutableLiveData = new MutableLiveData<>();
        EXECUTOR.execute(() -> {
            try {
                responseList.clear();
                responseList.addAll(QuotesCatalog.get().getRandomQuotes());
                mutableLiveData.postValue(new ArrayList<>(responseList));
            } catch (Exception e) {
                responseList.clear();
                responseList.add(new QuoteResponse(e));
                mutableLiveData.postValue(new ArrayList<>(responseList));
            }
        });
        return mutableLiveData;
    }
}
