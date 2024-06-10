package com.example.myapplication.gameobjects;

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
    private int color;
    protected List<Connectable> connections;

    public Point(int x, int y, int color, View view) {
        super(x, y, view);
        this.color = color;
        connections = new ArrayList<>();
        updateTexture();
    }

    public int getColor(int posType) {
        return color;
    }

    @Override
    public void setColor(int color, int posType) {
        this.color = color;
    }

    @Override
    public boolean canConnect(Connectable to) {
        if (connections.size() >= getMaxConnections()) {
            return false;
        }
        if (connections.contains(to))
            return false;

        return this.color == to.getColor(0) || this.color == -1 || to.getColor(0) == -1;
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
    public void updateTexture() {
        Drawable[] layers = new Drawable[2];
        layers[0] = view.getContext().getDrawable(R.drawable.grid_item_background);
        if (connections.size() == 0) {
            layers[1] = view.getContext().getDrawable(R.drawable.point);
            layers[1].setTint(color);
        } else {
            int rotationAngle = 90 * getConnectedPositionType(connections.get(0));
            VectorDrawable vectorDrawable = (VectorDrawable) view.getContext().getDrawable(R.drawable.connected_point);
            vectorDrawable.setTint(color);
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
