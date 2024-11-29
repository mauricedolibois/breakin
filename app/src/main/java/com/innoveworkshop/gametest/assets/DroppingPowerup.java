package com.innoveworkshop.gametest.assets;

import com.innoveworkshop.gametest.engine.Rectangle;
import com.innoveworkshop.gametest.engine.Vector;

public class DroppingPowerup extends Rectangle {
    float dropRate = 0;
    Paddle paddle;

    public DroppingPowerup(Vector position, float width, float height, float dropRate, int color, Paddle paddle) {
        super(position, width, height, color);
        this.dropRate = dropRate;
        this.paddle = paddle;
    }

    private boolean checkPaddleCollision(Paddle paddle) {
        // Get paddle bounds
        float paddleLeft = paddle.getPosition().x - paddle.getWidth() / 2;
        float paddleRight = paddle.getPosition().x + paddle.getWidth() / 2;
        float paddleTop = paddle.getPosition().y - paddle.getHeight() / 2;
        float paddleBottom = paddle.getPosition().y + paddle.getHeight() / 2;

        // Get power-up bounds
        float powerupLeft = position.x - width / 2;
        float powerupRight = position.x + width / 2;
        float powerupTop = position.y - height / 2;
        float powerupBottom = position.y + height / 2;

        // Check for collision: overlap in both horizontal and vertical ranges
        boolean isColliding = powerupRight > paddleLeft &&
                powerupLeft < paddleRight &&
                powerupBottom > paddleTop &&
                powerupTop < paddleBottom;

        return isColliding;
    }

    public boolean isCollected() {
        return checkPaddleCollision(paddle); // Logic to determine if the powerup was collected
    }

    @Override
    public void onFixedUpdate() {
        super.onFixedUpdate();

        if (!isFloored()) {
            position.y += dropRate;
        } else{
            destroy();
        }

    }
}
