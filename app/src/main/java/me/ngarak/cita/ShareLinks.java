package me.ngarak.cita;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Play Store URLs with UTM / install-referrer tags for content + invite loops.
 * Lightweight “smart link” helper — no third-party shortener required.
 * Pure string build so unit tests don’t need Android Uri mocks.
 */
public final class ShareLinks {

    public static final String PACKAGE_ID = "me.ngarak.cita";
    private static final String PLAY_DETAILS =
            "https://play.google.com/store/apps/details";

    private ShareLinks() {
    }

    /** Organic content post (TikTok / Reels / Shorts). */
    @NonNull
    public static String contentPlayUrl(@Nullable String campaign) {
        return playUrl("tiktok", "organic",
                campaign != null && !campaign.isEmpty() ? campaign : "content_engine",
                null);
    }

    /** Invite share — also encodes referrer for Play install attribution. */
    @NonNull
    public static String invitePlayUrl(@NonNull String inviteCode) {
        String referrer = "utm_source%3Dcita_invite%26utm_medium%3Dreferral%26utm_campaign%3Dinvite%26utm_content%3D"
                + encodeQuery(inviteCode);
        return PLAY_DETAILS + "?id=" + PACKAGE_ID + "&referrer=" + referrer;
    }

    @NonNull
    public static String playUrl(@NonNull String source, @NonNull String medium,
                                 @NonNull String campaign, @Nullable String content) {
        StringBuilder b = new StringBuilder(PLAY_DETAILS)
                .append("?id=").append(PACKAGE_ID)
                .append("&utm_source=").append(encodeQuery(source))
                .append("&utm_medium=").append(encodeQuery(medium))
                .append("&utm_campaign=").append(encodeQuery(campaign));
        if (content != null && !content.isEmpty()) {
            b.append("&utm_content=").append(encodeQuery(content));
        }
        return b.toString();
    }

    /** Minimal query encoding for UTM tokens (alphanumeric + underscore safe). */
    @NonNull
    static String encodeQuery(@NonNull String raw) {
        StringBuilder out = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9') || c == '_' || c == '-' || c == '.') {
                out.append(c);
            } else if (c == ' ') {
                out.append('%').append("20");
            } else {
                out.append('%');
                String hex = Integer.toHexString(c).toUpperCase();
                if (hex.length() == 1) out.append('0');
                out.append(hex);
            }
        }
        return out.toString();
    }
}
