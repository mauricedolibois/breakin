package com.innoveworkshop.gametest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;

import com.innoveworkshop.gametest.assets.BouncingBall;
import com.innoveworkshop.gametest.assets.Brick;
import com.innoveworkshop.gametest.levels.LevelLoader;
import com.innoveworkshop.gametest.assets.Paddle;
import com.innoveworkshop.gametest.engine.GameObject;
import com.innoveworkshop.gametest.engine.GameSurface;
import com.innoveworkshop.gametest.engine.Vector;

import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    protected GameSurface gameSurface;
    protected Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameSurface = findViewById(R.id.gameSurface);
        try {
            InputStream levelFile = getResources().openRawResource(R.raw.levels); // Assuming levels.txt is placed in res/raw
            game = new Game(levelFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameSurface.setRootGameObject(game);
    }

    class Game extends GameObject {
        private Paddle paddle;
        private LevelLoader levelLoader;
        private int currentLevel;

        public Game(InputStream levelFile) throws Exception {
            levelLoader = new LevelLoader(levelFile);
            currentLevel = 0;
        }

        @Override
        public void onStart(GameSurface surface) {
            super.onStart(surface);

            // Add paddle
            paddle = new Paddle(new Vector(surface.getWidth() / 2, surface.getHeight() * 0.90f), 200, 20, Color.LTGRAY);
            surface.addGameObject(paddle);

            // Add ball
            BouncingBall bouncingBall = new BouncingBall(surface.getWidth() / 2, surface.getHeight() * 0.80f, 20, Color.WHITE, 20f, paddle);
            surface.addGameObject(bouncingBall);

            // Load initial level
            loadLevel(surface, bouncingBall);
        }

        private void loadLevel(GameSurface surface, BouncingBall ball) {

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
                        // Calculate brick position
                        Vector position = new Vector(
                                col * brickWidth + xOffset + brickWidth / 2,
                                row * brickHeight + yOffset + brickHeight / 2
                        );

                        // Create and add brick
                        Brick brick = new Brick(position, brickWidth, brickHeight, Color.GREEN, health, ball);
                        surface.addGameObject(brick);
                    }
                }
            }
        }

        public void nextLevel(GameSurface surface, BouncingBall ball) {
            if (currentLevel < levelLoader.getLevelCount() - 1) {
                currentLevel++;
                loadLevel(surface, ball);
            } else {
                System.out.println("No more levels!");
            }
        }

        @Override
        public void onTouch(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                if (paddle != null ) {
                    Vector rememberposition= new Vector(paddle.getPosition().x, paddle.getPosition().y);
                    paddle.setPosition(new Vector(touchX, paddle.getPosition().y));
                    if (paddle.hitLeftWall() || paddle.hitRightWall())
                    {
                        paddle.setPosition(rememberposition);
                    }
                }
            }
        }

        @Override
        public void onFixedUpdate() {
            super.onFixedUpdate();
        }
    }
}
