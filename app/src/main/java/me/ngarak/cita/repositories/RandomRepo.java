package me.ngarak.cita.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.ngarak.cita.Mood;
import me.ngarak.cita.QuotesCatalog;
import me.ngarak.cita.TasteModel;
import me.ngarak.cita.models.QuoteResponse;

public class RandomRepo {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final Context appContext;

    public RandomRepo() {
        this.appContext = null;
    }

    public RandomRepo(Context context) {
        this.appContext = context != null ? context.getApplicationContext() : null;
    }

    public MutableLiveData<List<QuoteResponse>> requestQuote() {
        return requestQuote(Mood.ALL, null);
    }

    public MutableLiveData<List<QuoteResponse>> requestQuote(Mood mood, List<String> preferredAnime) {
        MutableLiveData<List<QuoteResponse>> mutableLiveData = new MutableLiveData<>();
        EXECUTOR.execute(() -> {
            try {
                List<QuoteResponse> list = QuotesCatalog.get().getRandomByMood(mood, preferredAnime);
                if (appContext != null) {
                    TasteModel taste = new TasteModel(appContext);
                    // Blend: take a larger mood pool, rank by taste, keep page size.
                    List<QuoteResponse> pool = QuotesCatalog.get().getQuotesForMood(mood, preferredAnime);
                    if (pool.size() > list.size()) {
                        list = taste.rank(pool, mood);
                        if (list.size() > 10) {
                            list = new ArrayList<>(list.subList(0, 10));
                        }
                    } else {
                        list = taste.rank(list, mood);
                    }
                }
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
