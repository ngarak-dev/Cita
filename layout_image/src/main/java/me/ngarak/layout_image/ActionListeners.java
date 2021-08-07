package me.ngarak.layout_image;

import android.graphics.Bitmap;

public interface ActionListeners {
    public void convertedWithSuccess(Bitmap bitmap, String filePath, String absolutePath);
    public void convertedWithError(String error);

}
