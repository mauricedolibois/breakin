package com.innoveworkshop.gametest.assets;

import com.innoveworkshop.gametest.engine.Rectangle;
import com.innoveworkshop.gametest.engine.Vector;

public class Brick extends Rectangle {
    private int health;
    private BouncingBall ball;
    private int color;

    public Brick(Vector position, float width, float height, int color, int health, BouncingBall ball) {
        super(position, width, height, color);
        this.health = health;
        this.ball = ball;
        // Darken color based on health
        this.color = darkenColor(color, 30f * health);
        setColor(this.color); // Update paint color in Rectangle
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
            this.color = darkenColor(color, -30f);
            setColor(this.color); // Update paint color in Rectangle
        } else {
            destroy(); // Destroy brick if health is zero or below
        }
    }

    private void isColliding() {
        // Brick bounds
        float brickLeft = position.x - width / 2;
        float brickRight = position.x + width / 2;
        float brickTop = position.y - height / 2;
        float brickBottom = position.y + height / 2;

        // Ball bounds
        float ballLeft = ball.position.x - ball.radius;
        float ballRight = ball.position.x + ball.radius;
        float ballTop = ball.position.y - ball.radius;
        float ballBottom = ball.position.y + ball.radius;

        // Check if ball intersects brick
        if(ballRight > brickLeft && ballLeft < brickRight && ballBottom > brickTop && ballTop < brickBottom) {

            Vector ballDir = ball.getDirection();

            float overlapLeft = ballRight - brickLeft;     // Distance from ball's right to rectangle's left
            float overlapRight = brickRight - ballLeft;    // Distance from ball's left to rectangle's right
            float overlapTop = ballBottom - brickTop;      // Distance from ball's bottom to rectangle's top
            float overlapBottom = brickBottom - ballTop;   // Distance from ball's top to rectangle's bottom

            float verticalhit= Math.min(overlapTop, overlapBottom);
            float horizontalhit= Math.min(overlapLeft, overlapRight);
            if (horizontalhit <= verticalhit)
            {
                ball.setPosition(new Vector(overlapRight<overlapLeft ? brickRight+ball.radius : brickLeft-ball.radius, ball.position.y));
                ball.setDirection(new Vector(-ballDir.x, ballDir.y));
            }
            else
            {
                ball.setPosition(new Vector(ball.position.x,  overlapBottom<overlapTop ? brickBottom+ball.radius : brickTop-ball.radius));
                ball.setDirection(new Vector(ballDir.x, -ballDir.y));
            }
            hit();
        }
    }

    @Override
    public void onFixedUpdate() {
        super.onFixedUpdate();
        isColliding();
    }
}
