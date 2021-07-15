package me.ngarak.cita;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class retrofitInstance {

    private static Retrofit retrofit;

    //BASE URLS
    /*
    GITHUB REST API FROM
    https://github.com/rocktimsaikia/anime-chan
    */
    private static final String ANIME_BASE_URL = "https://animechan.vercel.app/api/";

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
