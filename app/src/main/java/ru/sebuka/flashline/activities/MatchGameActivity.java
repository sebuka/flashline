package ru.sebuka.flashline.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.sebuka.flashline.utils.GameLevel;
import ru.sebuka.flashline.models.LevelModel;
import ru.sebuka.flashline.R;
import ru.sebuka.flashline.dialogs.SettingsDialogFragment;

public class MatchGameActivity extends AppCompatActivity {
    private GameLevel gameLevel;
    private TextView pathsText;
    private TextView timeText;
    private ImageButton settingsButton;
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_game);
        String levelModelJson = getIntent().getStringExtra("LEVEL_MODEL");
        LevelModel levelModel = LevelModel.fromJson(levelModelJson);
        gridLayout = findViewById(R.id.grid_layout);
        pathsText = findViewById(R.id.paths_text);
        timeText = findViewById(R.id.time_text);
        settingsButton = findViewById(R.id.settings_button);
        gameLevel = new GameLevel(levelModel, pathsText, timeText, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels, new Runnable() {
            @Override
            public void run() {
                backToMatchActivity();
            }
        }, new Runnable() {
            @Override
            public void run() {
                gameLevel.setElapsedTime(gameLevel.getElapsedTime() - 1);
                timeText.setText(String.format("Time: %d sec", gameLevel.getElapsedTime()));
                gameLevel.getHandler().postDelayed(this, 1000);
                if(gameLevel.getElapsedTime() <= 0){
                    gameLevel.finishGame();
                }
            }
        });
        gameLevel.renderModel(gridLayout);
        gameLevel.setElapsedTime(levelModel.getTime());
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
        gameLevel.finishGame();
    }

    public void backToMatchActivity() {
        returnScoreAndFinish(gameLevel.calculateScore());
    }

    private void returnScoreAndFinish(int score) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("SCORE", score);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
