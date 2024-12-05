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
        Vector nextPosition = new Vector(position.x + direction.x * speed, position.y + direction.y * speed);

        // Handle paddle collision along the path
        checkPaddleCollision(position, nextPosition);

        // Update the ball's position
        position = nextPosition;
    }

    public float getSpeed() {
        return speed;
    }

    private void bounce() {
        if (hitLeftWall()) {
            position.x = radius; // Set position a little bit outside the left wall
            direction.x *= -1;
        }
        if (hitRightWall()) {
            position.x = gameSurface.getWidth() - radius; // Set position a little bit outside the right wall
            direction.x *= -1;
        }
        if (hitTopWall()) {
            direction.y *= -1;
        }
        if (isFloored()) {
            destroyed = true;
            destroy();
        }
    }

    private void checkPaddleCollision(Vector currentPosition, Vector nextPosition) {
        // Check if the ball crosses the paddle's vertical range
        if (currentPosition.y + radius <= paddle.getPosition().y &&
                nextPosition.y + radius >= paddle.getPosition().y &&
                nextPosition.x + radius >= paddle.getPosition().x - paddle.getWidth() / 2 &&
                nextPosition.x - radius <= paddle.getPosition().x + paddle.getWidth() / 2) {

            // Ball is crossing the paddle
            // Calculate the paddle's center
            float paddleCenterX = paddle.getPosition().x;

            // Calculate the horizontal distance from the paddle's center
            float distanceFromCenter = nextPosition.x - paddleCenterX;

            // Normalize this distance to adjust the x-direction
            float normalizedDistance = distanceFromCenter / (paddle.getWidth() / 2);

            // Update the ball's direction vector
            direction.x = normalizedDistance; // X direction depends on hit position
            direction.y = -1; // Always move upward after hitting the paddle

            // Normalize the direction vector to maintain consistent speed
            float magnitude = (float) Math.sqrt(direction.x * direction.x + direction.y * direction.y);
            direction.x /= magnitude;
            direction.y /= magnitude;

            // Set position a little bit above the paddle
            position.y = paddle.getPosition().y - paddle.height / 2 - radius;
        }
    }

    @Override
    public void onFixedUpdate() {
        super.onFixedUpdate();
        bounce();
        move();
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setPosition(Vector vector) {
        position = vector;
    }
}
