package com.example.racegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameScreenActivity extends AppCompatActivity {
    private ExtendedFloatingActionButton game_FAB_left;
    private ExtendedFloatingActionButton game_FAB_right;
    private MaterialTextView game_LBL_time;
    private LinearLayout game_LL_heartLayout;
    private ArrayList<ShapeableImageView> game_life_images = new ArrayList<ShapeableImageView>();
    private LinearLayout game_LL_characterLayout;
    private ArrayList<ShapeableImageView> game_character_positions = new ArrayList<ShapeableImageView>();
    private LinearLayout game_LL_obstaclesLayout;
    private GameManager gm;
    private final int NUMBER_OF_POSITIONS = 3;
    //time variables
    long startTime = 0;
    final int DELAY = 1000;
    ExecutorService threadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        gm = new GameManager();
        findViews();
        game_FAB_left.setOnClickListener(v -> moveLeft());
        game_FAB_right.setOnClickListener(v -> moveRight());
        initCharacterPositions();
        startTimer();
        //playBackgroundMusic();
        threadPool = Executors.newFixedThreadPool(2);
        startTime = System.currentTimeMillis();
    }

    private void runObstaclesTasks() {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        createObstacle();
                        //delay();
                    }
                });
    }

    private void playBackgroundMusic() {
        MediaPlayer ring= MediaPlayer.create(this,R.raw.crowd);
        ring.setLooping(true);
        ring.start();
    }

    //this function initiates an arraylist with the image views of the main character
    //in the character layout, there are also views to make a space between the image views
    //so i don't want to include them
    private void initCharacterPositions() {
        int size = game_LL_characterLayout.getChildCount();
        for(int i=0;i<size;i++){
            View v = game_LL_characterLayout.getChildAt(i);
            if(v instanceof ShapeableImageView){
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
                runOnUiThread(()->{
                    updateTimerUI();
                    runObstaclesTasks();
                });
            }
        },DELAY,DELAY);
    }

    private void updateTimerUI() {
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        int hours =minutes / 60;
        hours %= 24;
        game_LBL_time.setText(String.format("%02d:%02d:%02d", hours,minutes, seconds));
    }

    private void createObstacle() {
        Obstacle o = new Obstacle();
        int index = getRandomNum(0,NUMBER_OF_POSITIONS);
        LinearLayout chosenLayout = (LinearLayout) game_LL_obstaclesLayout.getChildAt(index);
        int numberOfObstaclesPosition = gm.getNumberOfObstaclesPosition();
        obstacleMovement(o, index, chosenLayout, numberOfObstaclesPosition);
    }

    //the max num to be generated is "to" minus 1
    private int getRandomNum(int from,int to) {
        return from + (int)(Math.random() * (to - from));
    }

    public void obstacleMovement(Obstacle o, int index, LinearLayout chosenLayout, int numberOfObstaclesPosition) {
        for (int i = 0; i < numberOfObstaclesPosition; i++) {
            ShapeableImageView obstacleImg = (ShapeableImageView) chosenLayout.getChildAt(o.getPosition());
            showObstacle(obstacleImg);
            if(gm.isCollision(index, o.getPosition())){
                setLifeImages();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                String msg = "Ouch! " + String.valueOf(gm.getLifeCount()) + " lives left";
                toast(msg);
                gm.vibrate(v);
            }
            delay();
            hideObstacle(obstacleImg);
            o.setNextPosition();
        }
    }

    private void toast(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gm.toast(msg,GameScreenActivity.this);
            }
        });
    }

    private void setLifeImages() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int removedIndex = gm.getLifeCount();
                game_life_images.get(removedIndex).setVisibility(View.INVISIBLE);
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

    synchronized void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    }

    private void getLifeImages() {
        game_life_images.add(findViewById(R.id.main_IMG_heart1));
        game_life_images.add(findViewById(R.id.main_IMG_heart2));
        game_life_images.add(findViewById(R.id.main_IMG_heart3));
    }
}