package com.example.racegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class MainActivity extends AppCompatActivity {

    ShapeableImageView main_BTN_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        main_BTN_start.setOnClickListener(v -> openGameScreen());
        playBackgroundMusic();
    }

    private void playBackgroundMusic() {
        MediaPlayer ring = MediaPlayer.create(this, R.raw.crowd);
        ring.setLooping(true);
        ring.start();
    }

    private void openGameScreen() {
        Intent gameIntent = new Intent(this, GameScreenActivity.class);
        startActivity(gameIntent);
        overridePendingTransition(R.style.WindowAnimationStyle,R.style.WindowAnimationStyle);
    }

    private void findViews() {
        main_BTN_start = findViewById(R.id.main_BTN_start);
    }
}