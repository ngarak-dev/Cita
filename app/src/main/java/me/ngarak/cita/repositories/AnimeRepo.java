package me.ngarak.cita.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.ngarak.cita.QuotesCatalog;

public class AnimeRepo {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final List<String> animeList = new ArrayList<>();

    public MutableLiveData<List<String>> requestAnime() {
        return searchAnime(null);
    }

    public MutableLiveData<List<String>> searchAnime(String query) {
        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();
        EXECUTOR.execute(() -> {
            try {
                animeList.clear();
                animeList.addAll(QuotesCatalog.get().searchAnime(query));
                mutableLiveData.postValue(new ArrayList<>(animeList));
            } catch (Exception ignored) {
                mutableLiveData.postValue(new ArrayList<>());
            }
        });
        return mutableLiveData;
    }
}
