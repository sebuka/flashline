package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class StatsActivity extends AppCompatActivity {
    private LinearLayout levelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        levelList = findViewById(R.id.level_list);
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        loadResults();
    }

    private void loadResults() {
        SharedPreferences sharedPreferences = getSharedPreferences("game_results", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("results", "");
        if (!json.isEmpty()) {
            String[] resultsArray = json.split(";");
            for (String result : resultsArray) {
                String[] resultData = result.split(",");
                if (resultData.length == 4) {
                    addResultToList(resultData[0], resultData[1], resultData[2], resultData[3]);
                }
            }
        }
    }

    private void addResultToList(String seed, String difficulty, String result, String imagePath) {
        View listItem = LayoutInflater.from(this).inflate(R.layout.level_list_item, levelList, false);

        TextView levelSeed = listItem.findViewById(R.id.level_seed);
        TextView levelDifficulty = listItem.findViewById(R.id.level_difficulty);
        TextView levelResult = listItem.findViewById(R.id.level_result);
        Button btnViewSolution = listItem.findViewById(R.id.btn_view_solution);

        levelSeed.setText("Уровень: " + seed);
        levelDifficulty.setText("Сложность: " + difficulty);
        levelResult.setText("Результат: " + result);

        btnViewSolution.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            SolutionDialogFragment solutionDialog = SolutionDialogFragment.newInstance(imagePath);
            solutionDialog.show(fragmentManager, "solution_dialog");
        });

        levelList.addView(listItem);
    }
}
