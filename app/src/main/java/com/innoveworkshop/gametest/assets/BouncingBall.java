package com.innoveworkshop.gametest.assets;

import com.innoveworkshop.gametest.engine.Circle;
import com.innoveworkshop.gametest.engine.Vector;

public class BouncingBall extends Circle {
    private final Paddle paddle; // Store the paddle reference
    private float speed;
    private Vector direction = new Vector((float) (Math.random() * 0.6 - 0.3), 1f); // Random initial direction
    public BouncingBall(float x, float y, float radius, int color, float speed, Paddle paddle) {
        super(x, y, radius, color);
        this.speed = speed;
        this.paddle = paddle; // Assign the paddle
    }

    private void move() {
        position.x += direction.x * speed;
        position.y += direction.y * speed;
    }

    private void bounce() {
        if (hitLeftWall() || hitRightWall()) {
            direction.x *= -1;
        }
        if (hitTopWall()) {
            direction.y *= -1;
        }
        if (isFloored()) {
            destroyed = true;
        }
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public Vector getDirection() {
        return direction;
    }

    private void hitPaddle() {
        // Check if the ball is within the vertical range of the paddle
        if (position.y + radius >= paddle.getPosition().y && // Ball is at or below the paddle's top
                position.y + radius <= paddle.getPosition().y + paddle.getHeight() && // Ball is not below the paddle
                position.x + radius >= paddle.getPosition().x - paddle.getWidth()/2&& // Ball is at or to the right of the paddle's left edge
                position.x - radius <= paddle.getPosition().x + paddle.getWidth()/2) { // Ball is at or to the left of the paddle's right edge

            // Ball has hit the paddle on top
            // Calculate the paddle's center
            float paddleCenterX = paddle.getPosition().x;

            // Calculate the horizontal distance from the paddle's center
            float distanceFromCenter = position.x - paddleCenterX;

            // Normalize this distance to adjust the x-direction
            float normalizedDistance = distanceFromCenter / (paddle.getWidth() / 2);

            // Update the ball's direction vector
            direction.x = normalizedDistance; // X direction depends on hit position
            direction.y = -1; // Always move upward after hitting the paddle

            // Normalize the direction vector to maintain consistent speed
            float magnitude = (float) Math.sqrt(direction.x * direction.x + direction.y * direction.y);
            direction.x /= magnitude;
            direction.y /= magnitude;

            // Optionally adjust ball speed or add effects if needed
        }
    }

    @Override
    public void onFixedUpdate() {
        super.onFixedUpdate();
        bounce();
        hitPaddle();
        move();
    }

    public void setPosition(Vector vector) {
        position = vector;
    }
}
