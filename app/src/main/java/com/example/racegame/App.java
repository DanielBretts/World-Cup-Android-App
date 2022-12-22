package com.example.racegame;

import android.app.Application;

import com.example.racegame.Utils.GameSharedPreferences;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GameSharedPreferences.init(this);
    }
}
