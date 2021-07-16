package me.ngarak.cita.ui.random;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.repositories.RandomRepo;

public class RandomViewModel extends ViewModel {

    private final RandomRepo randomRepo;
    private MutableLiveData<List<QuoteResponse>> mutableLiveData;

    public RandomViewModel() {
        randomRepo = new RandomRepo();
    }

    public LiveData<List<QuoteResponse>> getQuote () {
        if (mutableLiveData == null) {
            mutableLiveData = randomRepo.requestQuote();
        }
        return mutableLiveData;
    }
}