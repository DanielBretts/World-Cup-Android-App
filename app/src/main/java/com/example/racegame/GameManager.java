package com.example.racegame;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public class GameManager {
    private int lifeCount = 3;
    private Character player;
    private final int NUMBER_OF_OBSTACLE_POSITION = 4;


    public int getLifeCount() {
        return lifeCount;
    }

    public Character getPlayer() {
        return player;
    }

    public GameManager() {
        this.player = new Character();
    }

    public boolean isCollision(int obstaclePosition, int position) {
        if(obstaclePosition == player.currentPosition && position == NUMBER_OF_OBSTACLE_POSITION-1){
            if (lifeCount>0)
                setWrong();
            return true;
        }
        return false;
    }

    public void setWrong() {
        this.lifeCount--;
    }

    public int getNumberOfObstaclesPosition() {
        return NUMBER_OF_OBSTACLE_POSITION;
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
}
