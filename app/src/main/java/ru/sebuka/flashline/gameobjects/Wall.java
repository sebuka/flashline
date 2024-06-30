package ru.sebuka.flashline.gameobjects;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import ru.sebuka.flashline.R;

public class Wall extends GameObject {
    public Wall(int x, int y, View view) {
        super(x, y, view);
        updateTexture();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void updateTexture() {
        Drawable[] layers = new Drawable[2];
        layers[0] = view.getContext().getDrawable(R.drawable.grid_item_background);
        layers[1] = view.getContext().getDrawable(R.drawable.wall_texture);
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        view.setBackground(layerDrawable);
    }
}