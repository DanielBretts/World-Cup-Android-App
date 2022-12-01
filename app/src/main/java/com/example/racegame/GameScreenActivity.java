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
    private Vibrator v;
    private Toast t;
    private ExtendedFloatingActionButton game_FAB_left;
    private ExtendedFloatingActionButton game_FAB_right;
    private MaterialTextView game_LBL_time;
    private LinearLayout game_LL_heartLayout;
    private ArrayList<ShapeableImageView> game_life_images = new ArrayList<ShapeableImageView>();
    private LinearLayout game_LL_characterLayout;
    private ArrayList<ShapeableImageView> game_character_positions = new ArrayList<ShapeableImageView>();
    private LinearLayout game_LL_obstaclesLayout;
    private ArrayList<LinearLayout> allRoutesLayouts = new ArrayList<LinearLayout>();
    private ShapeableImageView obstaclesImages[][];
    private GameManager gm;
    private int summonObstacle = 1;
    private final int NUMBER_OF_POSITIONS = 3;
    //time variables
    long startTime = 0;
    final int DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        gm = new GameManager();
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
        if(t != null)
            t.cancel();
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

    private Timer timer;

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    updateTimerUI();
                });
                createObstacle();
                summonObstacle++;
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
        int numberOfObstaclesPosition = gm.getNumberOfObstaclesPosition() - 1;
        //obstacleMovement(o, index, chosenLayout, numberOfObstaclesPosition);
        while (numberOfObstaclesPosition > 0) {
            for (LinearLayout l : allRoutesLayouts) {
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
            if (isCollision()) {
                setLifeImages();
                gm.setWrong();
                String msg = "Ouch! " + String.valueOf(gm.getLifeCount()) + " lives left";
                toast(msg);
                gm.vibrate(v);
            }
            numberOfObstaclesPosition--;
        }

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

//    public void obstacleMovement(Obstacle o, int index, LinearLayout chosenLayout, int numberOfObstaclesPosition) {
//        for (int i = 0; i < numberOfObstaclesPosition; i++) {
//            ShapeableImageView obstacleImg = (ShapeableImageView) chosenLayout.getChildAt(o.getPosition());
//            showObstacle(obstacleImg);
//            if(gm.isCollision(index, o.getPosition())){
//                setLifeImages();
//                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                String msg = "Ouch! " + String.valueOf(gm.getLifeCount()) + " lives left";
//                toast(msg);
//                gm.vibrate(v);
//            }
//            delay();
//            hideObstacle(obstacleImg);
//            o.setNextPosition();
//
//        }
//    }

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
        game_LL_obstaclesLayout = findViewById(R.id.game_LL_obstaclesLayout);
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