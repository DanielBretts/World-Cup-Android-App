package com.example.racegame;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import im.delight.android.location.SimpleLocation;

public class GameManager {
    private int lifeCount = 3;
    private Character player;
    private final int NUMBER_OBSTACLE_ROW = 5;
    private final int NUMBER_OBSTACLE_COL = 5;
    private int score = 0;
    private boolean isGenerateBonus = false;

    public int getLifeCount() {
        return this.lifeCount;
    }

    public Character getPlayer() {
        return player;
    }

    public GameManager() {
        this.player = new Character();
    }

    public void setLifeCount() {
        if(this.lifeCount > 0)
            this.lifeCount--;
    }

    public int getNUMBER_OBSTACLE_ROW() {
        return NUMBER_OBSTACLE_ROW;
    }

    public int getNUMBER_OBSTACLE_COL() {
        return NUMBER_OBSTACLE_COL;
    }

    public void vibrate(Vibrator v) {
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    public void toast(String text, Context context){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show();
    }

    //returns true if generating an bonus and false if a obstacle
    public boolean isBonus() {
        int choice = (int) (Math.random() * 100);
        if (choice <= 80)
            return false;
        return true;
    }

    public int getScore() {
        return score;
    }

    public void increaseScore() {
        this.score+=5;
    }

    public SimpleLocation initLocation(Context context) {
        SimpleLocation location = new SimpleLocation(context);
        if (!location.hasLocationEnabled()) {
            SimpleLocation.openSettings(context);
        }
        return location;
    }
}
