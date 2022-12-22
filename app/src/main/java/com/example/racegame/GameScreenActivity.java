package com.example.racegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.racegame.Utils.GameSharedPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import im.delight.android.location.SimpleLocation;

public class GameScreenActivity extends AppCompatActivity {
    private ExtendedFloatingActionButton game_FAB_left;
    private ExtendedFloatingActionButton game_FAB_right;
    private MaterialTextView game_LBL_time;
    private LinearLayout game_LL_heartLayout;
    private ArrayList<ShapeableImageView> game_life_images;
    private LinearLayout game_LL_characterLayout;
    private ArrayList<ShapeableImageView> game_character_positions;
    private ArrayList<LinearLayout> allRoutesLayouts;
    private GameManager gm;
    private Vibrator v;
    private Toast t;
    private BackgroundSound bs;
    private BackgroundSound collisionSound;
    private BackgroundSound coinSound;
    private boolean isStopped = false;
    private int index = 0;
    private boolean isBonus = false;
    private final int SLOW_SPEED = 1000;
    private final int FAST_SPEED = 500;
    public static final String SCORE_RECORD = "SCORE_RECORD";
    //private int summonObstacle = 1;
    private final int NUMBER_OF_POSITIONS = 5;
    boolean isGameOver = false;
    //time variables
    private Timer timer;
    private long startTime = 0;
    private int DELAY;
    public static final String KEY_SPEED = "KEY_SPEED";
    public static final String KEY_MOVE_TYPE = "MOVE_TYPE";
    private MovementDetector md;
    private boolean movementType;
    //GPS Variables
    private SimpleLocation simpleLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        gm = new GameManager();
        simpleLocation = gm.initLocation(this);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initArrays();
        findViews();
        setTags();
        setMovementType();
        setSpeed();
        initCharacterPositions();
        startTime = System.currentTimeMillis();
        startTimer();
    }

    private void initArrays() {
        game_life_images = new ArrayList<ShapeableImageView>();
        game_character_positions = new ArrayList<ShapeableImageView>();
        allRoutesLayouts = new ArrayList<LinearLayout>();
    }


    private void setSpeed() {
        boolean speed = getSpeed();
        if(speed){
            DELAY = FAST_SPEED;
        }else{
            DELAY = SLOW_SPEED;
        }
    }

    private void setTags() {
        for(LinearLayout l : allRoutesLayouts) {
            for (int i = 0; i < l.getChildCount(); i++) {
                l.getChildAt(i).setTag(R.drawable.obstacle);
            }
        }
    }

    private void setMovementType() {
        movementType = getMovementType();
        if(movementType) {
            game_FAB_right.setVisibility(View.INVISIBLE);
            game_FAB_left.setVisibility(View.INVISIBLE);
            initDetector();
        }else{
            game_FAB_left.setOnClickListener(v -> moveLeft());
            game_FAB_right.setOnClickListener(v -> moveRight());
        }


    }

    private boolean getMovementType() {
        Intent previousIntent = getIntent();
        return previousIntent.getBooleanExtra(KEY_MOVE_TYPE,false);
    }

    private boolean getSpeed() {
        Intent previousIntent = getIntent();
        return previousIntent.getBooleanExtra(KEY_SPEED,false);
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
        initMusic();
        setMovementType();
        simpleLocation.beginUpdates();
        if(md != null)
            md.start();
        isStopped = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(md != null)
            md.stop();
        simpleLocation.endUpdates();
        if(collisionSound != null)
            collisionSound.cancel(false);
    }

    private void initMusic() {
        bs = new BackgroundSound(this,R.raw.crowd,true);
        bs.execute();
    }

    private void initDetector() {
        md = new MovementDetector(this, new MovementCallback() {
            @Override
            public void moveLeftSensor() {
                moveLeft();
            }

            @Override
            public void moveRightSensor() {
                moveRight();
            }
        });
    }
    /**
     * this function initiates an arraylist with the image views of the main character
     * in the character layout, there are also views to make a space between the image views
     * so i don't want to include them
     */
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
                    if(!isGameOver) {
                    runOnUiThread(() -> {
                        updateTimerUI();
                    });
                    createObstacle();
                    //summonObstacle++;
                    }


                }
            }, DELAY, DELAY);
    }

    private void updateTimerUI() {
        game_LBL_time.setText(String.format("Score: %d", gm.getScore()));
    }

    private void createObstacle() {
        //gm.getNumberOfObstaclesPosition() - 1 is the index of the last row of obstacles
        moveObstacles(gm.getNUMBER_OBSTACLE_ROW() - 1);
        initNewObstacle();
    }

    //Resetting the first row and generating new obstacles
    private void initNewObstacle() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (LinearLayout l : allRoutesLayouts)
                        l.getChildAt(0).setVisibility(View.INVISIBLE);
                    index = getRandomNum(0, NUMBER_OF_POSITIONS);
                    //checking if generating a bonus and not an obstacle
                    isBonus = gm.isBonus();
                    ShapeableImageView image = (ShapeableImageView) allRoutesLayouts.get(index).getChildAt(0);
                    if (isBonus){
                        allRoutesLayouts.get(index).getChildAt(0).setTag(R.drawable.cup);
                        ((ShapeableImageView) allRoutesLayouts.get(index).getChildAt(0)).setImageResource(R.drawable.cup);
                    }
                    allRoutesLayouts.get(index).getChildAt(0).setVisibility(View.VISIBLE);
                }
            });
    }

    //obstacle movement and checks if there was a collision
    private void moveObstacles(int numberOfObstaclesPosition) {
        while (numberOfObstaclesPosition > 0) {
            for (LinearLayout l : allRoutesLayouts)
                moveSingleRow(numberOfObstaclesPosition, l);
            if(numberOfObstaclesPosition == 1) {
                int collision = isCollision();
                if (collision == 1) {
                    Log.d("moveObstacles: ", String.valueOf(gm.getLifeCount()));
                    collisionSound = new BackgroundSound(this, R.raw.collision, false);
                    collisionSound.execute();
                    gm.setLifeCount();
                    setLifeImages();
                    gm.vibrate(v);
                    String msg = "Ouch! " + String.valueOf(gm.getLifeCount()) + " lives left";
                    if (!isStopped)
                        toast(msg);
                    if (gm.getLifeCount() == 0) {
                        isGameOver = true;
                        gm.setLifeCount();
                        setLifeImages();
                        createNamePopup();
                        bs.stopMusic();
                    }
                    //break;
                } else if (collision == 2) {
                    gm.increaseScore();
                    coinSound = new BackgroundSound(this, R.raw.coin, false);
                    coinSound.execute();
                    //break;
                }
            }
            numberOfObstaclesPosition--;
        }
    }


    @SuppressLint("ResourceType")
    private void createNamePopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup,null);
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        PopupWindow namePopup = new PopupWindow(popupView,width,height,true);
        namePopup.setFocusable(true);
        namePopup.update();

        RelativeLayout mainLayout = findViewById(R.id.main_RL_game);
        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                namePopup.showAtLocation(mainLayout, Gravity.CENTER,0,0);
            }
        });
        MaterialTextView popup_LBL_score = popupView.findViewById(R.id.popup_LBL_score);
        TextInputEditText popup_ET_name = popupView.findViewById(R.id.popup_ET_name);
        MaterialButton popup_BTN_submit = popupView.findViewById(R.id.popup_BTN_submit);
        popup_LBL_score.setText("Score: " + String.valueOf(gm.getScore()));
        popup_BTN_submit.setOnClickListener(v -> {
            recordScore(popup_ET_name.getText().toString());
            switchToScoresActivity();
        });
        popup_ET_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(popup_ET_name.getText().length() != 0) {

                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        switchToScoresActivity();
                    }
                }else{
                    showAnimationError(popup_ET_name);
                }
                return true;
            }
        });
    }

    private void showAnimationError(View view) {
        YoYo.with(Techniques.Shake)
                .duration(300)
                .repeat(1)
                .playOn(view);
    }

    protected void recordScore(String name) {
        double longitude = simpleLocation.getLongitude();
        double latitude = simpleLocation.getLatitude();

        double[] location = {longitude,latitude};
        ScoreRecord scoreRecord = new ScoreRecord(name,gm.getScore(),location);
        Log.d("a",scoreRecord.toString());
        List<ScoreRecord> top10Scores = GameSharedPreferences.getInstance().getList();
        top10Scores.add(scoreRecord);
        GameSharedPreferences.getInstance().setList(top10Scores);
    }

    private void switchToScoresActivity() {
        Intent gameIntent = new Intent(this, EndScreenActivity.class);
        startActivity(gameIntent);
    }

    private boolean checkIfHitBonus() {
        int playerPosition = gm.getPlayer().getCurrentPosition();
        int bottomPosition = gm.getNUMBER_OBSTACLE_ROW()-2;
        for (int i = 0; i < allRoutesLayouts.size(); i++) {
            if ((Integer) allRoutesLayouts.get(i).getChildAt(bottomPosition).getTag() == R.drawable.cup && playerPosition == i){
                allRoutesLayouts.get(i).getChildAt(bottomPosition+1).setVisibility(View.INVISIBLE);
                return true;
            }
        }
        return false;
    }


    //The obstacle movement logic - every row takes the row above and copies it to itself
    private void moveSingleRow(int numberOfObstaclesPosition, LinearLayout l) {
        ShapeableImageView currentImage = (ShapeableImageView) l.getChildAt(numberOfObstaclesPosition);
        ShapeableImageView imageAbove = (ShapeableImageView) l.getChildAt(numberOfObstaclesPosition-1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setImagesToBonus(imageAbove,currentImage);
                setVisibilityObstacles(imageAbove,currentImage);
            }
        });
    }

    private void setImagesToBonus(ShapeableImageView imageAbove, ShapeableImageView currentImage) {
        currentImage.setTag(imageAbove.getTag());
        currentImage.setImageResource((Integer) currentImage.getTag());
        imageAbove.setTag(R.drawable.obstacle);
        imageAbove.setImageResource((Integer) imageAbove.getTag());
    }

    private void setVisibilityObstacles(ShapeableImageView imageAbove, ShapeableImageView currentImage) {
        currentImage.setVisibility(imageAbove.getVisibility());
        imageAbove.setVisibility(View.INVISIBLE);
    }

    //Collision checker - checks if the character was in the same route while an obstacle was in the bottom of the screen
    private int isCollision() {
        int playerPosition = gm.getPlayer().getCurrentPosition();
        int bottomPosition = gm.getNUMBER_OBSTACLE_ROW()-2;
        for (int i = 0; i < allRoutesLayouts.size(); i++) {
            if(allRoutesLayouts.get(i).getChildAt(bottomPosition).getVisibility() == View.VISIBLE && playerPosition == i)
                if((Integer) allRoutesLayouts.get(i).getChildAt(bottomPosition).getTag() == R.drawable.obstacle) {
                    Log.d("", allRoutesLayouts.get(0).getChildAt(bottomPosition).getTag().toString() + allRoutesLayouts.get(1).getChildAt(bottomPosition).getTag().toString() + allRoutesLayouts.get(2).getChildAt(bottomPosition).getTag().toString() + allRoutesLayouts.get(3).getChildAt(bottomPosition).getTag().toString() + allRoutesLayouts.get(4).getChildAt(bottomPosition).getTag().toString());
                    return 1;
                }
                else if((Integer) allRoutesLayouts.get(i).getChildAt(bottomPosition).getTag() == R.drawable.cup) {
                    allRoutesLayouts.get(i).getChildAt(bottomPosition+1).setVisibility(View.INVISIBLE);
                    return 2;
                }
        }
        return 0;
    }

    //the max num to be generated is "to" minus 1
    private int getRandomNum(int from,int to) {
        return from + (int)(Math.random() * (to - from));
    }

    //toast creator
    private void toast(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t = Toast.makeText(GameScreenActivity.this,msg,Toast.LENGTH_SHORT);
                t.show();
            }
        });
    }

    //set the amount of life (soccer balls) in the top of the screen
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

    //Character movement - Left
    private void moveLeft() {
        int playerPosition = gm.getPlayer().getCurrentPosition();
        if(playerPosition > 0){
            game_character_positions.get(playerPosition).setVisibility(View.INVISIBLE);
            gm.getPlayer().setCurrentPosition(playerPosition-1);
            playerPosition = gm.getPlayer().getCurrentPosition();
            game_character_positions.get(playerPosition).setVisibility(View.VISIBLE);
        }
    }

    //Character movement - Right
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
        game_LL_heartLayout = findViewById(R.id.game_LL_heartLayout);
        getLifeImages();
        getAllRoutes();
    }

    //Gets the routes to be accessible in the activity
    private void getAllRoutes() {
        allRoutesLayouts.add(findViewById(R.id.main_LL_firstLayout));
        allRoutesLayouts.add(findViewById(R.id.main_LL_secondLayout));
        allRoutesLayouts.add(findViewById(R.id.main_LL_thirdLayout));
        allRoutesLayouts.add(findViewById(R.id.main_LL_fourthLayout));
        allRoutesLayouts.add(findViewById(R.id.main_LL_fifthLayout));

    }

    //Gets the life images (soccer ball) to be accessible in the activity
    private void getLifeImages() {
        game_life_images.add(findViewById(R.id.main_IMG_heart1));
        game_life_images.add(findViewById(R.id.main_IMG_heart2));
        game_life_images.add(findViewById(R.id.main_IMG_heart3));
    }

}
