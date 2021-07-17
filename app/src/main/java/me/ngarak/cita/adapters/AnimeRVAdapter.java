package me.ngarak.cita.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import me.ngarak.cita.databinding.LayoutAnimeSimpleBinding;

public class AnimeRVAdapter extends RecyclerView.Adapter<AnimeRVAdapter.AnimeHolder> {

    private List<String> animeList;
    private final AnimeClickListener animeClickListener;

    public AnimeRVAdapter(AnimeClickListener animeClickListener) {
        this.animeClickListener = animeClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public AnimeRVAdapter.AnimeHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutAnimeSimpleBinding binding = LayoutAnimeSimpleBinding.inflate(layoutInflater, parent, false);
        return new AnimeHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AnimeRVAdapter.AnimeHolder holder, int position) {
        String anime = animeList.get(position);
        holder.bind(anime, animeClickListener);
    }

    @Override
    public int getItemCount() {
        return animeList!= null ? animeList.size() : 0;
    }

    public List<String> getAnimeList() {
        return animeList;
    }

    public void setAnimeList(List<String> animeList) {
        this.animeList = animeList;
        notifyDataSetChanged();
    }

    protected static class AnimeHolder extends RecyclerView.ViewHolder {
        LayoutAnimeSimpleBinding binding;
        public AnimeHolder(@NonNull @NotNull LayoutAnimeSimpleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String anime, AnimeClickListener animeClickListener) {
            binding.setAnime(anime);
            binding.executePendingBindings();

            binding.linearLayout.setOnClickListener(v -> {
                animeClickListener.OnClick(anime);
            });
        }
    }

    /*onClick interface*/
    public interface AnimeClickListener {
        void OnClick(String anime);
    }
}
