package com.innoveworkshop.gametest;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
import com.innoveworkshop.gametest.assets.BouncingBall;
import com.innoveworkshop.gametest.assets.Brick;
import com.innoveworkshop.gametest.assets.DroppingPowerup;
import com.innoveworkshop.gametest.assets.Paddle;
import com.innoveworkshop.gametest.engine.GameObject;
import com.innoveworkshop.gametest.engine.GameSurface;
import com.innoveworkshop.gametest.engine.Vector;
import com.innoveworkshop.gametest.levels.LevelManager;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private GameSurface gameSurface;
    private Game game;
    private TextView levelText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameSurface = findViewById(R.id.gameSurface);
        levelText = findViewById(R.id.levelText);

        try {
            InputStream levelFile = getResources().openRawResource(R.raw.levels);
            game = new Game(levelFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameSurface.setRootGameObject(game);
    }

    class Game extends GameObject {
        private LevelManager levelManager;

        public Game(InputStream levelFile) throws Exception {
            levelManager = new LevelManager(levelFile);
        }

        @Override
        public void onStart(GameSurface surface) {
            super.onStart(surface);
            updateLevelText();
            levelManager.loadLevel(surface);
        }

        @Override
        public void onTouch(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                Paddle paddle = levelManager.getPaddle();
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
                levelManager.nextLevel(gameSurface);
                updateLevelText();
            }

            if (levelManager.getBalls().stream().allMatch(BouncingBall::isDestroyed)) {
                levelManager.reloadLevel(gameSurface);
            }

            List<GameObject> powerups = gameSurface.getGameObjects().stream()
                    .filter(gameObject -> gameObject instanceof DroppingPowerup)
                    .collect(Collectors.toList());

            for (GameObject powerup : powerups) {
                DroppingPowerup droppingPowerup = (DroppingPowerup) powerup;
                if (droppingPowerup.isCollected()) {
                    spawnBallsAtEachExistingBall();
                    gameSurface.removeGameObject(droppingPowerup);
                }
            }
        }

        private void spawnBallsAtEachExistingBall() {
            List<BouncingBall> existingBalls = levelManager.getBalls();
            Random random = new Random();

            for (BouncingBall ball : new ArrayList<>(existingBalls)) {
                float x = ball.getPosition().x;
                float y = ball.getPosition().y;
                float radius = ball.radius;
                int color = Color.WHITE;
                float speed = ball.getSpeed();

                if (gameSurface.getGameObjects().stream().filter(gameObject -> gameObject instanceof BouncingBall).count() > 30) {
                    return;
                }
                addOneBall(x, y, radius, color, speed);

                BouncingBall newBall = levelManager.getBalls().get(levelManager.getBalls().size() - 1);
                float randomAngle = random.nextFloat() * 360;
                float directionX = (float) Math.cos(Math.toRadians(randomAngle));
                Vector randomDirection = new Vector(directionX, ball.getDirection().y);
                newBall.setDirection(randomDirection);
            }
        }

        private void addOneBall(float x, float y, float radius, int color, float speed) {
            BouncingBall newBall = new BouncingBall(x, y, radius, color, speed, levelManager.getPaddle());
            levelManager.getBalls().add(newBall);
            gameSurface.addGameObject(newBall);
        }

        private void updateLevelText() {
            int currentLevel = levelManager.getCurrentLevel();
            runOnUiThread(() -> levelText.setText(currentLevel > 0 ? "Level " + currentLevel  : "No more Levels"));
        }
    }
}