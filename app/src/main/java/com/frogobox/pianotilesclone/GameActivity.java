package com.frogobox.pianotilesclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.frogobox.pianotilesclone.databinding.ActivityGameBinding;
import com.frogobox.pianotilesclone.BaseActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends BaseActivity<ActivityGameBinding> {

    private Timer timer = new Timer();
    private Handler handler = new Handler();

    private Vibrator vibe;
    private SharedPreferences sharedPreferences;
    private String difficultyName;

    private boolean paused = false;
    private boolean updateScore = false;
    private boolean gameRunning = false;

    private float tileWidth, tileHeight;
    private float speed = 17;
    private float backupSpeed = speed;

    private int difficulty;
    private int score = 0;
    private int lastSc = 0;
    private int childCount = 0;
    private int period = 225;

    private List<ImageView> imageViewList;
    private List<Float> availablePositions;

    private MediaPlayer b1, b2, b3, b4;

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private void initDifficulty() {
        if (difficulty == 0) {
            speed = 10;
            period = 350;
        } else if (difficulty == 1) {
            speed = 12;
            period = 300;
        } else {
            speed = 14;
            period = 250;
        }
        backupSpeed = speed;
    }

    private void increaseDifficulty() {
        if (score - lastSc > 25) {
            if (speed <= 17)
                speed += 1;
            else {
                period -= 5;
            }
            lastSc = score;
        }
    }

    @NonNull
    @Override
    public ActivityGameBinding setupViewBinding() {
        return ActivityGameBinding.inflate(getLayoutInflater());
    }

    @Override
    public void setupUI(@Nullable Bundle savedInstanceState) {
        tileHeight = getScreenWidth() / 2;
        tileWidth = getScreenWidth() / 4;
        vibe = (Vibrator) GameActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        sharedPreferences = getSharedPreferences("my_prefs", 0);

        switch (Integer.parseInt(sharedPreferences.getString("PDifficulty", "0"))) {
            case 0:
                difficultyName = "Easy";
                difficulty = 0;
                break;
            case 1:
                difficulty = 1;
                difficultyName = "Medium";
                break;
            case 2:
                difficultyName = "Hard";
                difficulty = 2;
                break;
        }

        if (gameRunning) {
            initGame();
        }

        binding.btnStart.setOnClickListener(v -> {
            startGame();
        });

    }

    public void initGame() {
        paused = false;
        score = 0;
        initDifficulty();
        imageViewList = new ArrayList<>();
        availablePositions = new ArrayList<>();
        b3 = MediaPlayer.create(getApplicationContext(), R.raw.b3);
        binding.tvScore.setText(String.valueOf(score));
        for (int i = 0; i < 4; i++) {
            availablePositions.add(tileHeight);
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    moveTiles();
                    if (updateScore) {
                        increaseDifficulty();
                        score += 1;
                        binding.tvScore.setText(String.valueOf(score));
                        updateScore = false;
                    }

                });
            }
        }, 0, 10);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (!paused)
                        pickNextTileToDraw();
                });
            }
        }, 0, period);

    }

    private void updateScoreList() {
        SharedPreferences sharedPrefs = getSharedPreferences("prefs", 0);
        sharedPreferences = getSharedPreferences("my_prefs", 0);
        String v = sharedPreferences.getString("PName", "Player") + "\n" + score;
        HashSet<String> mySet = new HashSet<String>(sharedPrefs.getStringSet(difficultyName, new HashSet<String>()));
        SharedPreferences.Editor editor = sharedPrefs.edit();
        mySet.add(v);
        editor.putStringSet(difficultyName, new HashSet<String>(mySet));
        editor.apply();
    }

    private void showStats() {
        updateScoreList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Results");
        String message = "Player:" + sharedPreferences.getString("PName", "Player") + "\n" + "Difficulty:" + difficultyName + "\nScore:" + score;
        builder.setMessage(message);
        builder.setPositiveButton("Ok", (dialog, which) -> {
        });

        AlertDialog alertDialog = builder.create();
        try {
            alertDialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    private void moveTiles() {
        if (!paused) {

            if (speed < backupSpeed) {
                speed += 0.05;
            }
            if (imageViewList != null) {

                boolean quit = false;
                for (ImageView imageView : imageViewList) {
                    imageView.setY(imageView.getY() + speed);
                    if (quit) {
                        imageViewList = new ArrayList<>();
                        break;
                    }

                    if (imageView.getY() > getScreenHeight()) {
                        if (imageView.getMinimumHeight() == 0) {
                            resetGame();
                            showStats();
                            quit = true;
                        }
                        binding.parentRelative.removeView(imageView);
                    }
                }
                for (int i = 0; i < 4; i++) {
                    availablePositions.set(i, availablePositions.get(i) + speed);
                }
            }
        }

    }

    private void pickNextTileToDraw() {
        Random rd = new Random();
        boolean run = true;
        while (run) {
            int rnd = Math.abs(rd.nextInt()) % 4;
            float f = rd.nextFloat();
            if (availablePositions.get(rnd) >= tileHeight + speed) {
                DrawTile(f > 0.25, rnd * tileWidth, 0);
                availablePositions.set(rnd, 0f);
                run = false;
            }
        }

    }

    private void DrawTile(boolean black, float x, float y) {

        ImageView imageView = new ImageView(getApplicationContext());
        addView(imageView, (int) tileWidth, (int) tileHeight);
        imageView.setX(x);
        imageView.setY(-2 * tileHeight);
        if (black) {
            imageView.setBackgroundColor(Color.BLACK);
            imageView.setMinimumHeight(0);
        } else {
            imageView.setBackgroundColor(Color.WHITE);
            imageView.setMinimumHeight(1);
        }
        enableListeners(imageView, black);
        imageViewList.add(imageView);
        childCount += 1;

    }

    private void enableListeners(ImageView imageView, boolean black) {

        imageView.setOnClickListener(v -> {
            onTileClick(v, black);
        });

        imageView.setOnLongClickListener(v -> {
            onTileClick(v, black);
            return false;
        });

        imageView.setOnGenericMotionListener((v, event) -> {
            onTileClick(v, black);
            return false;
        });

        imageView.setOnHoverListener((v, event) -> {
            onTileClick(v, black);
            return false;
        });

        imageView.setOnDragListener((v, event) -> {
            onTileClick(v, black);
            return false;
        });

    }

    private boolean checkIfVibrations() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", 0);
        return Integer.parseInt(sharedPreferences.getString("PVibrations", "0")) == 1;
    }

    private boolean checkIfSounds() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", 0);
        return Integer.parseInt(sharedPreferences.getString("PSound", "0")) == 1;
    }

    private void onTileClick(View view, boolean black) {
        if (!paused) {
            if (black) {
                binding.parentRelative.removeView(view);
                imageViewList.remove(view);
                if (checkIfVibrations())
                    vibe.vibrate(25);
                updateScore = true;
                if (checkIfSounds()) {
                    b3.start();
                }
            } else {
                resetGame();
                showStats();
            }
        }
    }

    private void addView(ImageView imageView, int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(0, 0, 0, 0);
        imageView.setLayoutParams(params);
        binding.parentRelative.addView(imageView);
    }

    public void pauseGame(MenuItem item) {
        if (!paused) {
            paused = true;
            speed = 10;
        } else {
            paused = false;
        }
    }

    public void resetGame() {
        timer.cancel();
        timer.purge();
        timer = new Timer();
        for (ImageView imageView : imageViewList) {
            binding.parentRelative.removeView(imageView);
        }
        binding.btnStart.setVisibility(View.VISIBLE);
    }

    public void startGame() {
        initGame();
        binding.btnStart.setVisibility(View.GONE);
    }

}