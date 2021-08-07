package me.ngarak.cita;

import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import me.ngarak.cita.ui.MainActivity;

public class perm {

    public void reQuestStorage(MainActivity mainActivity) {
        PermissionX.init(mainActivity)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request(new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull @NotNull List<String> grantedList, @NonNull @NotNull List<String> deniedList) {
                    if (!allGranted) {
                        Toast.makeText(mainActivity, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    public void reQuestStorage(FragmentActivity requireActivity) {
        PermissionX.init(requireActivity)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull @NotNull List<String> grantedList, @NonNull @NotNull List<String> deniedList) {
                        if (!allGranted) {
                            Toast.makeText(requireActivity, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
