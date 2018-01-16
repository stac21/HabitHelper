package com.example.grant.projectmoheth;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ConsistencyFragment extends Fragment {
    private View view;
    private int totalCompleted;
    private TextView streak, grade;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.consistency_fragment, container, false);

        this.streak = (TextView) this.view.findViewById(R.id.streak);
        this.grade = (TextView) this.view.findViewById(R.id.grade);
        this.totalCompleted = 0;

        return view;
    }

    public void makeFragment(CardInfo cardInfo) {
        this.streak.setText(" " + cardInfo.getStreakCount());

        for (int i = 0; i < cardInfo.savedDates.size(); i++) {
            if (cardInfo.savedDates.get(i).getCompleted())
                this.totalCompleted++;
        }

        String gradeStr = " " +  ((cardInfo.savedDates.size() == 0) ? getString(R.string.not_available) :
                (int) ((double) this.totalCompleted / cardInfo.savedDates.size() * 100) + "%");

        this.grade.setText(gradeStr);
    }

    public void refresh(CardInfo cardInfo) {
        this.totalCompleted++;

        this.streak.setText(" " + cardInfo.getStreakCount());
        this.grade.setText(" " + (int) ((double) this.totalCompleted / cardInfo.savedDates.size()
                * 100) + "%");
    }
}
