package com.example.racegame;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.text.PrecomputedText;

public class BackgroundSound extends AsyncTask<Void,Void,Void> {

    private Context context;
    private int music;
    private boolean isLooping;
    private MediaPlayer player;

    public BackgroundSound(Context context,int music,boolean isLooping) {
        this.context = context;
        this.music = music;
        this.isLooping = isLooping;
    }

    @Override
    protected Void doInBackground(Void... params) {
        player = MediaPlayer.create(this.context, this.music);
        player.setLooping(isLooping); // Set looping
        player.setVolume(1.0f, 1.0f);
        player.start();
        return null;
    }

    public void stopMusic(){
        player.stop();
    }

}
