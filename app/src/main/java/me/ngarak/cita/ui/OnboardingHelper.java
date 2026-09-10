package me.ngarak.cita.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.ngarak.cita.QuoteAnalytics;
import me.ngarak.cita.QuotesCatalog;
import me.ngarak.cita.R;
import me.ngarak.cita.UserTaste;

/** First-run taste picker — up to 3 anime. */
public final class OnboardingHelper {

    private OnboardingHelper() {
    }

    public static void maybeShow(Activity activity, Runnable onDone) {
        UserTaste taste = new UserTaste(activity);
        if (!taste.needsOnboarding()) {
            if (onDone != null) onDone.run();
            return;
        }

        View view = LayoutInflater.from(activity).inflate(R.layout.layout_onboarding_taste, null);
        ChipGroup group = view.findViewById(R.id.animeChipGroup);
        MaterialButton skip = view.findViewById(R.id.skipBtn);
        MaterialButton cont = view.findViewById(R.id.continueBtn);

        Set<String> catalog = new HashSet<>(QuotesCatalog.get().getAvailableAnime());
        List<String> suggestions = new ArrayList<>();
        for (String s : UserTaste.seedSuggestions()) {
            for (String title : catalog) {
                if (title.equalsIgnoreCase(s) || title.toLowerCase().contains(s.toLowerCase())) {
                    if (!suggestions.contains(title)) suggestions.add(title);
                    break;
                }
            }
        }
        // Fill remaining slots from catalog
        for (String title : QuotesCatalog.get().getAvailableAnime()) {
            if (suggestions.size() >= 18) break;
            if (!suggestions.contains(title)) suggestions.add(title);
        }

        for (String title : suggestions) {
            Chip chip = new Chip(activity);
            chip.setText(title);
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int checked = 0;
                for (int i = 0; i < group.getChildCount(); i++) {
                    if (((Chip) group.getChildAt(i)).isChecked()) checked++;
                }
                if (isChecked && checked > 3) {
                    buttonView.setChecked(false);
                    Toast.makeText(activity, R.string.onboarding_pick_limit, Toast.LENGTH_SHORT).show();
                }
            });
            group.addView(chip);
        }

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(false)
                .create();

        QuoteAnalytics analytics = new QuoteAnalytics(activity);
        skip.setOnClickListener(v -> {
            taste.skipOnboarding();
            analytics.onboardingComplete(0);
            dialog.dismiss();
            if (onDone != null) onDone.run();
        });
        cont.setOnClickListener(v -> {
            List<String> picked = new ArrayList<>();
            for (int i = 0; i < group.getChildCount(); i++) {
                Chip chip = (Chip) group.getChildAt(i);
                if (chip.isChecked()) picked.add(chip.getText().toString());
            }
            taste.completeOnboarding(picked);
            analytics.onboardingComplete(picked.size());
            dialog.dismiss();
            if (onDone != null) onDone.run();
        });
        dialog.show();
    }
}
