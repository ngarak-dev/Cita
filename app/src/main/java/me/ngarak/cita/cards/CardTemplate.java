package me.ngarak.cita.cards;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import me.ngarak.cita.CitaPlus;
import me.ngarak.cita.R;
import me.ngarak.cita.databinding.LayoutBgBottomSheetBinding;
import me.ngarak.cita.visual.CoverArt;

/** Card Studio templates — visual skins for the shareable quote card. */
public enum CardTemplate {
    CLASSIC(
            R.string.template_classic,
            Color.parseColor("#FFFFFFFF"),
            Color.parseColor("#2C2E43"),
            Color.parseColor("#5C5E70"),
            Color.parseColor("#FFD523"),
            false,
            false,
            "serif",
            Typeface.BOLD
    ),
    MIDNIGHT(
            R.string.template_midnight,
            Color.parseColor("#0A0A12"),
            Color.parseColor("#F5F5F7"),
            Color.parseColor("#B2B1B9"),
            Color.parseColor("#FFD523"),
            true,
            false,
            "sans-serif",
            Typeface.BOLD
    ),
    RESOLVE(
            R.string.template_resolve,
            Color.parseColor("#101820"),
            Color.parseColor("#F7F7F2"),
            Color.parseColor("#C5D0D8"),
            Color.parseColor("#3DDC97"),
            true,
            false,
            "sans-serif",
            Typeface.BOLD
    ),
    HEARTBREAK(
            R.string.template_heartbreak,
            Color.parseColor("#1A1014"),
            Color.parseColor("#FFE8EE"),
            Color.parseColor("#D9A5B3"),
            Color.parseColor("#FF6B8A"),
            true,
            false,
            "serif",
            Typeface.ITALIC
    ),
    CHAOS(
            R.string.template_chaos,
            Color.parseColor("#1A0A08"),
            Color.parseColor("#FFF3E8"),
            Color.parseColor("#E0B89A"),
            Color.parseColor("#FF7A18"),
            true,
            false,
            "sans-serif",
            Typeface.BOLD
    ),
    EMBER(
            R.string.template_ember,
            Color.parseColor("#1C0E0A"),
            Color.parseColor("#FFE8D6"),
            Color.parseColor("#D4A08A"),
            Color.parseColor("#FF5A1F"),
            true,
            false,
            "serif",
            Typeface.BOLD
    ),
    MIST(
            R.string.template_mist,
            Color.parseColor("#E8EEF4"),
            Color.parseColor("#1A2433"),
            Color.parseColor("#5A6A7A"),
            Color.parseColor("#4A90A8"),
            false,
            false,
            "sans-serif",
            Typeface.NORMAL
    ),
    INK(
            R.string.template_ink,
            Color.parseColor("#F4F1EA"),
            Color.parseColor("#121212"),
            Color.parseColor("#5C5C5C"),
            Color.parseColor("#111111"),
            false,
            false,
            "serif",
            Typeface.BOLD
    ),
    NEON(
            R.string.template_neon,
            Color.parseColor("#0A0614"),
            Color.parseColor("#E8F0FF"),
            Color.parseColor("#9AA8D4"),
            Color.parseColor("#39FF14"),
            true,
            false,
            "monospace",
            Typeface.BOLD
    ),
    /** Unlocked via referral invite (Phase 3 growth loop). */
    AURORA(
            R.string.template_aurora,
            Color.parseColor("#0B1220"),
            Color.parseColor("#E8F1FF"),
            Color.parseColor("#9BB4D4"),
            Color.parseColor("#7C5CFF"),
            true,
            true,
            "sans-serif",
            Typeface.BOLD
    );

    @StringRes
    public final int titleRes;
    @ColorInt
    public final int cardBackground;
    @ColorInt
    public final int quoteColor;
    @ColorInt
    public final int metaColor;
    @ColorInt
    public final int accentColor;
    public final boolean dark;
    public final boolean requiresUnlock;
    private final String typefaceFamily;
    private final int typefaceStyle;

    CardTemplate(@StringRes int titleRes, int cardBackground, int quoteColor,
                 int metaColor, int accentColor, boolean dark, boolean requiresUnlock,
                 String typefaceFamily, int typefaceStyle) {
        this.titleRes = titleRes;
        this.cardBackground = cardBackground;
        this.quoteColor = quoteColor;
        this.metaColor = metaColor;
        this.accentColor = accentColor;
        this.dark = dark;
        this.requiresUnlock = requiresUnlock;
        this.typefaceFamily = typefaceFamily;
        this.typefaceStyle = typefaceStyle;
    }

    public static List<CardTemplate> available(CitaPlus plus) {
        List<CardTemplate> list = new ArrayList<>();
        for (CardTemplate t : values()) {
            if (!t.requiresUnlock || plus.hasReferralTemplateUnlock()) {
                list.add(t);
            }
        }
        return list;
    }

    public void apply(LayoutBgBottomSheetBinding binding, boolean hideWatermark, boolean storiesFormat) {
        View root = binding.toBeConverted;
        root.setBackgroundColor(cardBackground);

        MaterialCardView card = binding.quoteCard;
        card.setCardBackgroundColor(cardBackground);
        card.setStrokeWidth(this == INK ? 2 : 0);
        if (this == INK) {
            card.setStrokeColor(Color.parseColor("#22111111"));
        }
        card.setCardElevation(dark ? 0f : card.getResources().getDimension(R.dimen.spacing_sm));

        ImageView accentBg = binding.cardAccentBg;
        if (accentBg != null) {
            accentBg.setImageResource(CoverArt.studioBgRes(ordinal()));
            accentBg.setColorFilter(Color.argb(dark ? 90 : 40, 0, 0, 0));
            accentBg.setVisibility(View.VISIBLE);
        }

        TextView quote = binding.quote;
        TextView character = binding.character;
        TextView anime = binding.anime;
        quote.setTextColor(quoteColor);
        character.setTextColor(quoteColor);
        anime.setTextColor(metaColor);
        Typeface tf = Typeface.create(typefaceFamily, typefaceStyle);
        quote.setTypeface(tf);
        quote.setLetterSpacing(this == NEON ? 0.04f : (this == HEARTBREAK ? 0.02f : 0.01f));
        quote.setLineSpacing(0f, this == MIST ? 1.25f : 1.15f);
        float quoteSp = storiesFormat ? 22f : (this == CLASSIC || this == INK ? 20f : 18f);
        quote.setTextSize(TypedValue.COMPLEX_UNIT_SP, quoteSp);
        character.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        anime.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));

        TextView avatar = binding.characterDp;
        if (avatar != null) {
            String name = character.getText() != null ? character.getText().toString() : null;
            String animeName = anime.getText() != null ? anime.getText().toString() : null;
            CoverArt.applyLetterAvatar(avatar, name, animeName);
        }

        ImageView sticker = binding.cardSticker;
        if (sticker != null) {
            sticker.setImageResource(CoverArt.stickerRes(ordinal()));
            ImageViewCompat.setImageTintList(sticker, ColorStateList.valueOf(accentColor));
            sticker.setVisibility(View.VISIBLE);
        }

        LinearLayout mark = binding.citaMark;
        mark.setVisibility(hideWatermark ? View.GONE : View.VISIBLE);
        TextView markText = null;
        ImageView markIcon = null;
        for (int i = 0; i < mark.getChildCount(); i++) {
            View child = mark.getChildAt(i);
            if (child instanceof TextView) markText = (TextView) child;
            if (child instanceof ImageView) markIcon = (ImageView) child;
        }
        if (markText != null) {
            markText.setTextColor(accentColor);
        }
        if (markIcon != null) {
            ImageViewCompat.setImageTintList(markIcon, ColorStateList.valueOf(accentColor));
        }

        int spacerVisibility = storiesFormat ? View.VISIBLE : View.GONE;
        binding.storiesTopSpacer.setVisibility(spacerVisibility);
        binding.storiesBottomSpacer.setVisibility(spacerVisibility);
    }
}
