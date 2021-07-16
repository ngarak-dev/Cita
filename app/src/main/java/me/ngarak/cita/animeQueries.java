package me.ngarak.cita;

import java.util.List;

import me.ngarak.cita.models.QuoteResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface animeQueries {
    @GET("quotes")
    Call<List<QuoteResponse>> getRandomQuotes (); /*Return 10 random quotes*/

    @GET("available/anime")
    Call<String> getAnime (); /*returns available anime from API*/
}
