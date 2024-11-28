package com.innoveworkshop.gametest.engine;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Sprite {
    Bitmap bmp;
    float x;
    float y;

    public Sprite(Bitmap bmp, float x, float y) {
        this.bmp = bmp;
        this.x = x;
        this.y = y;
    }

    public Sprite(int resId, float x, float y) {
        this(BitmapFactory.decodeResource(Resources.getSystem(), resId), x, y);
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmp, x, y, null);
    }
}
