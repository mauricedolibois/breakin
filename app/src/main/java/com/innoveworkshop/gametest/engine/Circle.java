package com.innoveworkshop.gametest.engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Circle extends GameObject implements Caged {
    public float radius;
    public Paint paint;

    public Circle(float x, float y, float radius, int color) {
        super(x, y);
        this.radius = radius;

        // Set up the paint.
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(position.x, position.y, radius, paint);
    }

    @Override
    public boolean hitLeftWall() {
        return (position.x - radius) <= 0;
    }

    @Override
    public boolean hitRightWall() {
        return (position.x + radius) >= gameSurface.getWidth();
    }

    @Override
    public boolean isFloored() {
        return (position.y + radius) >= gameSurface.getHeight();
    }

    @Override
    public boolean hitTopWall() {
        return (position.y - radius) <= 0;
    }
}
