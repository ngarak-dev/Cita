package me.ngarak.cita.ui.packs;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.ngarak.cita.PacksCatalog;
import me.ngarak.cita.QuoteSheetController;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.ActivityPackDetailBinding;
import me.ngarak.cita.models.QuotePack;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.perm;

import java.util.List;

public class PackDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PACK_ID = "pack_id";

    private ActivityPackDetailBinding binding;
    private QuoteSheetController sheetController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPackDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sheetController = new QuoteSheetController(new QuoteSheetController.Host() {
            @NonNull
            @Override
            public Activity activity() {
                return PackDetailActivity.this;
            }

            @Override
            public boolean isActive() {
                return !isFinishing();
            }

            @Override
            public void requestStoragePermission() {
                new perm().reQuestStorage(PackDetailActivity.this);
            }
        });

        String packId = getIntent().getStringExtra(EXTRA_PACK_ID);
        QuotePack pack = null;
        for (QuotePack p : PacksCatalog.get().allPacks()) {
            if (p.id != null && p.id.equals(packId)) {
                pack = p;
                break;
            }
        }

        setSupportActionBar(binding.toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(pack != null ? pack.title : "");
        }
        binding.toolBar.setNavigationOnClickListener(v -> finish());

        if (pack != null) {
            binding.packSubtitle.setText(pack.subtitle);
            List<QuoteResponse> quotes = PacksCatalog.get().resolveQuotes(pack);
            QuotesRVAdapter adapter = new QuotesRVAdapter(q ->
                    startActivity(me.ngarak.cita.ui.QuoteDetailActivity.intent(this, q)));
            binding.quotesRv.setAdapter(adapter);
            adapter.setQuoteList(quotes);
        }
    }
}
