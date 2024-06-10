package com.example.myapplication.gameobjects;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Log;
import android.view.View;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected View view;

    public GameObject(int x, int y, View view) {
        this.x = x;
        this.y = y;
        this.view = view;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public View getView() {
        return view;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void updateTexture();

    protected void rotateImageView(float angle) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", angle);
        animator.setDuration(0);
        animator.start();
    }
    public int getConnectedPositionType(Connectable to) {
        GameObject object = (GameObject) to;
        int type = 0;
        if (object.x < x)
            type = 1; // левый поворот
        else if (object.y < y)
            type = 0; // верхний поворот
        else if (object.x > x)
            type = 3; // правый поворот
        else if (object.y > y)
            type = 2; // нижний поворот
        return type;
    }


}
