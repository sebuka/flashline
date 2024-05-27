package com.example.myapplication;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class LevelActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView pathsText;
    private TextView timeText;
    private ImageButton settingsButton;
    private int gridSize = 5;
    private int itemSize = 200; // Начальный размер элемента
    private ScaleGestureDetector scaleGestureDetector;
    private Handler handler = new Handler();
    private int elapsedTime = 0;
    private String levelSeed = "12345678";
    private float scale = 1.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        gridLayout = findViewById(R.id.grid_layout);
        pathsText = findViewById(R.id.paths_text);
        timeText = findViewById(R.id.time_text);
        settingsButton = findViewById(R.id.settings_button);

        setupGridLayout();
        startTimer();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        gridLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });
        settingsButton.setOnClickListener(v -> {
            SettingsDialogFragment settingsDialog = new SettingsDialogFragment(levelSeed);
            settingsDialog.show(getSupportFragmentManager(), "settings_dialog");
        });
    }

    private void setupGridLayout() {
        gridLayout.removeAllViews();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                ImageView imageView = new ImageView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = itemSize;
                params.height = itemSize;
                params.setMargins(2, 2, 2, 2); // Добавляем отступы для видимости границ
                imageView.setLayoutParams(params);
                imageView.setBackgroundResource(R.drawable.grid_item_background);
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    public boolean onTouch(View v, MotionEvent event) {

                        return true;
                    }
                });
                gridLayout.addView(imageView);
            }
        }
    }

    private void changeCellColor(View cell, boolean paint) {
        if (paint) {
            // Перекрашиваем клетку в красный цвет
            cell.setBackgroundColor(Color.RED);
        } else {
            cell.setBackgroundResource(R.drawable.grid_item_background);
        }
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                elapsedTime++;
                int minutes = elapsedTime / 60;
                int seconds = elapsedTime % 60;
                timeText.setText(String.format("%02d:%02d", minutes, seconds));
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.5f, Math.min(scale, 3.0f));

            gridLayout.setScaleX(scale);
            gridLayout.setScaleY(scale);
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Intent intent = new Intent(LevelActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
        }
    }

}