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
    private double totalCompleted;
    private TextView streak, grade;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.consistency_fragment, container, false);

        this.streak = (TextView) this.view.findViewById(R.id.streak);
        this.grade = (TextView) this.view.findViewById(R.id.grade);
        this.progressBar = (ProgressBar) this.view.findViewById(R.id.progress_bar);
        this.totalCompleted = 0;

        return view;
    }

    public void makeProgressBar(CardInfo cardInfo) {
        this.streak.setText(cardInfo.getStreakCount() + "");

        for (int i = 0; i < cardInfo.savedDates.size(); i++) {
            if (cardInfo.savedDates.get(i).getCompleted())
                this.totalCompleted++;
        }

        this.grade.setText(this.totalCompleted / cardInfo.savedDates.size() + "");

        this.progressBar.setProgress((int) (this.totalCompleted / cardInfo.savedDates.size()));
    }

    public void refreshProgressBar(CardInfo cardInfo) {
        this.totalCompleted++;

        this.streak.setText(" " + cardInfo.getStreakCount());
        this.grade.setText(this.totalCompleted / cardInfo.savedDates.size() + "");
        this.progressBar.setProgress((int) (this.totalCompleted / cardInfo.savedDates.size()));
    }
}
