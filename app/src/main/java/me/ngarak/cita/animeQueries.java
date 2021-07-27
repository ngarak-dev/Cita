package me.ngarak.cita;

import java.util.List;

import me.ngarak.cita.models.QuoteResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface animeQueries {
    @GET("quotes")
    Call<List<QuoteResponse>> getRandomQuotes(); /*Return 10 random quotes*/

    @GET("available/anime")
    Call<List<String>> getAnime(); /*returns available anime from API*/

    @GET("quotes")
    Call<List<QuoteResponse>> getQuotes(@Query("page") int page); /*returning 10 quotes per page*/

    @GET("quotes/anime")
    Call<List<QuoteResponse>> getQuotesByAnime(@Query("title") String title, @Query("page") int page); /*returning 10 quotes per page*/
}
