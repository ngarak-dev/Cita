package me.ngarak.cita;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class retrofitInstance {

    // Optional remote API (quotes-api/). The app currently serves quotes from assets/quotes.json.
    // After deploying quotes-api to Vercel, set this to https://YOUR_PROJECT.vercel.app/api/
    private static final String ANIME_BASE_URL = "https://cita-quotes-api.vercel.app/api/";
    private static Retrofit retrofit;

    public static Retrofit getAnimeInst() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(ANIME_BASE_URL)
                    .build();
        }
        return retrofit;
    }
}
