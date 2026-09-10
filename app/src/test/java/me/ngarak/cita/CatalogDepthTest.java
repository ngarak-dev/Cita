package me.ngarak.cita;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/** Catalog quality sprint II — popular seed titles must not stay thin. */
public class CatalogDepthTest {

    private static final String[] MIN_DEPTH_TITLES = {
            "Demon Slayer: Kimetsu no Yaiba",
            "Jujutsu Kaisen",
            "Hunter x Hunter",
            "Sword Art Online",
            "Steins;Gate",
            "Neon Genesis Evangelion",
            "Spirited Away",
            "Black Clover"
    };

    @Test
    public void popularTitlesHaveMinimumDepth() throws Exception {
        InputStream in = openQuotes();
        assertNotNull(in);
        JsonArray quotes = JsonParser.parseReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonArray();
        in.close();
        assertTrue(quotes.size() >= 1200);

        Map<String, Integer> counts = new HashMap<>();
        for (JsonElement el : quotes) {
            JsonObject o = el.getAsJsonObject();
            if (!o.has("anime") || o.get("anime").isJsonNull()) continue;
            String anime = o.get("anime").getAsString();
            counts.merge(anime, 1, Integer::sum);
        }
        for (String title : MIN_DEPTH_TITLES) {
            int n = counts.getOrDefault(title, 0);
            assertTrue(title + " too thin: " + n, n >= 9);
        }
    }

    private static InputStream openQuotes() throws Exception {
        InputStream in = CatalogDepthTest.class.getClassLoader().getResourceAsStream("quotes.json");
        if (in != null) return in;
        Path path = Paths.get("src/main/assets/quotes.json");
        if (!Files.exists(path)) {
            path = Paths.get("app/src/main/assets/quotes.json");
        }
        return Files.newInputStream(path);
    }
}
