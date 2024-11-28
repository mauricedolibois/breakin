package com.innoveworkshop.gametest.engine;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceView;

public abstract class GameObject {
    public Vector position;

    protected GameSurface gameSurface = null;
    protected boolean destroyed = false;

    public GameObject() {
        this(0, 0);
    }

    public GameObject(Vector position) {
        this.position = position;
    }

    public GameObject(float x, float y) {
        position = new Vector(x, y);
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public void destroy() {
        gameSurface.removeGameObject(this);
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void onStart(GameSurface surface) {
        this.gameSurface = surface;
    }

    public void onFixedUpdate() {
        if (destroyed) {
            setPosition(-100, -100);
        }
    }

    public Vector getPosition() {
        return position;
    }

    public void onDraw(Canvas canvas) {}

    public void onTouch(MotionEvent event) {
    }

}
