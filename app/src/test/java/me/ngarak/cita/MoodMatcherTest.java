package me.ngarak.cita;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import me.ngarak.cita.models.QuoteResponse;

public class MoodMatcherTest {

    @Test
    public void resolveMatchesWillpowerLanguage() {
        QuoteResponse q = new QuoteResponse("Naruto", "Naruto", "I will never give up on my dream!");
        assertTrue(MoodMatcher.matches(q, Mood.RESOLVE));
    }

    @Test
    public void heartbreakMatchesLoveLanguage() {
        QuoteResponse q = new QuoteResponse("Show", "Hero", "My heart is broken and I feel alone.");
        assertTrue(MoodMatcher.matches(q, Mood.HEARTBREAK));
    }

    @Test
    public void allAlwaysMatches() {
        QuoteResponse q = new QuoteResponse("A", "B", "Anything goes here.");
        assertTrue(MoodMatcher.matches(q, Mood.ALL));
        assertFalse(MoodMatcher.matches(q, Mood.CHAOS));
    }

    @Test
    public void favoritesKeyStable() {
        QuoteResponse a = new QuoteResponse("One Piece", "Luffy", "I'm gonna be king!");
        QuoteResponse b = new QuoteResponse("One Piece", "Luffy", "I'm gonna be king!");
        assertEquals(FavoritesStore.keyOf(a), FavoritesStore.keyOf(b));
    }
}
