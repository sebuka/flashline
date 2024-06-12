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

public class Point extends GameObject implements Connectable {
    protected List<Connectable> connections;

    public Point(int x, int y,  View view) {
        super(x, y, view);
        connections = new ArrayList<>();
        updateTexture();
    }


    @Override
    public boolean canConnect(Connectable to) {
        if (connections.size() >= getMaxConnections()) {
            return false;
        }
        if (connections.contains(to))
            return false;

        return true;
    }

    @Override
    public void connect(Connectable to) throws Exception {
        connections.add(to);
        updateTexture();
    }

    private int getMaxConnections() {
        return 1;
    }

    @Override
    @SuppressLint("UseCompatLoadingForDrawables")
    public void updateTexture() {
        Drawable[] layers = new Drawable[2];
        layers[0] = view.getContext().getDrawable(R.drawable.grid_item_background);
        if (connections.isEmpty()) {
            layers[1] = view.getContext().getDrawable(R.drawable.point);
        } else {
            int rotationAngle = 90 * getConnectedPositionType(connections.get(0));
            VectorDrawable vectorDrawable = (VectorDrawable) view.getContext().getDrawable(R.drawable.connected_point);
            layers[1] = new RotatedVectorDrawable(vectorDrawable, rotationAngle);
        }
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        view.setBackground(layerDrawable);
    }

    public List<Connectable> getConnections() {
        return connections;
    }


    public void clearConnections() {
        connections.clear();
        updateTexture();
    }

    @Override
    public int getPathsCount() {
        return 0;
    }
}
