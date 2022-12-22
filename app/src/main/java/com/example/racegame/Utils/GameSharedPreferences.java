package com.example.racegame.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;

import com.example.racegame.ScoreRecord;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GameSharedPreferences {
    public static final String DB_FILE = "DB_FILE";
    private static GameSharedPreferences instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    private GameSharedPreferences(Context context) {
        preferences = context.getSharedPreferences(DB_FILE,context.MODE_PRIVATE);
    }

    public static void init(Context context){
        if(instance == null)
            instance = new GameSharedPreferences(context);
    }

    public static GameSharedPreferences getInstance(){
        return instance;
    }


//    public void putInt(String key, int value) {
//
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt(key, value);
//        editor.apply();
//    }
//
//    public int getInt(String key, int defValue) {
//        return preferences.getInt(key, defValue);
//    }
//
//    public void putString(String key, String value) {
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(key, value);
//        editor.apply();
//    }
//
//    public String getString(String key, String defValue) {
//        return preferences.getString(key, defValue);
//    }


    public void setList(List<ScoreRecord> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor = preferences.edit();
        editor.putString(DB_FILE, json);
        editor.commit();
    }

    public List<ScoreRecord> getList() {
        List<ScoreRecord> list = null;
        String serializedObject = preferences.getString(DB_FILE, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScoreRecord>>() {
            }.getType();
            list = gson.fromJson(serializedObject, type);
        }
        return list==null ? new ArrayList<ScoreRecord>() : list;
    }

}
