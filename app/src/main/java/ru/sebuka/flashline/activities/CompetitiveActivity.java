package ru.sebuka.flashline.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.sebuka.flashline.R;

public class CompetitiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitive);
    }

    public void onBackButtonClick(View view) {
        onBackPressed();
    }

    public void onMatchButtonClick(View view) {
        Intent intent = new Intent(this, MatchActivity.class);
        startActivity(intent);
    }

    public void onRankingButtonClick(View view) {
        Intent intent = new Intent(this, RatingActivity.class);
        startActivity(intent);
    }

    public void onTrainingButtonClick(View view) {
        Intent intent = new Intent(this, GeneratorActivity.class);
        startActivity(intent);
    }
}
