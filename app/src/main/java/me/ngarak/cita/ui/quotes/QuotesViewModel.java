package me.ngarak.cita.ui.quotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.repositories.QuotesRepo;

public class QuotesViewModel extends ViewModel {

    private final QuotesRepo quotesRepo = new QuotesRepo();

    public LiveData<List<QuoteResponse>> getQuotes(int page) {
        return quotesRepo.getQuotes(page);
    }

    public LiveData<List<QuoteResponse>> getQuotesByAnime(String anime, int page) {
        return quotesRepo.getQuotesByAnime(anime, page);
    }
}
