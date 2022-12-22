package com.example.racegame;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.racegame.Views.ListFragment;
import com.example.racegame.Views.MapViewFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class EndScreenActivity extends AppCompatActivity{

    private MapViewFragment mapFragment;
    private ListFragment scoresFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);
        mapFragment = new MapViewFragment();
        scoresFragment = new ListFragment(this);
        scoresFragment.getScoreAdapter().setMapCallback(new MapCallback() {
            @Override
            public void showLocation(ScoreRecord scoreRecord, int position) {mapFragment.showOnMap(scoreRecord);
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.main_FRAME_list, scoresFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.main_FRAME_map, mapFragment).commit();
    }


}