package me.ngarak.cita.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import me.ngarak.cita.Mood;
import me.ngarak.cita.R;

/** Configure Daily Drop widget mood + optional anime bias. */
public class WidgetConfigureActivity extends AppCompatActivity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner moodSpinner;
    private Spinner animeSpinner;
    private List<Mood> moods;
    private List<String> animeChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_widget_configure);

        Intent intent = getIntent();
        Bundle extras = intent != null ? intent.getExtras() : null;
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        moodSpinner = findViewById(R.id.widgetMoodSpinner);
        animeSpinner = findViewById(R.id.widgetAnimeSpinner);
        MaterialButton save = findViewById(R.id.widgetConfigSave);

        moods = new ArrayList<>();
        List<String> moodLabels = new ArrayList<>();
        for (Mood m : Mood.values()) {
            moods.add(m);
            moodLabels.add(getString(m.titleRes));
        }
        moodSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, moodLabels));

        animeChoices = WidgetPrefs.animeChoices(this);
        List<String> animeLabels = new ArrayList<>();
        for (String a : animeChoices) {
            animeLabels.add(a.isEmpty() ? getString(R.string.widget_anime_follow) : a);
        }
        animeSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, animeLabels));

        WidgetPrefs prefs = new WidgetPrefs(this);
        Mood savedMood = prefs.moodFor(appWidgetId);
        moodSpinner.setSelection(Math.max(0, moods.indexOf(savedMood)));
        String savedAnime = prefs.rawAnime(appWidgetId);
        int animeIndex = animeChoices.indexOf(savedAnime == null ? "" : savedAnime);
        animeSpinner.setSelection(Math.max(0, animeIndex));

        save.setOnClickListener(v -> persistAndFinish());
    }

    private void persistAndFinish() {
        Mood mood = moods.get(moodSpinner.getSelectedItemPosition());
        String anime = animeChoices.get(animeSpinner.getSelectedItemPosition());
        new WidgetPrefs(this).save(appWidgetId, mood, anime);

        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        CitaDailyDropWidget.updateAppWidget(this, manager, appWidgetId);

        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(Activity.RESULT_OK, result);
        Toast.makeText(this, R.string.widget_configured, Toast.LENGTH_SHORT).show();
        finish();
    }
}
