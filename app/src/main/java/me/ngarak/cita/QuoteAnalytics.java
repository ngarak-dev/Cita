package me.ngarak.cita;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import me.ngarak.cita.models.QuoteResponse;

/** Core funnel events for the Card Studio growth path. */
public final class QuoteAnalytics {
    public static final String EVENT_QUOTE_VIEW = "quote_view";
    public static final String EVENT_QUOTE_SAVE = "quote_save";
    public static final String EVENT_QUOTE_SHARE = "quote_share";
    public static final String EVENT_QUOTE_COPY = "quote_copy";
    public static final String EVENT_FAVORITE_TOGGLE = "favorite_toggle";
    public static final String EVENT_SEARCH = "quote_search";

    private final FirebaseAnalytics analytics;

    public QuoteAnalytics(Context context) {
        analytics = FirebaseAnalytics.getInstance(context.getApplicationContext());
    }

    public void quoteView(QuoteResponse quote) {
        logQuoteEvent(EVENT_QUOTE_VIEW, quote, null);
    }

    public void quoteSave(QuoteResponse quote) {
        logQuoteEvent(EVENT_QUOTE_SAVE, quote, null);
    }

    public void quoteShare(QuoteResponse quote) {
        logQuoteEvent(EVENT_QUOTE_SHARE, quote, null);
    }

    public void quoteCopy(QuoteResponse quote) {
        logQuoteEvent(EVENT_QUOTE_COPY, quote, null);
    }

    public void favoriteToggle(QuoteResponse quote, boolean favorited) {
        Bundle extra = new Bundle();
        extra.putString("state", favorited ? "on" : "off");
        logQuoteEvent(EVENT_FAVORITE_TOGGLE, quote, extra);
    }

    public void search(String query, int resultCount) {
        Bundle b = new Bundle();
        if (query != null) {
            b.putString(FirebaseAnalytics.Param.SEARCH_TERM,
                    query.length() > 80 ? query.substring(0, 80) : query);
        }
        b.putInt("result_count", resultCount);
        analytics.logEvent(EVENT_SEARCH, b);
    }

    public void templateSelected(String template) {
        Bundle b = new Bundle();
        b.putString("template", template);
        analytics.logEvent("card_template_select", b);
    }

    public void dailyDropOpen() {
        analytics.logEvent("daily_drop_open", new Bundle());
    }

    public void moodSelected(String mood) {
        Bundle b = new Bundle();
        b.putString("mood", mood);
        analytics.logEvent("mood_select", b);
    }

    public void onboardingComplete(int animeCount) {
        Bundle b = new Bundle();
        b.putInt("anime_count", animeCount);
        analytics.logEvent("onboarding_complete", b);
    }

    public void exportFormat(String format) {
        Bundle b = new Bundle();
        b.putString("format", format);
        analytics.logEvent("export_format", b);
    }

    public void referralShared() {
        analytics.logEvent("referral_shared", new Bundle());
    }

    public void referralRedeemed(boolean ok) {
        Bundle b = new Bundle();
        b.putString("result", ok ? "ok" : "fail");
        analytics.logEvent("referral_redeemed", b);
    }

    /** Weekly Shared Cards north-star signal. */
    public void weeklyShare(int weekCount, int streakWeeks) {
        Bundle b = new Bundle();
        b.putInt("week_share_count", weekCount);
        b.putInt("streak_weeks", streakWeeks);
        analytics.logEvent("weekly_shared_card", b);
    }

    private void logQuoteEvent(String name, QuoteResponse quote, Bundle extra) {
        Bundle b = extra != null ? new Bundle(extra) : new Bundle();
        if (quote != null) {
            if (quote.getAnime() != null) {
                b.putString("anime", truncate(quote.getAnime(), 80));
            }
            if (quote.getCharacter() != null) {
                b.putString("character", truncate(quote.getCharacter(), 80));
            }
        }
        analytics.logEvent(name, b);
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }
}
