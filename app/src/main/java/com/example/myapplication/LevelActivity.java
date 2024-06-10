package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class LevelActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    private GameLevel gameLevel;
    private TextView pathsText;
    private TextView timeText;
    private ImageButton settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        int seed = getIntent().getIntExtra("SEED_VALUE", 0);
        String difficulty = getIntent().getStringExtra("DIFFICULTY_VALUE");

        GridLayout gridLayout = findViewById(R.id.grid_layout);
        pathsText = findViewById(R.id.paths_text);
        timeText = findViewById(R.id.time_text);
        settingsButton = findViewById(R.id.settings_button);
        gameLevel = new GameLevel(seed, difficulty, pathsText, timeText);
        gameLevel.generate(gridLayout, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        settingsButton.setOnClickListener(v -> {
            SettingsDialogFragment settingsDialog = new SettingsDialogFragment(gameLevel.getSeed());
            settingsDialog.show(getSupportFragmentManager(), "settings_dialog");
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameLevel.stopTimer();
    }

    public GameLevel getGameLevel() {
        return gameLevel;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gameLevel.finishGame();
    }
}
