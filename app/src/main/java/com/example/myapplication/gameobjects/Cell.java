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

public class Cell extends GameObject implements Connectable {

    protected List<Connectable> connections;

    private int color = -1;

    public Cell(int x, int y, View view) {
        super(x, y, view);
        connections = new ArrayList<>();
        updateTexture();
    }

    public int getColor(int posType) {
        return color;
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
        Log.d("Connect", "cell");
        updateTexture();
    }

    public void setColor(int color, int posType) {
        this.color = color;
    }

    private int getMaxConnections() {
        return 2;
    }

    @Override
    public void updateTexture() {
        Drawable[] layers = new Drawable[2];
        layers[0] = view.getContext().getDrawable(R.drawable.grid_item_background);
        if (connections.size() == 1) {
            VectorDrawable vectorDrawable = (VectorDrawable) view.getContext().getDrawable(R.drawable.way);
            vectorDrawable.setTint(color);
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
                vectorDrawable.setTint(color);
                int rotate = 0;
                if (postype1 == 0 && postype2 == 1)
                    rotate = 0; // сверху вниз
                if (postype1 == 1 && postype2 == 2)
                    rotate = 1; // справа вниз
                if (postype1 == 2 && postype2 == 3)
                    rotate = 2; // снизу вверх
                if (postype1 == 0 && postype2 == 3)
                    rotate = 3; // слева вверх
                layers[1] = new RotatedVectorDrawable(vectorDrawable, 90 * rotate);
            } else {
                VectorDrawable vectorDrawable = (VectorDrawable) view.getContext().getDrawable(R.drawable.completed_way);
                vectorDrawable.setTint(color);
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
        color = -1;
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
