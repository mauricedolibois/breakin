package com.innoveworkshop.gametest.levels;

import android.graphics.Color;

import com.innoveworkshop.gametest.assets.BouncingBall;
import com.innoveworkshop.gametest.assets.Brick;
import com.innoveworkshop.gametest.assets.Paddle;
import com.innoveworkshop.gametest.engine.GameSurface;
import com.innoveworkshop.gametest.engine.Vector;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelManager {
    private LevelLoader levelLoader;
    private int currentLevel;
    private Paddle paddle;
    private List<BouncingBall> balls;

    public LevelManager(InputStream levelFile) throws Exception {
        this.levelLoader = new LevelLoader(levelFile);
        this.currentLevel = 0;
        this.balls = new ArrayList<>();
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public List<BouncingBall> getBalls() {
        return balls;
    }

    public void loadLevel(GameSurface surface) {
        Map<String, Integer> metadata = levelLoader.getMetadata(currentLevel);
        int paddleWidth = metadata.getOrDefault("PaddleSize", 200);
        float ballSpeed = metadata.getOrDefault("BallSpeed", 20);
        float dropRate = metadata.getOrDefault("DropChance", 100);

        paddle = new Paddle(new Vector(surface.getWidth() / 2, surface.getHeight() * 0.90f), paddleWidth, 20, Color.LTGRAY);
        balls.clear();
        addBall(surface.getWidth() / 2, surface.getHeight() * 0.88f, 20, Color.WHITE, ballSpeed);

        // Load level layout
        String[] layout = levelLoader.getLevel(currentLevel);
        int numRows = layout.length;
        int numCols = layout[0].length();
        float brickWidth = surface.getWidth() / 10; // Fixed width for bricks
        float brickHeight = 50; // Fixed height for bricks
        float xOffset = (surface.getWidth() - (numCols * brickWidth)) / 2; // Center horizontally
        float yOffset = 100; // Adjust starting Y position

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                char cell = layout[row].charAt(col);
                int health = Character.getNumericValue(cell);
                if (health > 0 && health <= 4) {
                    Vector position = new Vector(
                            col * brickWidth + xOffset + brickWidth / 2,
                            row * brickHeight + yOffset + brickHeight / 2
                    );

                    // Create and add brick
                    Brick brick = new Brick(position, brickWidth, brickHeight, health, dropRate, balls, paddle);
                    surface.addGameObject(brick);
                }
            }
        }
        surface.addGameObject(paddle);
        for (BouncingBall ball : balls) {
            surface.addGameObject(ball);
        }
    }

    public void nextLevel(GameSurface surface) {
        surface.clearGameObjects();
        if (currentLevel < levelLoader.getLevelCount() - 1) {
            currentLevel++;
            loadLevel(surface);
        } else {
            System.out.println("No more levels!");
        }
    }

    public void reloadLevel(GameSurface surface) {
        loadLevel(surface);
    }

    private void addBall(float x, float y, float radius, int color, float speed) {
        BouncingBall newBall = new BouncingBall(x, y, radius, color, speed, paddle);
        balls.add(newBall);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
