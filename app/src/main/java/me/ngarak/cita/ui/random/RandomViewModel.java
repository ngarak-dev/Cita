package me.ngarak.cita.ui.random;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.repositories.RandomRepo;

public class RandomViewModel extends ViewModel {

    private final RandomRepo randomRepo = new RandomRepo();

    public LiveData<List<QuoteResponse>> getQuote() {
        return randomRepo.requestQuote();
    }
}
