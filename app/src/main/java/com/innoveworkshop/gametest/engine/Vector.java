package com.innoveworkshop.gametest.engine;

public class Vector {
    public float x;
    public float y;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector subtract(Vector clickedVector) {
        return new Vector(this.x - clickedVector.x, this.y - clickedVector.y);
    }

    public void add(Vector delta) {
        this.x += delta.x;
        this.y += delta.y;
    }
}
