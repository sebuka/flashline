package ru.sebuka.flashline.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ru.sebuka.flashline.R;
import ru.sebuka.flashline.gameobjects.Bridge;
import ru.sebuka.flashline.gameobjects.Cell;
import ru.sebuka.flashline.gameobjects.Connectable;
import ru.sebuka.flashline.gameobjects.GameObject;
import ru.sebuka.flashline.gameobjects.Point;
import ru.sebuka.flashline.gameobjects.Wall;
import ru.sebuka.flashline.models.LevelModel;

public class GameLevel {
    private static final String TAG = "GameLevel";
    private Map<View, GameObject> gameField;
    private GameObject[][] grid;
    private List<Connectable> lastPath;

    LevelModel model;
    private int completedPaths = 0;

    private int screenWidth, screenHeight;
    private int optimalPaths = 0;
    private GridLayout gridLayout;
    private final Object pathLock = new Object();
    private TextView pathsText;
    private TextView timeText;
    private Handler handler = new Handler();
    private int elapsedTime = 0;
    private Runnable onGameFinish;
    private Runnable timerRunnable;

    public GameLevel(LevelModel model, TextView pathsText, TextView timeText, int screenWidth, int screenHeight, Runnable onGameFinish, Runnable timer) {
        this.gameField = new HashMap<>();
        this.lastPath = new ArrayList<>();
        this.pathsText = pathsText;
        this.timeText = timeText;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.model = model;
        this.onGameFinish = onGameFinish;
        this.timerRunnable = timer;
        startTimer();
    }

    public int getGridSize() {
        return model.getSize();
    }


    public void renderModel(GridLayout gridLayout) {
        gridLayout.removeAllViews();
        int gridSize = getGridSize();
        gridLayout.setRowCount(gridSize);
        gridLayout.setColumnCount(gridSize);
        grid = new GameObject[gridSize][gridSize];
        int itemSize = Math.min(screenWidth, screenHeight) / gridSize;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                ImageView imageView = createImageView(gridLayout, itemSize);
                GameObject gameObject;

                switch (model.getModel()[i][j]) {
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
                if (completedPaths >= model.getPoints()) {
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
        pathsText.setText(completedPaths + "/" + model.getPoints());
    }

    private void startTimer() {
        handler.postDelayed(timerRunnable, 1000);
    }


    public void stopTimer() {
        handler.removeCallbacks(timerRunnable);
    }

    public void finishGame() {
        stopTimer();
        onGameFinish.run();
    }


    public int calculateScore() {
        int pathScore = (int) ((completedPaths / (float) model.getOptimalPaths()) * 100);
        int optimalPathScore = (int) ((optimalPaths / (float) getPathsCount()) * 100);
        int timeScore = (elapsedTime <= model.getTime()) ? 100 : Math.max(0, 100 - ((elapsedTime - model.getTime()) * 2));

        return (int) (pathScore * 0.4 + optimalPathScore * 0.4 + timeScore * 0.2);
    }


    public int getSeed() {
        return model.getSeed();
    }

    public int getPathsCount() {
        int paths = 0;
        for (GameObject object : gameField.values()) {
            if (object instanceof Connectable) {
                Connectable connectable = (Connectable) object;
                paths += connectable.getPathsCount();
            }
        }
        return paths;
    }

    public GridLayout getGridLayout() {
        return gridLayout;
    }

    public LevelModel getModel() {
        return model;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public int getCompletedPaths() {
        return completedPaths;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
