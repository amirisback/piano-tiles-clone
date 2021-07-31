package com.frogobox.pianotilesclone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.myapplication.R;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private List<ImageView> imageViewList;
    private RelativeLayout relativeLayout;
    private float tileWidth, tileHeight;
    private float speed = 17;
    private int score = 0;
    private int lastSc = 0;
    private int childCount = 0;
    private List<Float> availablePositions;
    private Vibrator vibe;
    private ActionMenuItemView titleView;
    private boolean paused = false;
    private int period = 225;
    private boolean updateScore = false;
    private float backupSpeed = speed;
    private boolean gameRunning = false;
    private SharedPreferences sharedPreferences;
    private String difficultyName;
    private int difficulty;
    private MediaPlayer b1, b2, b3, b4;
    private ShareButton shareButton;
    private LoginButton loginButton;
    private boolean isLogged = false;

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

    private void shareOnFavebook() {

        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder().setContentUrl(Uri.parse("https://www.youtube.com/watch?v=5_kPo8muETo"))
                .setShareHashtag(new ShareHashtag.Builder().setHashtag("#WhiteTile").build()).
                        setQuote("I just scored " + score + " points on " + difficultyName + " difficulty").build();

        shareButton.setShareContent(shareLinkContent);
        shareButton.callOnClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        relativeLayout = findViewById(R.id.parentRelative);
        tileHeight = getScreenWidth() / 2;
        tileWidth = getScreenWidth() / 4;
        titleView = (ActionMenuItemView) findViewById(R.id.ScoreId);
        vibe = (Vibrator) GameActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        sharedPreferences = getSharedPreferences("my_prefs", 0);
        shareButton = findViewById(R.id.shareButtonFB);

        switch (Integer.valueOf(sharedPreferences.getString("PDifficulty", "0"))) {
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


    }

    public void initGame() {
        paused = false;
        score = 0;
        initDifficulty();
        imageViewList = new ArrayList<>();
        availablePositions = new ArrayList<>();
        b3 = MediaPlayer.create(getApplicationContext(), R.raw.b3);

        titleView = findViewById(R.id.ScoreId);
        String tempScore = "Score:" + score;
        titleView.setTitle(tempScore);


        for (int i = 0; i < 4; i++) {
            availablePositions.add(tileHeight);
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        moveTiles();
                        if (updateScore) {
                            increaseDifficulty();
                            titleView = findViewById(R.id.ScoreId);
                            score += 1;
                            titleView.setTitle("Score:" + score);
                            updateScore = false;
                        }

                    }
                });
            }
        }, 0, 10);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!paused)
                            pickNextTileToDraw();
                    }
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
        editor.commit();


    }

    private void showStats() {

        updateScoreList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Results");
        String message = "Player:" + sharedPreferences.getString("PName", "Player") + "\n" + "Difficulty:" + difficultyName + "\nScore:" + score;
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", 0);


        if (sharedPreferences.getBoolean("isLogged", false) == true) {
            builder.setNeutralButton("Share", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    shareOnFavebook();
                }
            });
        }

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
                        relativeLayout.removeView(imageView);


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
                DrawTile(f > 0.25 ? true : false, rnd * tileWidth, 0);
                availablePositions.set(rnd, 0f);
                run = false;
            }
        }


    }


    private void DrawTile(boolean black, float x, float y) {

        ImageView imageView = new ImageView(getApplicationContext());
        addvieW(imageView, (int) tileWidth, (int) tileHeight);
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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTileClick(v, black);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onTileClick(v, black);
                return false;
            }

        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTileClick(v, black);
                imageView.setOnTouchListener(null);
                return true;
            }
        });

        imageView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                onTileClick(v, black);
                return false;
            }
        });


        imageView.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                onTileClick(v, black);
                return false;
            }
        });


        imageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                onTileClick(v, black);
                return false;
            }
        });


    }

    private boolean checkIfVibrations() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", 0);
        if (Integer.valueOf(sharedPreferences.getString("PVibrations", "0")) == 1)
            return true;
        return false;
    }

    private boolean checkIfSounds() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", 0);
        if (Integer.valueOf(sharedPreferences.getString("PSound", "0")) == 1)
            return true;
        return false;
    }

    private void onTileClick(View view, boolean black) {
        if (!paused) {
            if (black) {

                relativeLayout.removeView(view);
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

    private void addvieW(ImageView imageView, int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);

        params.setMargins(0, 0, 0, 0);
        imageView.setLayoutParams(params);

        RelativeLayout relativeLayout = findViewById(R.id.parentRelative);
        relativeLayout.addView(imageView);
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

            relativeLayout.removeView(imageView);
        }
        Button beginButton = findViewById(R.id.beginButton);
        beginButton.setVisibility(View.VISIBLE);
    }

    public void startGame(View view) {
        initGame();
        Button beginButton = findViewById(R.id.beginButton);
        beginButton.setVisibility(View.GONE);

    }

}