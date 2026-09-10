package me.ngarak.cita;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Locale;
import java.util.UUID;

/** Invite codes that unlock a bonus card template for the invitee. */
public final class ReferralStore {
    private static final String PREFS = "cita_referral";
    private static final String KEY_MY_CODE = "my_code";
    private static final String KEY_REDEEMED = "redeemed_code";
    private static final String KEY_REDEEMED_DONE = "redeemed_done";

    private final SharedPreferences prefs;
    private final Context app;

    public ReferralStore(Context context) {
        app = context.getApplicationContext();
        prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public String myCode() {
        String code = prefs.getString(KEY_MY_CODE, null);
        if (TextUtils.isEmpty(code)) {
            code = UUID.randomUUID().toString().replace("-", "")
                    .substring(0, 6).toUpperCase(Locale.US);
            prefs.edit().putString(KEY_MY_CODE, code).apply();
        }
        return code;
    }

    public boolean hasRedeemed() {
        return prefs.getBoolean(KEY_REDEEMED_DONE, false);
    }

    public String redeemedCode() {
        return prefs.getString(KEY_REDEEMED, null);
    }

    /**
     * @return null on success, or an error string resource-friendly message key
     */
    public RedeemResult redeem(String rawCode) {
        if (hasRedeemed()) {
            return RedeemResult.ALREADY;
        }
        if (TextUtils.isEmpty(rawCode)) {
            return RedeemResult.INVALID;
        }
        String code = rawCode.trim().toUpperCase(Locale.US);
        if (code.length() < 4 || code.equals(myCode())) {
            return RedeemResult.INVALID;
        }
        prefs.edit()
                .putBoolean(KEY_REDEEMED_DONE, true)
                .putString(KEY_REDEEMED, code)
                .apply();
        new CitaPlus(app).unlockReferralTemplate();
        new QuoteCredits(app).add(2);
        return RedeemResult.OK;
    }

    public Intent shareInviteIntent() {
        String code = myCode();
        String play = ShareLinks.invitePlayUrl(code);
        String body = app.getString(R.string.referral_share_body, code, play);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, app.getString(R.string.referral_share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, body);
        return Intent.createChooser(intent, app.getString(R.string.referral_share_chooser));
    }

    public enum RedeemResult {
        OK, INVALID, ALREADY
    }
}
