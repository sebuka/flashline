package ru.sebuka.flashline.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import ru.sebuka.flashline.utils.GameLevel;
import ru.sebuka.flashline.models.LevelModel;
import ru.sebuka.flashline.R;
import ru.sebuka.flashline.dialogs.SettingsDialogFragment;

public class LevelActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    private GameLevel gameLevel;
    private TextView pathsText;
    private TextView timeText;
    private ImageButton settingsButton;
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        String levelModelJson = getIntent().getStringExtra("LEVEL_MODEL");
        LevelModel levelModel = LevelModel.fromJson(levelModelJson);
        gridLayout = findViewById(R.id.grid_layout);
        pathsText = findViewById(R.id.paths_text);
        timeText = findViewById(R.id.time_text);
        settingsButton = findViewById(R.id.settings_button);
        gameLevel = new GameLevel(levelModel, pathsText, timeText, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels, new Runnable() {
            @Override
            public void run() {
                saveResult();
                showResultsDialog();
            }
        }, new Runnable() {
            @Override
            public void run() {
                gameLevel.setElapsedTime(gameLevel.getElapsedTime() + 1);
                timeText.setText(String.format("Time: %d sec", gameLevel.getElapsedTime()));
                gameLevel.getHandler().postDelayed(this, 1000);
            }
        });
        gameLevel.renderModel(gridLayout);
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

    public void saveResult() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("game_results", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json = sharedPreferences.getString("results", "");
        List<String> resultList = new ArrayList<>();
        if (!json.isEmpty()) {
            String[] resultsArray = json.split(";");
            for (String s : resultsArray) {
                resultList.add(s);
            }
        }

        StringBuilder newJson = new StringBuilder();
        for (String res : resultList) {
            newJson.append(res).append(";");
        }

        editor.putString("results", newJson.toString());
        editor.apply();
    }

    private String saveGridImage(View gridLayout) {
        if (gridLayout == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(gridLayout.getWidth(), gridLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        gridLayout.draw(canvas);

        File directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "GridImages");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = "grid_" + System.currentTimeMillis() + ".png";
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private void showResultsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_results, null);

        TextView resultText = dialogView.findViewById(R.id.result_text);
        ImageView star1 = dialogView.findViewById(R.id.star_1);
        ImageView star2 = dialogView.findViewById(R.id.star_2);
        ImageView star3 = dialogView.findViewById(R.id.star_3);
        TextView scoreText = dialogView.findViewById(R.id.score_text);
        Button toMain = dialogView.findViewById(R.id.btn_main_menu);
        if (gameLevel.getCompletedPaths() >= gameLevel.getModel().getPoints()) {
            star1.setColorFilter(ContextCompat.getColor(this, R.color.star_color), android.graphics.PorterDuff.Mode.SRC_IN);
            if (gameLevel.getElapsedTime() <= gameLevel.getModel().getOptimalPaths()) {
                star2.setColorFilter(ContextCompat.getColor(this, R.color.star_color), android.graphics.PorterDuff.Mode.SRC_IN);
                if (gameLevel.getPathsCount() >= gameLevel.getModel().getOptimalPaths() * gameLevel.getModel().getPathsPersantage()) {
                    star3.setColorFilter(ContextCompat.getColor(this, R.color.star_color), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        }
        resultText.setText("Результат");
        int score = gameLevel.calculateScore();
        scoreText.setText("Счет: " + score);
        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setCancelable(false);
        builder.show();
    }
}
