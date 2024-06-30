package ru.sebuka.flashline.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

import ru.sebuka.flashline.adapters.LevelGridAdapter;
import ru.sebuka.flashline.models.LevelModel;
import ru.sebuka.flashline.R;

public class LevelSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        Button backButton = findViewById(R.id.back_button);
        RecyclerView levelGrid = findViewById(R.id.level_grid);

        backButton.setOnClickListener(v -> onBackPressed());

        List<Integer> levels = parseLevelsFromXML(this);

        LevelGridAdapter adapter = new LevelGridAdapter(this, levels, level -> {
            Intent intent = new Intent(LevelSelectionActivity.this, LevelActivity.class);
            LevelModel model = LevelModel.fromXML(level,this);
            intent.putExtra("LEVEL_MODEL", model.toJson());
            startActivity(intent);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        levelGrid.setLayoutManager(gridLayoutManager);
        levelGrid.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 8;
                outRect.top = 8;
                outRect.left = 8;
                outRect.right = 8;
            }
        });

        levelGrid.setAdapter(adapter);
    }

    private List<Integer> parseLevelsFromXML(Context context) {
        List<Integer> levels = new ArrayList<>();

        try {
            XmlPullParser parser = context.getResources().getXml(R.xml.levels);
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "level".equals(parser.getName())) {
                    int levelNumber = Integer.parseInt(parser.getAttributeValue(null, "number"));
                    levels.add(levelNumber);
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return levels;
    }
}
