package com.example.racegame.Views;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.racegame.EndScreenActivity;
import com.example.racegame.MapCallback;
import com.example.racegame.ScoresComparator;
import com.example.racegame.R;
import com.example.racegame.ScoreAdapter;
import com.example.racegame.ScoreRecord;
import com.example.racegame.Utils.GameSharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ScoreAdapter scoreAdapter;
    private Context context;

    public ListFragment(Context context){
        this.context = context;
        List<ScoreRecord> scores = GameSharedPreferences.getInstance().getList();
        Collections.sort(scores,new ScoresComparator());
        List<ScoreRecord> topTen = scores.stream().limit(10).collect(Collectors.toList());
        scoreAdapter = new ScoreAdapter(context, (ArrayList<ScoreRecord>) topTen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scores_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
//        Context context = view.getContext();
//        List<ScoreRecord> scores = GameSharedPreferences.getInstance().getList();
//        Collections.sort(scores,new ScoresComparator());
//        scoreAdapter = new ScoreAdapter(context, (ArrayList<ScoreRecord>) scores);
        recyclerView = view.findViewById(R.id.end_RV_scores);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(scoreAdapter);
    }

    public ScoreAdapter getScoreAdapter() {
        return scoreAdapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
