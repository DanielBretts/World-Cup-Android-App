package com.example.racegame;

import java.util.Arrays;
import java.util.Comparator;

public class ScoreRecord implements Comparable<ScoreRecord>{
    private String name;
    private int score;
    private double[] location;

    public ScoreRecord(String name,int score,double[] location) {
        this.name = name;
        this.score = score;
        this.location = location;
    }

    public double[] getLocation() {
        return location;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(ScoreRecord o) {
        if (this.getScore() < o.getScore())
            return 1;
        else if (this.score > o.getScore())
            return -1;
        else
            return 0;
    }


    @Override
    public String toString() {
        return "ScoreRecord{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", location=" + Arrays.toString(location) +
                '}';
    }
}
