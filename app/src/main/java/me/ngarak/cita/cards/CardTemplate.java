package me.ngarak.cita.cards;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.card.MaterialCardView;

import me.ngarak.cita.R;
import me.ngarak.cita.databinding.LayoutBgBottomSheetBinding;

/** Phase 2 Card Studio templates — visual skins for the shareable quote card. */
public enum CardTemplate {
    CLASSIC(
            R.string.template_classic,
            Color.parseColor("#FFFFFFFF"),
            Color.parseColor("#2C2E43"),
            Color.parseColor("#5C5E70"),
            Color.parseColor("#FFD523"),
            false
    ),
    MIDNIGHT(
            R.string.template_midnight,
            Color.parseColor("#0A0A12"),
            Color.parseColor("#F5F5F7"),
            Color.parseColor("#B2B1B9"),
            Color.parseColor("#FFD523"),
            true
    ),
    RESOLVE(
            R.string.template_resolve,
            Color.parseColor("#101820"),
            Color.parseColor("#F7F7F2"),
            Color.parseColor("#C5D0D8"),
            Color.parseColor("#3DDC97"),
            true
    ),
    HEARTBREAK(
            R.string.template_heartbreak,
            Color.parseColor("#1A1014"),
            Color.parseColor("#FFE8EE"),
            Color.parseColor("#D9A5B3"),
            Color.parseColor("#FF6B8A"),
            true
    ),
    CHAOS(
            R.string.template_chaos,
            Color.parseColor("#1A0A08"),
            Color.parseColor("#FFF3E8"),
            Color.parseColor("#E0B89A"),
            Color.parseColor("#FF7A18"),
            true
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

    CardTemplate(@StringRes int titleRes, int cardBackground, int quoteColor,
                 int metaColor, int accentColor, boolean dark) {
        this.titleRes = titleRes;
        this.cardBackground = cardBackground;
        this.quoteColor = quoteColor;
        this.metaColor = metaColor;
        this.accentColor = accentColor;
        this.dark = dark;
    }

    public void apply(LayoutBgBottomSheetBinding binding) {
        View root = binding.toBeConverted;
        root.setBackgroundColor(cardBackground);

        MaterialCardView card = binding.quoteCard;
        card.setCardBackgroundColor(cardBackground);
        card.setStrokeWidth(dark ? 0 : 0);
        card.setCardElevation(dark ? 0f : card.getResources().getDimension(R.dimen.spacing_sm));

        TextView quote = binding.quote;
        TextView character = binding.character;
        TextView anime = binding.anime;
        quote.setTextColor(quoteColor);
        character.setTextColor(quoteColor);
        anime.setTextColor(metaColor);
        quote.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));

        LinearLayout mark = binding.citaMark;
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
    }
}
