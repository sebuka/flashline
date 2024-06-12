package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.myapplication.gameobjects.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameLevel {
    private static final String TAG = "GameLevel";
    private int seed;
    private Difficulty difficulty;
    private Map<View, GameObject> gameField;
    private GameObject[][] grid;
    private List<Connectable> lastPath;
    private int gridSize;
    private int points; // количество точек
    private int completedPaths = 0; // счетчик завершенных путей

    private int optimalPaths = 0;
    private GridLayout gridLayout;
    private final Object pathLock = new Object();
    private TextView pathsText;
    private TextView timeText;
    private Handler handler = new Handler();
    private int elapsedTime = 0;

    public GameLevel(int seed, String difficulty, TextView pathsText, TextView timeText) {
        this.seed = seed;
        this.difficulty = Difficulty.getByName(difficulty);
        this.gameField = new HashMap<>();
        this.lastPath = new ArrayList<>();
        this.pathsText = pathsText;
        this.timeText = timeText;
        startTimer();
    }

    public int getGridSize() {
        return gridSize;
    }

    public void generate(GridLayout gridLayout, int screenWidth, int screenHeight) {
        this.gridLayout = gridLayout;
        long startTime = System.currentTimeMillis();
        boolean isModelValid = false;
        int localseed = seed;
        Random random = new Random(seed);
        while (!isModelValid && (System.currentTimeMillis() - startTime) < 5000) {
            int[][] model = generateModel(difficulty, localseed);
            isModelValid = validateModel(model);

            if (isModelValid) {
                int itemSize = Math.min(screenWidth, screenHeight) / gridSize;
                renderModel(gridLayout, model, itemSize);
            } else {
                localseed = random.nextInt();
            }
        }
        if (!isModelValid) {
            if (gridLayout != null) {
                Context context = gridLayout.getContext();
                if (context instanceof LevelActivity) {
                    ((LevelActivity) context).finish();
                }
            }
        }
    }

    private int[][] generateModel(Difficulty diff, int seed) {

        Random random = new Random(seed);

        gridSize = random.nextInt(diff.getMaxGridSize() - diff.getMinGridSize() + 1) + diff.getMinGridSize();
        int[][] model = new int[gridSize][gridSize];

        int totalCells = gridSize * gridSize;
        int remainingCells = totalCells;
        int walls = Math.min(random.nextInt(diff.getMaxWalls() - diff.getMinWalls() + 1) + diff.getMinWalls(), remainingCells);
        remainingCells -= walls;
        int bridges = Math.min(random.nextInt(diff.getMaxBridges() - diff.getMinBridges() + 1) + diff.getMinBridges(), remainingCells);
        remainingCells -= bridges;
        points = random.nextInt(diff.getMaxPoints() - diff.getMinPoints() + 1) + diff.getMinPoints();
        remainingCells -= points;
        int emptyCells = remainingCells;

        List<Integer> cells = new ArrayList<>();
        int colorCount = 1;
        for (int i = 0; i < points; i++) {
            cells.add(colorCount);
            cells.add(colorCount);
            colorCount++;
        }

        for (int i = 0; i < walls; i++) cells.add(-2);
        for (int i = 0; i < bridges; i++) cells.add(-1);
        for (int i = 0; i < emptyCells; i++) cells.add(0);
        cells = cells.subList(0, gridSize * gridSize);
        Collections.shuffle(cells, random);

        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                model[i][j] = cells.get(index++);
            }
        }
        StringBuilder b = new StringBuilder();
        for (int[] l : model) {
            for (int x : l) {
                b.append(x).append(" ");
            }
        }
        Log.d("Model", String.valueOf(gridSize));
        Log.d("Model", b.toString());
        return model;
    }

    private boolean validateModel(int[][] model) {
        LevelValidator validator = new LevelValidator(model);
        optimalPaths = (int) validator.validate() / 2;
        Log.d("Validator", String.valueOf(optimalPaths));
        return optimalPaths != -1;
    }

    private void renderModel(GridLayout gridLayout, int[][] model, int itemSize) {
        gridLayout.removeAllViews();
        int gridSize = model.length;
        gridLayout.setRowCount(gridSize);
        gridLayout.setColumnCount(gridSize);
        grid = new GameObject[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                ImageView imageView = createImageView(gridLayout, itemSize);
                GameObject gameObject;

                switch (model[i][j]) {
                    case 0:
                        gameObject = new Cell(i, j, imageView);
                        break;
                    case -2:
                        gameObject = new Wall(i, j, imageView);
                        break;
                    case -1:
                        gameObject = new Bridge(i, j, imageView);
                        break;
                    default:
                        gameObject = new Point(i, j, imageView);
                        break;
                }

                gameObject.updateTexture();
                gameField.put(imageView, gameObject);
                grid[i][j] = gameObject;
                gridLayout.addView(imageView);
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.d(TAG, "onTouch: " + event.getAction());
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                            case MotionEvent.ACTION_MOVE:
                                View touchedView = findViewAt(gridLayout, event.getRawX(), event.getRawY());
                                if (touchedView != null && gameField.containsKey(touchedView)) {
                                    GameObject gameObject = gameField.get(touchedView);
                                    if (gameObject instanceof Connectable) {
                                        addPointToLastPath(gameObject);
                                    }
                                }
                                return true;
                            case MotionEvent.ACTION_UP:
                                completePath();
                                return true;
                        }
                        return false;
                    }
                });
            }
        }
        updatePathsText();
    }

    private ImageView createImageView(GridLayout gridLayout, int itemSize) {
        ImageView imageView = new ImageView(gridLayout.getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = itemSize;
        params.height = itemSize;
        imageView.setLayoutParams(params);
        return imageView;
    }

    private List<Integer> getShuffledColors(Context context, int seed) {
        List<Integer> colors = new ArrayList<>();
        TypedArray ta = context.getResources().obtainTypedArray(R.array.colors);
        for (int i = 0; i < ta.length(); i++) {
            colors.add(ta.getColor(i, Color.BLACK));
        }
        ta.recycle();

        Collections.shuffle(colors, new Random(seed));
        return colors;
    }

    private View findViewAt(GridLayout gridLayout, float x, float y) {
        int[] location = new int[2];
        gridLayout.getLocationOnScreen(location);
        int relX = (int) x - location[0];
        int relY = (int) y - location[1];

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (relX >= child.getLeft() && relX <= child.getRight() &&
                    relY >= child.getTop() && relY <= child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    public GameObject getGameObjectAt(int x, int y) {
        if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length) {
            return grid[x][y];
        }
        return null;
    }

    public boolean areNeighbors(GameObject obj1, GameObject obj2) {
        int dx = Math.abs(obj1.getX() - obj2.getX());
        int dy = Math.abs(obj1.getY() - obj2.getY());
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    public void addPointToLastPath(GameObject object) {
        synchronized (pathLock) {
            if (lastPath.isEmpty() || (areNeighbors((GameObject) (lastPath.get(lastPath.size() - 1)), object))) {
                Connectable connectable = (Connectable) object;
                if (lastPath.isEmpty()) {
                    if (object instanceof Point) {
                        Point p = (Point) object;
                        if (p.getConnections().isEmpty())
                            lastPath.add(connectable);
                    }
                } else {
                    if (lastPath.get(lastPath.size() - 1).canConnect(connectable) && connectable.canConnect(lastPath.get(lastPath.size() - 1))) {
                        try {
                            connectable.connect(lastPath.get(lastPath.size() - 1));
                            lastPath.get(lastPath.size() - 1).connect(connectable);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        lastPath.add(connectable);
                        if (object instanceof Point) {
                            completePath();
                        }
                    }
                }
            }
        }
    }

    public void completePath() {
        synchronized (pathLock) {
            if (lastPath.size() > 1 && lastPath.get(lastPath.size() - 1) instanceof Point) {
                completedPaths++;
                updatePathsText();
                if (completedPaths >= points) {
                    finishGame();
                }
            } else {
                for (Connectable connectable : lastPath) {
                    connectable.clearConnections();
                }
            }
            lastPath.clear();
        }
    }

    public void updatePathsText() {
        pathsText.setText(completedPaths + "/" + points);
    }

    private void startTimer() {
        handler.postDelayed(timerRunnable, 1000);
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedTime++;
            timeText.setText(String.format("Time: %d sec", elapsedTime));
            handler.postDelayed(this, 1000);
        }
    };

    public void stopTimer() {
        handler.removeCallbacks(timerRunnable);
    }

    public void finishGame() {
        stopTimer();
        saveResult(gridLayout.getContext(), calculateScore());
        showResultsDialog();
    }

    private void showResultsDialog() {
        Context context = gridLayout.getContext();
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_results, null);

        TextView resultText = dialogView.findViewById(R.id.result_text);
        ImageView star1 = dialogView.findViewById(R.id.star_1);
        ImageView star2 = dialogView.findViewById(R.id.star_2);
        ImageView star3 = dialogView.findViewById(R.id.star_3);
        TextView scoreText = dialogView.findViewById(R.id.score_text);
        Button toMain = dialogView.findViewById(R.id.btn_main_menu);
        if (completedPaths >= points) {
            star1.setColorFilter(ContextCompat.getColor(context, R.color.star_color), android.graphics.PorterDuff.Mode.SRC_IN);
            if (elapsedTime <= difficulty.getOptimalTime()) {
                star2.setColorFilter(ContextCompat.getColor(context, R.color.star_color), android.graphics.PorterDuff.Mode.SRC_IN);
                if (getPathsCount() >= optimalPaths * difficulty.getPathLengthPercentage()) {
                    star3.setColorFilter(ContextCompat.getColor(context, R.color.star_color), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        }
        resultText.setText("Результат");
        int score = calculateScore();
        scoreText.setText("Счет: " + score);
        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LevelActivity) context).finish();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setCancelable(false);
        builder.show();
    }

    private int calculateScore() {
        int pathScore = (int) ((completedPaths / (float) points) * 100);
        int optimalPathScore = (int) ((optimalPaths / (float) getPathsCount()) * 100);
        int timeScore = (elapsedTime <= difficulty.getOptimalTime()) ? 100 : Math.max(0, 100 - ((elapsedTime - difficulty.getOptimalTime()) * 2));

        return (int) (pathScore * 0.4 + optimalPathScore * 0.4 + timeScore * 0.2);
    }


    public int getSeed() {
        return seed;
    }

    private int getPathsCount() {
        int paths = 0;
        for (GameObject object : gameField.values()) {
            if (object instanceof Connectable) {
                Connectable connectable = (Connectable) object;
                paths += connectable.getPathsCount();
            }
        }
        return paths;
    }

    public void saveResult(Context context, int result) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("game_results", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json = sharedPreferences.getString("results", "");
        List<String> resultList = new ArrayList<>();
        if (!json.isEmpty()) {
            String[] resultsArray = json.split(";");
            for (String s : resultsArray) {
                resultList.add(s);
            }
        }

        String imagePath = saveGridImage(context);

        String newResult = seed + "," + difficulty.getName() + "," + result + "," + imagePath;
        resultList.add(newResult);

        StringBuilder newJson = new StringBuilder();
        for (String res : resultList) {
            newJson.append(res).append(";");
        }

        // Save the updated results
        editor.putString("results", newJson.toString());
        editor.apply();
    }

    private String saveGridImage(Context context) {
        // Create a bitmap of the current grid
        Bitmap bitmap = Bitmap.createBitmap(gridLayout.getWidth(), gridLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        gridLayout.draw(canvas);

        // Save the bitmap to the external storage
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "GridImages");
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
}
