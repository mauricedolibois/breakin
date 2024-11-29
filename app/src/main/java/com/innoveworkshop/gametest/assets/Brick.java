package com.innoveworkshop.gametest.assets;

import android.graphics.Color;

import com.innoveworkshop.gametest.engine.Rectangle;
import com.innoveworkshop.gametest.engine.Vector;

import java.util.List;
import java.util.Random;

public class Brick extends Rectangle {
    private int health;
    private List<BouncingBall> balls;
    private Paddle paddle;
    private int color;
    private boolean isDestroyed = false;
    private float dropRate;

    public Brick(Vector position, float width, float height, int health,float dropRate, List<BouncingBall> balls, Paddle paddle) {
        super(position, width, height, 0);
        this.health = health;
        this.balls = balls;
        this.paddle = paddle;
        this.color = generateRandomColor();
        this.dropRate = dropRate;
        // Darken color based on health
        this.color = darkenColor(color, 50f * health);
        setColor(this.color); // Update paint color in Rectangle
    }

    private static int generateRandomColor() {
        Random random = new Random();

        // Define neon hue ranges: [Bright pink, Electric blue, Lime green, Vivid orange, Bright purple]
        float[] neonHues = {330, 220, 120, 40, 280};
        float hue = neonHues[random.nextInt(neonHues.length)]; // Randomly select a neon hue

        float saturation = 1.0f; // Maximum saturation for vibrant neon effect
        float brightness = 1.0f; // Maximum brightness for neon glow

        return Color.HSVToColor(new float[]{hue, saturation, brightness});
    }

    private int darkenColor(int color, float factor) {
        // Extract color components
        int a = (color >> 24) & 0xff; // Alpha
        int r = (color >> 16) & 0xff; // Red
        int g = (color >> 8) & 0xff;  // Green
        int b = color & 0xff;         // Blue

        // Apply darkening factor
        r = Math.max((int) (r - factor), 0);
        g = Math.max((int) (g - factor), 0);
        b = Math.max((int) (b - factor), 0);

        // Reconstruct color
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public void hit() {
        health--;
        if (health > 0) {
            // Update color when health decreases
            this.color = darkenColor(color, -50f);
            setColor(this.color); // Update paint color in Rectangle
        } else {
            isDestroyed = true; // Mark as destroyed
            powerupDrop();
            destroy(); // Destroy brick if health is zero or below
        }
    }

    private void powerupDrop() {
        if (Math.random() < dropRate/100) {
            DroppingPowerup powerup = new DroppingPowerup(position, 50, 50, 10, color, paddle);
            gameSurface.addGameObject(powerup);
        }
    }


    private void isColliding() {
        if (isDestroyed) return;

        for (BouncingBall ball : balls) {
            // Brick bounds
            float brickLeft = position.x - width / 2;
            float brickRight = position.x + width / 2;
            float brickTop = position.y - height / 2;
            float brickBottom = position.y + height / 2;

            // Ball center
            Vector ballCenter = ball.position;
            float radius = ball.radius;

            // Closest point on the brick to the ball's center
            float closestX = clamp(ballCenter.x, brickLeft, brickRight);
            float closestY = clamp(ballCenter.y, brickTop, brickBottom);

            // Calculate the distance from the ball's center to the closest point
            float distanceX = ballCenter.x - closestX;
            float distanceY = ballCenter.y - closestY;
            float distanceSquared = distanceX * distanceX + distanceY * distanceY;

            // Check for collision (distance between circle's center and closest point <= radius)
            if (distanceSquared <= radius * radius) {
                Vector ballDir = ball.getDirection();

                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    handleHorizontalCollision(ball, ballCenter, ballDir, radius, distanceX);
                } else {
                    handleVerticalCollision(ball, ballCenter, ballDir, radius, distanceY);
                }
                hit();
                break; // Stop checking after the first collision
            }
        }
    }

    private void handleHorizontalCollision(BouncingBall ball, Vector ballCenter, Vector ballDir, float radius, float distanceX) {
        // Set ball position based on horizontal collision
        ball.setPosition(new Vector(
                ballCenter.x + (distanceX > 0 ? radius : -radius),
                ballCenter.y
        ));
        ball.setDirection(new Vector(-ballDir.x, ballDir.y));
    }

    private void handleVerticalCollision(BouncingBall ball, Vector ballCenter, Vector ballDir, float radius, float distanceY) {
        // Set ball position based on vertical collision
        ball.setPosition(new Vector(
                ballCenter.x,
                ballCenter.y + (distanceY > 0 ? radius : -radius)
        ));
        ball.setDirection(new Vector(ballDir.x, -ballDir.y));
    }

    private float clamp(float value, float min, float max) {
        // Clamp value to be within the given range
        return Math.max(min, Math.min(value, max));
    }

    @Override
    public void onFixedUpdate() {
        super.onFixedUpdate();
        if (!isDestroyed) {
            isColliding();
        }
    }
}
