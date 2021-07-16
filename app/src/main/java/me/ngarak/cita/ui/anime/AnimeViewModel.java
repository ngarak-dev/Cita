package me.ngarak.cita.ui.anime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.ngarak.cita.repositories.AnimeRepo;

public class AnimeViewModel extends ViewModel {
    private MutableLiveData<List<String>> mutableLiveData;
    private final AnimeRepo animeRepo;

    public AnimeViewModel() {
        animeRepo = new AnimeRepo();
    }

    public LiveData<List<String>> getAnime () {
        if (mutableLiveData == null) {
            mutableLiveData = animeRepo.requestAnime();
        }
        return mutableLiveData;
    }
}