package me.ngarak.cita.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.ngarak.cita.R;
import me.ngarak.cita.databinding.ItemPackBinding;
import me.ngarak.cita.models.QuotePack;

public class PacksRVAdapter extends RecyclerView.Adapter<PacksRVAdapter.Holder> {

    public interface Listener {
        void onPackClick(QuotePack pack);
    }

    private final Listener listener;
    private final List<QuotePack> packs = new ArrayList<>();

    public PacksRVAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setPacks(List<QuotePack> list) {
        packs.clear();
        if (list != null) packs.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPackBinding binding = ItemPackBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(packs.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return packs.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        private final ItemPackBinding binding;

        Holder(ItemPackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(QuotePack pack, Listener listener) {
            binding.packTitle.setText(pack.title);
            binding.packSubtitle.setText(pack.subtitle);
            int count = pack.quoteKeys != null ? pack.quoteKeys.size() : 0;
            String mood = pack.mood != null ? pack.mood : "";
            binding.packMeta.setText(binding.getRoot().getContext()
                    .getString(R.string.pack_meta, mood, count));
            binding.getRoot().setOnClickListener(v -> listener.onPackClick(pack));
        }
    }
}
