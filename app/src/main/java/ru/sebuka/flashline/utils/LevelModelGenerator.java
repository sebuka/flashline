package ru.sebuka.flashline.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ru.sebuka.flashline.models.LevelModel;

public class LevelModelGenerator {

    public LevelModel generate(Difficulty difficulty, int seed) {
        long startTime = System.currentTimeMillis();
        boolean isModelValid = false;
        int localseed = seed;
        Random random = new Random(seed);
        LevelModel model = null;
        while (!isModelValid && (System.currentTimeMillis() - startTime) < 5000) {
            model = generateModel(difficulty, localseed);
            model.setOptimalPaths(validateModel(model));
            isModelValid = model.getOptimalPaths() != -1;
            model.setSeed(seed);
            if (!isModelValid) {
                localseed = random.nextInt();
            }
        }
        return model;
    }

    private LevelModel generateModel(Difficulty diff, int seed) {
        Random random = new Random(seed);

        int gridSize = random.nextInt(diff.getMaxGridSize() - diff.getMinGridSize() + 1) + diff.getMinGridSize();
        LevelModel model = new LevelModel(gridSize);

        int totalCells = gridSize * gridSize;
        int remainingCells = totalCells;
        int walls = Math.min(random.nextInt(diff.getMaxWalls() - diff.getMinWalls() + 1) + diff.getMinWalls(), remainingCells);
        remainingCells -= walls;
        int bridges = Math.min(random.nextInt(diff.getMaxBridges() - diff.getMinBridges() + 1) + diff.getMinBridges(), remainingCells);
        remainingCells -= bridges;
        int points = random.nextInt(diff.getMaxPoints() - diff.getMinPoints() + 1) + diff.getMinPoints();
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
        Collections.shuffle(cells, random);

        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                model.getModel()[i][j] = cells.get(index++);
            }
        }

        StringBuilder b = new StringBuilder();
        for (int[] row : model.getModel()) {
            for (int x : row) {
                b.append(x).append(" ");
            }
        }
        Log.d("Model", String.valueOf(gridSize));
        Log.d("Model", b.toString());

        model.setPoints(points);
        model.setTime(diff.getOptimalTime());
        model.setPathsPersantage(diff.getPathLengthPercentage());

        return model;
    }

    private int validateModel(LevelModel model) {
        LevelValidator validator = new LevelValidator(model);
        int optimalPaths = (int) validator.validate() / 2;
        return optimalPaths ;
    }
}
