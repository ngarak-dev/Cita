package me.ngarak.cita.ui.random;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import me.ngarak.cita.Mood;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.repositories.RandomRepo;

public class RandomViewModel extends AndroidViewModel {

    private final RandomRepo randomRepo;

    public RandomViewModel(@NonNull Application application) {
        super(application);
        randomRepo = new RandomRepo(application);
    }

    public LiveData<List<QuoteResponse>> getQuote() {
        return randomRepo.requestQuote();
    }

    public LiveData<List<QuoteResponse>> getQuote(Mood mood, List<String> preferredAnime) {
        return randomRepo.requestQuote(mood, preferredAnime);
    }
}
