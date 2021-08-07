package me.ngarak.cita.models;

public class QuoteResponse {
    private String anime;
    private String character;
    private String quote;
    private Throwable throwable;
    private int error_code;

    public QuoteResponse(String anime, String character, String quote) {
        this.anime = anime;
        this.character = character;
        this.quote = quote;
        this.throwable = null;
        this.error_code = 200;
    }

    public QuoteResponse(Throwable throwable) {
        this.anime = null;
        this.character = null;
        this.quote = null;
        this.throwable = throwable;
        this.error_code = 100;
    }

    public QuoteResponse(int error_code) {
        this.anime = null;
        this.character = null;
        this.quote = null;
        this.throwable = null;
        this.error_code = error_code;
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

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }
}
