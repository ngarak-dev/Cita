package me.ngarak.cita;

import org.junit.Test;

import me.ngarak.cita.visual.CoverArt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CoverArtTest {

    @Test
    public void hash_isDeterministic() {
        assertEquals(CoverArt.hash("Naruto"), CoverArt.hash("Naruto"));
        assertEquals(CoverArt.hash("naruto"), CoverArt.hash("Naruto"));
    }

    @Test
    public void hash_differsByTitle() {
        assertNotEquals(CoverArt.hash("Naruto"), CoverArt.hash("Bleach"));
    }

    @Test
    public void letterOf_usesFirstLetter() {
        assertEquals("N", CoverArt.letterOf("Naruto"));
        assertEquals("C", CoverArt.letterOf(null));
        assertEquals("C", CoverArt.letterOf("   "));
    }

    @Test
    public void coverRes_inRange() {
        int res = CoverArt.coverRes("Attack on Titan");
        assertTrue(res != 0);
        assertTrue(CoverArt.coverCount() >= 8);
    }
}
