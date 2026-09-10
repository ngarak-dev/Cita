package me.ngarak.cita.ui.anime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.ngarak.cita.repositories.AnimeRepo;

public class AnimeViewModel extends ViewModel {
    private final AnimeRepo animeRepo = new AnimeRepo();

    public LiveData<List<String>> getAnime() {
        return animeRepo.requestAnime();
    }

    public LiveData<List<String>> searchAnime(String query) {
        return animeRepo.searchAnime(query);
    }
}
