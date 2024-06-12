package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Button backbutton = findViewById(R.id.back_button);
        Button startbutton = findViewById(R.id.start_button);
        Spinner difficultySpinner = findViewById(R.id.difficulty_spinner);
        ArrayAdapter<String> adapter = getDifficultyOptions();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        difficultySpinner.setAdapter(adapter);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, LevelActivity.class);
                intent.putExtra("SEED_VALUE", getSeedValue());
                intent.putExtra("DIFFICULTY_VALUE", getSpinnerValue());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
        }
    }

    private String getSpinnerValue() {
        Spinner difficultySpinner = findViewById(R.id.difficulty_spinner);
        return difficultySpinner.getSelectedItem().toString();
    }

    private ArrayAdapter<String> getDifficultyOptions() {
        List<String> difficultyOptions = new ArrayList<>();
        for (Difficulty difficulty : Difficulty.values()) {
            difficultyOptions.add(difficulty.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, difficultyOptions);
        return adapter;
    }

    private int getSeedValue() {
        EditText seedEditText = findViewById(R.id.level_input);
        String seedstr = seedEditText.getText().toString();
        try {
            int seed = Integer.parseInt(seedstr);
            return seed;
        } catch (NumberFormatException e) {
            return (new Random()).nextInt();
        }
    }
}
