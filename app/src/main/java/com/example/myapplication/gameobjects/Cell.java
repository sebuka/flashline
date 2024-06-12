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

public class Cell extends GameObject implements Connectable {

    protected List<Connectable> connections;

    public Cell(int x, int y, View view) {
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
        Log.d("Connect", "cell");
        updateTexture();
    }


    private int getMaxConnections() {
        return 2;
    }

    @Override
    @SuppressLint("UseCompatLoadingForDrawables")
    public void updateTexture() {
        Drawable[] layers = new Drawable[2];
        layers[0] = view.getContext().getDrawable(R.drawable.grid_item_background);
        if (connections.size() == 1) {
            VectorDrawable vectorDrawable = (VectorDrawable) view.getContext().getDrawable(R.drawable.way);
            int rotationAngle = 90 * getConnectedPositionType(connections.get(0));
            layers[1] = new RotatedVectorDrawable(vectorDrawable, rotationAngle);
        } else if (connections.size() >= 2) {
            int postype1 = getConnectedPositionType(connections.get(0));
            int postype2 = getConnectedPositionType(connections.get(1));
            Log.d("Cell", "Connections: " + postype1 + ", " + postype2);
            if (postype1 > postype2) {
                int tmp = postype1;
                postype1 = postype2;
                postype2 = tmp;
            }
            if (Math.abs(postype1 - postype2) != 2) {
                VectorDrawable vectorDrawable = (VectorDrawable) view.getContext().getDrawable(R.drawable.corner_way);
                int rotate = 0;
                if (postype1 == 0 && postype2 == 1)
                    rotate = 0;
                if (postype1 == 1 && postype2 == 2)
                    rotate = 1;
                if (postype1 == 2 && postype2 == 3)
                    rotate = 2;
                if (postype1 == 0 && postype2 == 3)
                    rotate = 3;
                layers[1] = new RotatedVectorDrawable(vectorDrawable, 90 * rotate);
            } else {
                VectorDrawable vectorDrawable = (VectorDrawable) view.getContext().getDrawable(R.drawable.completed_way);
                int rotate = 0;
                if (postype1 == 1 && postype2 == 3)
                    rotate = 1; // справа вниз
                layers[1] = new RotatedVectorDrawable(vectorDrawable, 90 * rotate);
            }
        }
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        view.setBackground(layerDrawable);
    }

    public void clearConnections() {
        connections.clear();
        updateTexture();
    }

    @Override
    public int getPathsCount() {
        if (connections.size() == 2) {
            return 1;
        }
        return 0;
    }
}
