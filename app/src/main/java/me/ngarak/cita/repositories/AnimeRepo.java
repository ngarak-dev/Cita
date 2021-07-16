package me.ngarak.cita.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.ngarak.cita.animeQueries;
import me.ngarak.cita.retrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeRepo {
    private final String TAG = getClass().getSimpleName();
    private final List<String> animeList = new ArrayList<>();

    public MutableLiveData<List<String>> requestAnime() {
        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();

        //initializing retrofit
        animeQueries queries = retrofitInstance.getAnimeInst().create(animeQueries.class);
        //network call
        Call<List<String>> responseCall = queries.getAnime();
        responseCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NotNull Call<List<String>> call, @NotNull Response<List<String>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        animeList.addAll(response.body());
                        mutableLiveData.postValue(animeList);
                    }
                }
                else {
                    Log.d(TAG, "onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<String>> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: ", t);
            }
        });

        return mutableLiveData;
    }
}
