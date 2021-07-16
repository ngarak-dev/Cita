package me.ngarak.cita.models;

public class QuoteResponse {
    private String anime;
    private String character;
    private String quote;

    public QuoteResponse(String anime, String character, String quote) {
        this.anime = anime;
        this.character = character;
        this.quote = quote;
    }

    public String getAnime() {
        return anime;
    }

    public void setAnime(String anime) {
        this.anime = anime;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }
}
