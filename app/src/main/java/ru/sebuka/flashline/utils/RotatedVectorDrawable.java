package ru.sebuka.flashline.utils;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;

public class RotatedVectorDrawable extends Drawable {
    private final VectorDrawable vectorDrawable;
    private final float angle;
    private final Matrix matrix;
    private final Rect bounds;

    public RotatedVectorDrawable(VectorDrawable vectorDrawable, float angle) {
        this.vectorDrawable = vectorDrawable;
        this.angle = angle;
        this.matrix = new Matrix();
        this.bounds = new Rect();
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        copyBounds(bounds);
        int width = bounds.width();
        int height = bounds.height();
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();

        canvas.save();
        canvas.rotate(angle, centerX, centerY);
        vectorDrawable.setBounds(bounds);
        vectorDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        vectorDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        vectorDrawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return vectorDrawable.getOpacity();
    }

    @Override
    public int getIntrinsicWidth() {
        return vectorDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return vectorDrawable.getIntrinsicHeight();
    }
}
