package com.innoveworkshop.gametest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.innoveworkshop.gametest.assets.BouncingBall;
import com.innoveworkshop.gametest.assets.Brick;
import com.innoveworkshop.gametest.assets.DroppingPowerup;
import com.innoveworkshop.gametest.manager.LevelLoader;
import com.innoveworkshop.gametest.assets.Paddle;
import com.innoveworkshop.gametest.engine.GameObject;
import com.innoveworkshop.gametest.engine.GameSurface;
import com.innoveworkshop.gametest.engine.Vector;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    protected GameSurface gameSurface;
    protected Game game;
    private TextView levelText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameSurface = findViewById(R.id.gameSurface);
        levelText = findViewById(R.id.levelText); // Reference to the TextView

        try {
            InputStream levelFile = getResources().openRawResource(R.raw.levels); // Assuming levels.txt is placed in res/raw
            game = new Game(levelFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameSurface.setRootGameObject(game);
    }

    class Game extends GameObject {
        private LevelLoader levelLoader;
        private int currentLevel;
        private Paddle paddle;
        private List<BouncingBall> balls;

        public Game(InputStream levelFile) throws Exception {
            levelLoader = new LevelLoader(levelFile);
            currentLevel = 0;
            balls = new ArrayList<>();
        }

        @Override
        public void onStart(GameSurface surface) {
            super.onStart(surface);
            updateLevelText(); // Update level text when game starts

            // Add paddle
            paddle = new Paddle(new Vector(surface.getWidth() / 2, surface.getHeight() * 0.90f), 200, 20, Color.LTGRAY);

            // Add initial balls
            addBall(surface.getWidth() / 2, surface.getHeight() * 0.88f, 20, Color.WHITE, 20f);

            // Load initial level
            loadLevel(surface);
        }

        private void loadLevel(GameSurface surface) {
            updateLevelText(); // Update level text whenever a new level is loaded

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
                        Brick brick = new Brick(position, brickWidth, brickHeight, health,dropRate, balls, paddle);
                        surface.addGameObject(brick);
                    }
                }
            }
            surface.addGameObject(paddle);
            for (BouncingBall ball : balls) {
                surface.addGameObject(ball);
            }
        }

        private void addBall(float x, float y, float radius, int color, float speed) {
            BouncingBall newBall = new BouncingBall(x, y, radius, color, speed, paddle);
            balls.add(newBall);
        }

        public void nextLevel(GameSurface surface) {
            gameSurface.clearGameObjects();
            if (currentLevel < levelLoader.getLevelCount() - 1) {
                currentLevel++;
                paddle = new Paddle(new Vector(surface.getWidth() / 2, surface.getHeight() * 0.90f), 200, 20, Color.LTGRAY);
                balls.clear();
                addBall(surface.getWidth() / 2, surface.getHeight() * 0.88f, 20, Color.WHITE, 20f);
                loadLevel(surface);
            } else {
                System.out.println("No more levels!");
            }
        }

        public void addOneBall(float x, float y, float radius, int color, float speed) {
            BouncingBall newBall = new BouncingBall(x, y, radius, color, speed, paddle);
            balls.add(newBall);
            if (gameSurface != null) {
                gameSurface.addGameObject(newBall);
            }
        }

        public void reloadLevel(GameSurface surface) {
            gameSurface.clearGameObjects();
            paddle = new Paddle(new Vector(surface.getWidth() / 2, surface.getHeight() * 0.90f), 200, 20, Color.LTGRAY);
            balls.clear();
            addBall(surface.getWidth() / 2, surface.getHeight() * 0.88f, 20, Color.WHITE, 20f);
            loadLevel(surface);
        }

        @Override
        public void onTouch(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                if (paddle != null) {
                    Vector rememberPosition = new Vector(paddle.getPosition().x, paddle.getPosition().y);
                    paddle.setPosition(new Vector(touchX, paddle.getPosition().y));
                    if (paddle.hitLeftWall() || paddle.hitRightWall()) {
                        paddle.setPosition(rememberPosition);
                    }
                }
            }
        }

        @Override
        public void onFixedUpdate() {
            super.onFixedUpdate();
            if (gameSurface.getGameObjects().stream().noneMatch(gameObject -> gameObject instanceof Brick)) {
                nextLevel(gameSurface); // Proceed to the next level
            }
            if (balls.stream().allMatch(BouncingBall::isDestroyed)) {
                reloadLevel(gameSurface); // Reload the current level
            }
            // Check for instances of DroppingPowerup and handle them
            List<GameObject> powerups = new ArrayList<>(
                    gameSurface.getGameObjects().stream()
                            .filter(gameObject -> gameObject instanceof DroppingPowerup)
                            .collect(Collectors.toList())
            );

            // Process each DroppingPowerup instance
            for (GameObject powerup : powerups) {
                DroppingPowerup droppingPowerup = (DroppingPowerup) powerup;
                if (droppingPowerup.isCollected()) { // Assuming isCollected() method exists to check if collected
                    addOneBall(
                            droppingPowerup.getPosition().x,
                            droppingPowerup.getPosition().y - 20,
                            20,
                            Color.WHITE,
                            balls.get(0).getSpeed()
                    );
                    gameSurface.removeGameObject(droppingPowerup); // Remove the powerup from the game
                }
            }

        }

        private void updateLevelText() {
            runOnUiThread(() -> levelText.setText("Level " + (currentLevel + 1))); // Update level text on UI thread
        }
    }
}