package com.innoveworkshop.gametest.assets;

import com.innoveworkshop.gametest.engine.Rectangle;
import com.innoveworkshop.gametest.engine.Vector;

public class DroppingRectangle extends Rectangle {
    float dropRate = 0;

    public DroppingRectangle(Vector position, float width, float height, float dropRate, int color) {
        super(position, width, height, color);
        this.dropRate = dropRate;
    }

    @Override
    public void onFixedUpdate() {
        super.onFixedUpdate();

        if (!isFloored())
            position.y += dropRate;
    }
}
