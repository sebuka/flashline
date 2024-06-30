package ru.sebuka.flashline.activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ru.sebuka.flashline.R;

public class RulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

    }


}
