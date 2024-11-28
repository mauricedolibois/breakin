package com.innoveworkshop.gametest.assets;

import com.innoveworkshop.gametest.engine.Rectangle;
import com.innoveworkshop.gametest.engine.Vector;

public class Paddle extends Rectangle {

    public Paddle(Vector position, float width, float height, int color) {
        super(position, width, height, color);
    }

    public void setPosition(Vector position) {
        this.position = position;
    }


    @Override
    public void onFixedUpdate() {
        super.onFixedUpdate();

    }
}
