package me.ngarak.cita.ui.quotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.repositories.QuotesRepo;

public class QuotesViewModel extends ViewModel {

    private final QuotesRepo quotesRepo;
    private MutableLiveData<List<QuoteResponse>> mutableLiveData;

    public QuotesViewModel() {
        quotesRepo = new QuotesRepo();
    }

    public LiveData<List<QuoteResponse>> getQuotes (int page) {
        if (mutableLiveData == null) {
            mutableLiveData = quotesRepo.getQuotes(page);
        }
        return mutableLiveData;
    }
}