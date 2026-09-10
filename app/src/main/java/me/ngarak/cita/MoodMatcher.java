package me.ngarak.cita;

import java.util.Locale;

import me.ngarak.cita.models.QuoteResponse;

/** Lightweight keyword heuristics for mood lanes (Phase 2). */
public final class MoodMatcher {

    private MoodMatcher() {
    }

    public static boolean matches(QuoteResponse quote, Mood mood) {
        if (quote == null || mood == null || mood == Mood.ALL) return true;
        String text = ((quote.getQuote() == null ? "" : quote.getQuote()) + " "
                + (quote.getCharacter() == null ? "" : quote.getCharacter()))
                .toLowerCase(Locale.US);

        switch (mood) {
            case RESOLVE:
                return containsAny(text,
                        "never give up", "believe", "stronger", "fight", "protect",
                        "dream", "goal", "power", "courage", "will", "stand", "survivor",
                        "nakama", "hero", "victory", "become", "train", "destiny");
            case HEARTBREAK:
                return containsAny(text,
                        "love", "heart", "alone", "lonely", "cry", "tears", "pain",
                        "goodbye", "miss", "lost", "sad", "hurt", "death", "die",
                        "grief", "sorry", "leave", "broken");
            case CHAOS:
                return containsAny(text,
                        "kill", "destroy", "hate", "revenge", "war", "blood", "fear",
                        "monster", "chaos", "dark", "rage", "enemy", "burn", "hell",
                        "madness", "curse");
            case COMFORT:
                return containsAny(text,
                        "friend", "together", "home", "kind", "peace", "hope", "smile",
                        "happy", "family", "warm", "gentle", "trust", "care", "thank",
                        "live", "tomorrow", "okay", "safe");
            default:
                return true;
        }
    }

    private static boolean containsAny(String text, String... needles) {
        for (String n : needles) {
            if (text.contains(n)) return true;
        }
        return false;
    }
}
