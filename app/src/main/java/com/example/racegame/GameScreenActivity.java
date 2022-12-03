package com.example.racegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameScreenActivity extends AppCompatActivity {
    private ExtendedFloatingActionButton game_FAB_left;
    private ExtendedFloatingActionButton game_FAB_right;
    private MaterialTextView game_LBL_time;
    private LinearLayout game_LL_heartLayout;
    private ArrayList<ShapeableImageView> game_life_images;
    private LinearLayout game_LL_characterLayout;
    private ArrayList<ShapeableImageView> game_character_positions;
    //private LinearLayout game_LL_obstaclesLayout;
    private ArrayList<LinearLayout> allRoutesLayouts;
    private GameManager gm;
    private Vibrator v;
    private Toast t;
    private boolean isStopped = false;
    //private int summonObstacle = 1;
    private final int NUMBER_OF_POSITIONS = 3;
    //time variables
    private Timer timer;
    long startTime = 0;
    final int DELAY = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        gm = new GameManager();
        game_life_images = new ArrayList<ShapeableImageView>();
        game_character_positions = new ArrayList<ShapeableImageView>();
        allRoutesLayouts = new ArrayList<LinearLayout>();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        findViews();
        game_FAB_left.setOnClickListener(v -> moveLeft());
        game_FAB_right.setOnClickListener(v -> moveRight());
        initCharacterPositions();
        startTimer();
        playBackgroundMusic();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        v.cancel();
        isStopped = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStopped = false;
    }

    private void playBackgroundMusic() {
        MediaPlayer ring = MediaPlayer.create(this, R.raw.crowd);
        ring.setLooping(true);
        ring.start();
    }

    //this function initiates an arraylist with the image views of the main character
    //in the character layout, there are also views to make a space between the image views
    //so i don't want to include them
    private void initCharacterPositions() {
        int size = game_LL_characterLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            View v = game_LL_characterLayout.getChildAt(i);
            if (v instanceof ShapeableImageView) {
                game_character_positions.add((ShapeableImageView) v);
            }
        }
    }


    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    updateTimerUI();
                });
                createObstacle();
                //summonObstacle++;
            }
        }, DELAY, DELAY);
    }

    private void updateTimerUI() {
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        int hours = minutes / 60;
        hours %= 24;
        game_LBL_time.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void createObstacle() {
        //gm.getNumberOfObstaclesPosition() - 1 is the index of the last row of obstacles
        moveObstacles(gm.getNumberOfObstaclesPosition() - 1);
        initNewObstacle();
    }

    private void initNewObstacle() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (LinearLayout l : allRoutesLayouts) {
                    l.getChildAt(0).setVisibility(View.INVISIBLE);
                }
                int index = getRandomNum(0, NUMBER_OF_POSITIONS);
                allRoutesLayouts.get(index).getChildAt(0).setVisibility(View.VISIBLE);
            }
        });
    }

    private void moveObstacles(int numberOfObstaclesPosition) {
        while (numberOfObstaclesPosition > 0) {
            for (LinearLayout l : allRoutesLayouts)
                moveSingleRow(numberOfObstaclesPosition, l);
            if (isCollision()) {
                gm.setWrong();
                setLifeImages();
                gm.vibrate(v);
                String msg = "Ouch! " + String.valueOf(gm.getLifeCount()) + " lives left";
                if(!isStopped)
                    toast(msg);
            }
            numberOfObstaclesPosition--;
        }
    }

    private void moveSingleRow(int numberOfObstaclesPosition, LinearLayout l) {
        ShapeableImageView imageBelow = (ShapeableImageView) l.getChildAt(numberOfObstaclesPosition);
        ShapeableImageView imageAbove = (ShapeableImageView) l.getChildAt(numberOfObstaclesPosition - 1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageBelow.setVisibility(imageAbove.getVisibility());
                imageAbove.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean isCollision() {
        int playerPosition = gm.getPlayer().getCurrentPosition();
        int bottomPosition = gm.getNumberOfObstaclesPosition()-2;
        for (int i = 0; i < allRoutesLayouts.size(); i++) {
            if(allRoutesLayouts.get(i).getChildAt(bottomPosition).getVisibility() == View.VISIBLE && playerPosition == i)
                    return true;
        }
        return false;
    }

    //the max num to be generated is "to" minus 1
    private int getRandomNum(int from,int to) {
        return from + (int)(Math.random() * (to - from));
    }

    private void toast(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t = Toast.makeText(GameScreenActivity.this,msg,Toast.LENGTH_SHORT);
                t.show();
            }
        });
    }

    private void setLifeImages() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(gm.getLifeCount()>=0){
                    int removedIndex = gm.getLifeCount();
                    game_life_images.get(removedIndex).setVisibility(View.INVISIBLE);
                }
            }
        });

    }


    private void hideObstacle(ShapeableImageView obstacleImg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                obstacleImg.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showObstacle(ShapeableImageView obstacleImg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    obstacleImg.setVisibility(View.VISIBLE);
            }
        });
    }


    private void moveLeft() {
        int playerPosition = gm.getPlayer().getCurrentPosition();
        if(playerPosition > 0){
            game_character_positions.get(playerPosition).setVisibility(View.INVISIBLE);
            gm.getPlayer().setCurrentPosition(playerPosition-1);
            playerPosition = gm.getPlayer().getCurrentPosition();
            game_character_positions.get(playerPosition).setVisibility(View.VISIBLE);
        }
    }

    private void moveRight() {
        int playerPosition = gm.getPlayer().getCurrentPosition();
        if(playerPosition < NUMBER_OF_POSITIONS-1){
            game_character_positions.get(playerPosition).setVisibility(View.INVISIBLE);
            gm.getPlayer().setCurrentPosition(playerPosition+1);
            playerPosition = gm.getPlayer().getCurrentPosition();
            game_character_positions.get(playerPosition).setVisibility(View.VISIBLE);
        }
    }

    private void findViews() {
        game_LBL_time = findViewById(R.id.game_LBL_time);
        game_FAB_left = findViewById(R.id.game_FAB_left);
        game_FAB_right  = findViewById(R.id.game_FAB_right);
        game_LL_characterLayout = findViewById(R.id.game_LL_characterLayout);
        //game_LL_obstaclesLayout = findViewById(R.id.game_LL_obstaclesLayout);
        game_LL_heartLayout = findViewById(R.id.game_LL_heartLayout);
        getLifeImages();
        setAllRoutes();
    }


    private void setAllRoutes() {
        allRoutesLayouts.add(findViewById(R.id.main_LL_firstLayout));
        allRoutesLayouts.add(findViewById(R.id.main_LL_secondLayout));
        allRoutesLayouts.add(findViewById(R.id.main_LL_thirdLayout));
    }

    private void getLifeImages() {
        game_life_images.add(findViewById(R.id.main_IMG_heart1));
        game_life_images.add(findViewById(R.id.main_IMG_heart2));
        game_life_images.add(findViewById(R.id.main_IMG_heart3));
    }
}