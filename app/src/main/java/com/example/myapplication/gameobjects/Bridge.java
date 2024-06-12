package com.example.myapplication.gameobjects;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.utils.RotatedVectorDrawable;

import java.util.ArrayList;
import java.util.List;

public class Bridge extends GameObject implements Connectable {

    private List<Connectable> vertical;
    private List<Connectable> horizontal;


    public Bridge(int x, int y, View view) {
        super(x, y, view);
        vertical = new ArrayList<>();
        horizontal = new ArrayList<>();
        updateTexture();
    }

    @Override
    @SuppressLint("UseCompatLoadingForDrawables")
    public void updateTexture() {
        Drawable[] layers = new Drawable[4];
        layers[0] = view.getContext().getDrawable(R.drawable.grid_item_background);
        layers[1] = view.getContext().getDrawable(R.drawable.empty_bridge);
        if (!vertical.isEmpty()) {
            if (vertical.size() == 1) {
                if (getConnectedPositionType(vertical.get(0)) == 1) {
                    layers[2] = view.getContext().getDrawable(R.drawable.under_way_top);
                } else {
                    layers[2] = view.getContext().getDrawable(R.drawable.under_way_down);
                }
            } else {
                Drawable[] underway = new Drawable[2];
                underway[0] = view.getContext().getDrawable(R.drawable.under_way_top);
                underway[1] = view.getContext().getDrawable(R.drawable.under_way_down);
                layers[2] = new LayerDrawable(underway);
            }

        }
        if (!horizontal.isEmpty()) {
            if (horizontal.size() == 1) {
                layers[3] = new RotatedVectorDrawable((VectorDrawable) view.getContext().getDrawable(R.drawable.way), 90 * getConnectedPositionType(horizontal.get(0)));
            } else {
                layers[3] = view.getContext().getDrawable(R.drawable.completed_way);
            }
        }
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        view.setBackground(layerDrawable);
    }

    @Override
    public boolean canConnect(Connectable to) {
        int postype = getConnectedPositionType(to);
        if (postype == 0 || postype == 2) {
            if (horizontal.size() >= 2) return false;
            if (horizontal.isEmpty()) {
                return vertical.size() != 1;
            } else {
                return  vertical.size() != 1;
            }
        } else {
            if (vertical.size() >= 2) return false;
            if (vertical.isEmpty()) {
                return horizontal.size() != 1;
            } else {
                return horizontal.size() != 1;
            }
        }

    }

    @Override
    public void connect(Connectable to) throws Exception {
        int postype = getConnectedPositionType(to);
        if (postype == 0 || postype == 2) {
            horizontal.add(to);
        } else {
            vertical.add(to);
        }
        updateTexture();
    }

    @Override
    public void clearConnections() {
        vertical.clear();
        horizontal.clear();
        updateTexture();
    }

    @Override
    public int getPathsCount() {
        int paths = 0;
        if (vertical.size() == 2) paths++;
        if (horizontal.size() == 2) paths++;
        return paths;
    }



}