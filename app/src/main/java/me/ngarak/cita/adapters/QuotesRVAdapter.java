package me.ngarak.cita.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import me.ngarak.cita.databinding.LayoutSimpleQuoteBinding;
import me.ngarak.cita.models.QuoteResponse;

public class QuotesRVAdapter extends RecyclerView.Adapter<QuotesRVAdapter.QuotesHolder>{

    private  final String TAG = getClass().getSimpleName();
    private List<QuoteResponse> quoteList;

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
        holder.bind(quoteResponse);
    }

    @Override
    public int getItemCount() {
        return quoteList != null ? quoteList.size(): 0;
    }

    public List<QuoteResponse> getQuoteList() {
        return quoteList;
    }

    public void setQuoteList(List<QuoteResponse> quoteList) {
        this.quoteList = quoteList;
        notifyDataSetChanged();
    }

    protected static class QuotesHolder extends RecyclerView.ViewHolder {
        LayoutSimpleQuoteBinding binding;
        public QuotesHolder(@NonNull @NotNull LayoutSimpleQuoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(QuoteResponse quoteResponse) {
            binding.setQuote(quoteResponse);
            binding.executePendingBindings();
        }
    }
}