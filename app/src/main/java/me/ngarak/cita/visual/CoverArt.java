package me.ngarak.cita.visual;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import me.ngarak.cita.R;

/**
 * Deterministic abstract covers & lettermarks keyed by anime/name hash.
 * No licensed character art — procedural layers + typography only.
 */
public final class CoverArt {

    private static final int[] COVER_DRAWABLES = {
            R.drawable.bg_cover_ember,
            R.drawable.bg_cover_mist,
            R.drawable.bg_cover_ink,
            R.drawable.bg_cover_neon,
            R.drawable.bg_cover_dusk,
            R.drawable.bg_cover_jade,
            R.drawable.bg_cover_rose,
            R.drawable.bg_cover_steel,
            R.drawable.bg_cover_amber,
            R.drawable.bg_cover_violet,
            R.drawable.bg_cover_ocean,
            R.drawable.bg_cover_flare,
            R.drawable.bg_cover_noir,
            R.drawable.bg_cover_sakura,
            R.drawable.bg_cover_volt,
            R.drawable.bg_cover_tide
    };

    private static final int[] STUDIO_BACKGROUNDS = {
            R.drawable.bg_studio_paper,
            R.drawable.bg_studio_void,
            R.drawable.bg_studio_ember,
            R.drawable.bg_studio_mist,
            R.drawable.bg_studio_ink,
            R.drawable.bg_studio_neon,
            R.drawable.bg_studio_aurora,
            R.drawable.bg_studio_rose
    };

    private static final int[] STICKERS = {
            R.drawable.ic_sticker_spark,
            R.drawable.ic_sticker_star,
            R.drawable.ic_sticker_moon,
            R.drawable.ic_sticker_quote,
            R.drawable.ic_sticker_ring,
            R.drawable.ic_sticker_bolt,
            R.drawable.ic_sticker_heart,
            R.drawable.ic_sticker_petal,
            R.drawable.ic_sticker_orbit,
            R.drawable.ic_mark_slash
    };

    private CoverArt() {
    }

    public static int hash(@Nullable String key) {
        if (key == null || key.isEmpty()) return 0;
        int h = 0x811c9dc5;
        for (int i = 0; i < key.length(); i++) {
            h ^= Character.toLowerCase(key.charAt(i));
            h *= 0x01000193;
        }
        return h;
    }

    @DrawableRes
    public static int coverRes(@Nullable String key) {
        int idx = Math.floorMod(hash(key), COVER_DRAWABLES.length);
        return COVER_DRAWABLES[idx];
    }

    @DrawableRes
    public static int studioBgRes(int templateOrdinal) {
        int idx = Math.floorMod(templateOrdinal, STUDIO_BACKGROUNDS.length);
        return STUDIO_BACKGROUNDS[idx];
    }

    /** Prefer procedural layered cover (unique geometry per key). */
    public static void applyCover(@NonNull View view, @Nullable String key) {
        int h = hash(key);
        view.setBackground(new LayeredCoverDrawable(h, palette(h)));
    }

    /** Circular lettermark avatar — first letter of character or anime. */
    public static void applyLetterAvatar(@NonNull TextView target, @Nullable String name,
                                         @Nullable String fallbackKey) {
        String seed = (name != null && !name.isEmpty()) ? name : fallbackKey;
        int h = hash(seed);
        int[] colors = palette(h);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColors(new int[]{colors[0], colors[1]});
        bg.setOrientation(GradientDrawable.Orientation.TL_BR);
        target.setBackground(bg);
        target.setText(letterOf(name != null && !name.isEmpty() ? name : fallbackKey));
        target.setTextColor(Color.WHITE);
        target.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        target.setGravity(android.view.Gravity.CENTER);
        target.setIncludeFontPadding(false);
    }

    public static void applyCoverImage(@NonNull ImageView image, @Nullable String key) {
        image.setImageDrawable(null);
        int h = hash(key);
        image.setBackground(new LayeredCoverDrawable(h, palette(h)));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public static void applyLetterOverlay(@NonNull TextView letter, @Nullable String name) {
        letter.setText(letterOf(name));
        letter.setTextColor(Color.WHITE);
        letter.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        letter.setShadowLayer(6f, 0f, 2f, Color.parseColor("#66000000"));
    }

    @NonNull
    public static String letterOf(@Nullable String name) {
        if (name == null || name.trim().isEmpty()) return "C";
        String t = name.trim();
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                return String.valueOf(Character.toUpperCase(c));
            }
        }
        return "C";
    }

    @ColorInt
    public static int accentOf(@Nullable String key) {
        return palette(hash(key))[2];
    }

    /** Returns [start, end, accent]. */
    @NonNull
    public static int[] palette(int h) {
        float hue = Math.floorMod(h, 360);
        float hue2 = (hue + 28f + (Math.floorMod(h >> 8, 40))) % 360f;
        int start = Color.HSVToColor(new float[]{hue, 0.55f + (Math.floorMod(h, 20) / 100f), 0.28f});
        int end = Color.HSVToColor(new float[]{hue2, 0.45f, 0.55f});
        int accent = Color.HSVToColor(new float[]{(hue + 180f) % 360f, 0.65f, 0.92f});
        return new int[]{start, end, accent};
    }

    public static int coverCount() {
        return COVER_DRAWABLES.length;
    }

    public static void tintAccent(@NonNull Context context, @NonNull View view, @Nullable String key) {
        view.setBackgroundColor(accentOf(key));
    }

    @DrawableRes
    public static int accentMarkRes(int templateOrdinal) {
        return stickerRes(templateOrdinal);
    }

    @DrawableRes
    public static int stickerRes(int templateOrdinal) {
        return STICKERS[Math.floorMod(templateOrdinal, STICKERS.length)];
    }

    public static int colorCompat(@NonNull Context context, @DrawableRes int res) {
        return ContextCompat.getColor(context, android.R.color.transparent);
    }
}
