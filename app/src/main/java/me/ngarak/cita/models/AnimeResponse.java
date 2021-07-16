package me.ngarak.cita.models;

import java.io.Serializable;

public class AnimeResponse implements Serializable {
    private String anime;

    public AnimeResponse(String anime) {
        this.anime = anime;
    }

    public String getAnime() {
        return anime;
    }

    public void setAnime(String anime) {
        this.anime = anime;
    }
}
