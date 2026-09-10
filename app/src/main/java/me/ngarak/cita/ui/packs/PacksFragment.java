package me.ngarak.cita.ui.packs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.ngarak.cita.PacksCatalog;
import me.ngarak.cita.adapters.PacksRVAdapter;
import me.ngarak.cita.databinding.FragmentPacksBinding;

public class PacksFragment extends Fragment {

    private FragmentPacksBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPacksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PacksRVAdapter adapter = new PacksRVAdapter(pack -> {
            Intent intent = new Intent(requireContext(), PackDetailActivity.class);
            intent.putExtra(PackDetailActivity.EXTRA_PACK_ID, pack.id);
            startActivity(intent);
        });
        binding.packsRv.setAdapter(adapter);
        // Seasonal first, then the rest without duplicates.
        java.util.LinkedHashMap<String, me.ngarak.cita.models.QuotePack> map =
                new java.util.LinkedHashMap<>();
        for (me.ngarak.cita.models.QuotePack p : PacksCatalog.get().featuredNow()) {
            map.put(p.id, p);
        }
        for (me.ngarak.cita.models.QuotePack p : PacksCatalog.get().allPacks()) {
            map.putIfAbsent(p.id, p);
        }
        adapter.setPacks(new java.util.ArrayList<>(map.values()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
