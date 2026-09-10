package me.ngarak.cita.visual;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Procedural layered cover — gradient base + geometric accents keyed by hash.
 * Looks intentional per-anime without licensed art.
 */
public final class LayeredCoverDrawable extends Drawable {

    private final int hash;
    private final Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint washPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint accentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint vignettePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path accentPath = new Path();
    private final int pattern;
    private final int[] palette;

    public LayeredCoverDrawable(int hash, @NonNull int[] palette) {
        this.hash = hash;
        this.palette = palette;
        this.pattern = Math.floorMod(hash >> 4, 4);
        accentPaint.setStyle(Paint.Style.STROKE);
        accentPaint.setStrokeWidth(3f);
        accentPaint.setColor(Color.argb(90, Color.red(palette[2]),
                Color.green(palette[2]), Color.blue(palette[2])));
    }

    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);
        int w = bounds.width();
        int h = bounds.height();
        if (w <= 0 || h <= 0) return;

        float angle = Math.floorMod(hash, 2) == 0 ? 0f : 1f;
        basePaint.setShader(new LinearGradient(
                bounds.left, bounds.top,
                angle == 0 ? bounds.right : bounds.left + w * 0.2f,
                angle == 0 ? bounds.bottom : bounds.bottom,
                palette[0], palette[1],
                Shader.TileMode.CLAMP));

        int mid = Color.argb(70, Color.red(palette[2]), Color.green(palette[2]), Color.blue(palette[2]));
        washPaint.setShader(new RadialGradient(
                bounds.left + w * (0.2f + Math.floorMod(hash, 60) / 100f),
                bounds.top + h * (0.15f + Math.floorMod(hash >> 8, 50) / 100f),
                Math.max(w, h) * 0.75f,
                mid, Color.TRANSPARENT,
                Shader.TileMode.CLAMP));

        vignettePaint.setShader(new RadialGradient(
                bounds.exactCenterX(), bounds.exactCenterY(),
                Math.max(w, h) * 0.72f,
                Color.TRANSPARENT, Color.argb(120, 0, 0, 0),
                Shader.TileMode.CLAMP));

        accentPath.reset();
        float pad = Math.min(w, h) * 0.12f;
        switch (pattern) {
            case 0: // diagonal slash
                accentPath.moveTo(bounds.left + pad, bounds.bottom - pad);
                accentPath.lineTo(bounds.right - pad, bounds.top + pad);
                break;
            case 1: // arc
                accentPath.addCircle(bounds.exactCenterX(), bounds.bottom + h * 0.1f,
                        h * 0.55f, Path.Direction.CW);
                break;
            case 2: // ring
                accentPaint.setStrokeWidth(Math.max(2f, Math.min(w, h) * 0.035f));
                accentPath.addCircle(bounds.exactCenterX(), bounds.exactCenterY(),
                        Math.min(w, h) * 0.28f, Path.Direction.CW);
                break;
            default: // corner bars
                accentPath.moveTo(bounds.left + pad, bounds.top + pad * 2);
                accentPath.lineTo(bounds.left + pad, bounds.top + pad);
                accentPath.lineTo(bounds.left + pad * 2.5f, bounds.top + pad);
                accentPath.moveTo(bounds.right - pad, bounds.bottom - pad * 2);
                accentPath.lineTo(bounds.right - pad, bounds.bottom - pad);
                accentPath.lineTo(bounds.right - pad * 2.5f, bounds.bottom - pad);
                break;
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect b = getBounds();
        if (b.isEmpty()) return;
        canvas.drawRect(b, basePaint);
        canvas.drawRect(b, washPaint);
        canvas.drawPath(accentPath, accentPaint);
        canvas.drawRect(b, vignettePaint);
    }

    @Override
    public void setAlpha(int alpha) {
        basePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        basePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
