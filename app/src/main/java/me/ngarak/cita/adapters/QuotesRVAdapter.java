package me.ngarak.cita.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.ngarak.cita.databinding.LayoutSimpleQuoteBinding;
import me.ngarak.cita.models.QuoteResponse;

public class QuotesRVAdapter extends RecyclerView.Adapter<QuotesRVAdapter.QuotesHolder> {

    private final QuoteClickListener clickListener;
    private final List<QuoteResponse> quoteList = new ArrayList<>();

    public QuotesRVAdapter(QuoteClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @NotNull
    @Override
    public QuotesRVAdapter.QuotesHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutSimpleQuoteBinding binding = LayoutSimpleQuoteBinding.inflate(layoutInflater, parent, false);
        return new QuotesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull QuotesRVAdapter.QuotesHolder holder, int position) {
        QuoteResponse quoteResponse = quoteList.get(position);
        holder.bind(quoteResponse, clickListener);
    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }

    public List<QuoteResponse> getQuoteList() {
        return quoteList;
    }

    public void clear() {
        int size = quoteList.size();
        if (size == 0) return;
        quoteList.clear();
        notifyItemRangeRemoved(0, size);
    }

    /** Replace list (page 1 / refresh) or append a new page slice. */
    public void setQuoteList(List<QuoteResponse> pageItems) {
        if (pageItems == null || pageItems.isEmpty()) {
            return;
        }
        if (quoteList.isEmpty()) {
            quoteList.addAll(pageItems);
            notifyDataSetChanged();
        } else {
            int oldSize = quoteList.size();
            quoteList.addAll(pageItems);
            notifyItemRangeInserted(oldSize, pageItems.size());
        }
    }

    public interface QuoteClickListener {
        void onClick(QuoteResponse quoteResponse);
    }

    protected static class QuotesHolder extends RecyclerView.ViewHolder {
        LayoutSimpleQuoteBinding binding;

        public QuotesHolder(@NonNull @NotNull LayoutSimpleQuoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(QuoteResponse quoteResponse, QuoteClickListener clickListener) {
            binding.setQuote(quoteResponse);
            binding.executePendingBindings();

            binding.quoteLayout.setOnClickListener(v -> clickListener.onClick(quoteResponse));
        }
    }
}
