package me.ngarak.cita.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.ngarak.cita.Mood;
import me.ngarak.cita.QuotesCatalog;
import me.ngarak.cita.models.QuoteResponse;

public class RandomRepo {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public MutableLiveData<List<QuoteResponse>> requestQuote() {
        return requestQuote(Mood.ALL, null);
    }

    public MutableLiveData<List<QuoteResponse>> requestQuote(Mood mood, List<String> preferredAnime) {
        MutableLiveData<List<QuoteResponse>> mutableLiveData = new MutableLiveData<>();
        EXECUTOR.execute(() -> {
            try {
                List<QuoteResponse> list = QuotesCatalog.get().getRandomByMood(mood, preferredAnime);
                mutableLiveData.postValue(new ArrayList<>(list));
            } catch (Exception e) {
                List<QuoteResponse> error = new ArrayList<>();
                error.add(new QuoteResponse(e));
                mutableLiveData.postValue(error);
            }
        });
        return mutableLiveData;
    }
}
