package com.example.racegame;

import java.util.Comparator;

public class ScoresComparator implements Comparator<ScoreRecord> {
    @Override
    public int compare(ScoreRecord o1, ScoreRecord o2) {
        return o1.compareTo(o2);
    }
}
