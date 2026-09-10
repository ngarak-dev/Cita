package me.ngarak.cita;

import androidx.annotation.StringRes;

/** Emotional lanes for discovery (Phase 2). */
public enum Mood {
    ALL(R.string.mood_all),
    RESOLVE(R.string.mood_resolve),
    HEARTBREAK(R.string.mood_heartbreak),
    CHAOS(R.string.mood_chaos),
    COMFORT(R.string.mood_comfort);

    @StringRes
    public final int titleRes;

    Mood(@StringRes int titleRes) {
        this.titleRes = titleRes;
    }
}
