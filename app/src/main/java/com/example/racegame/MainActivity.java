package com.example.racegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Switch;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    private ShapeableImageView main_BTN_start;
    private BackgroundSound mBackgroundSound;
    private MaterialTextView main_LBL_movement;
    private MaterialTextView main_LBL_buttons;

    private Switch main_SWTC_moveType;
    private Switch main_SWTC_speed;
    private MaterialTextView main_LBL_slow;
    private MaterialTextView main_LBL_fast;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkGPSSettings();
        setContentView(R.layout.activity_main);
        findViews();
        main_BTN_start.setOnClickListener(v -> openGameScreen());
        main_SWTC_moveType.setOnClickListener(v -> changeBoldText(main_SWTC_moveType,0));
        main_SWTC_speed.setOnClickListener(v -> changeBoldText(main_SWTC_speed,1));
        //playBackgroundMusic();
    }

    private void checkGPSSettings() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void changeBoldText(Switch switchType, int type) {
        boolean isOptions = switchType.isChecked();
        switch (type){
            case(0):{
                if(isOptions){
                    main_LBL_movement.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    main_LBL_buttons.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }else {
                    main_LBL_movement.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    main_LBL_buttons.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                break;
            }
            case(1):{
                if(isOptions){
                    main_LBL_fast.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    main_LBL_slow.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }else {
                    main_LBL_fast.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    main_LBL_slow.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBackgroundSound = new BackgroundSound(this,R.raw.whistle,false);
        mBackgroundSound.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBackgroundSound.cancel(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBackgroundSound.cancel(true);
    }

    private void openGameScreen() {
        boolean movementType = main_SWTC_moveType.isChecked();
        boolean speedType = main_SWTC_speed.isChecked();
        Intent gameIntent = new Intent(this, GameScreenActivity.class);
        gameIntent.putExtra(GameScreenActivity.KEY_MOVE_TYPE, movementType);
        gameIntent.putExtra(GameScreenActivity.KEY_SPEED, speedType);
        startActivity(gameIntent);
        overridePendingTransition(R.style.WindowAnimationStyle,R.style.WindowAnimationStyle);
    }

    private void findViews() {
        main_BTN_start = findViewById(R.id.main_BTN_start);
        main_SWTC_moveType = findViewById(R.id.main_SWTC_moveType);
        main_SWTC_speed = findViewById(R.id.main_SWTC_speed);
        main_LBL_buttons = findViewById(R.id.main_LBL_buttons);
        main_LBL_movement = findViewById(R.id.main_LBL_movement);
        main_LBL_slow = findViewById(R.id.main_LBL_slow);
        main_LBL_fast = findViewById(R.id.main_LBL_fast);
    }
}