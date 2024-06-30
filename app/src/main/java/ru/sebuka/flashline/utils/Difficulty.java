package ru.sebuka.flashline.utils;

import java.util.Random;

public enum Difficulty {
    EASY("Легкий", 2, 3, 5, 16, 0, 9, 5, 8, 300, 0.7),
    MEDIUM("Средний", 5, 10, 2, 5, 1, 2, 10, 12, 600, 0.8),
    HARD("Тяжелый", 8, 15, 5, 10, 3, 5, 12, 16, 900, 0.9);

    private final String name;
    private final int minPoints;
    private final int maxPoints;
    private final int minWalls;
    private final int maxWalls;
    private final int minBridges;
    private final int maxBridges;
    private final int minGridSize;
    private final int maxGridSize;
    private final int optimalTime;
    private final double pathLengthPercentage;

    Difficulty(String name, int minPoints, int maxPoints, int minWalls, int maxWalls, int minBridges, int maxBridges, int minGridSize, int maxGridSize, int optimalTime, double pathLengthPercentage) {
        this.name = name;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.minWalls = minWalls;
        this.maxWalls = maxWalls;
        this.minBridges = minBridges;
        this.maxBridges = maxBridges;
        this.minGridSize = minGridSize;
        this.maxGridSize = maxGridSize;
        this.optimalTime = optimalTime;
        this.pathLengthPercentage = pathLengthPercentage;
    }

    public String getName() {
        return name;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public int getMinWalls() {
        return minWalls;
    }

    public int getMaxWalls() {
        return maxWalls;
    }

    public int getMinBridges() {
        return minBridges;
    }

    public int getMaxBridges() {
        return maxBridges;
    }

    public int getMinGridSize() {
        return minGridSize;
    }

    public int getMaxGridSize() {
        return maxGridSize;
    }

    public int getOptimalTime() {
        return optimalTime;
    }

    public double getPathLengthPercentage() {
        return pathLengthPercentage;
    }

    public static Difficulty getByName(String name) {
        for (Difficulty difficulty : Difficulty.values()) {
            if (difficulty.getName().equalsIgnoreCase(name)) {
                return difficulty;
            }
        }
        return null;
    }

    public int[] getAdjustedComponents(int totalCells) {
        Random random = new Random();
        int points = Math.min(random.nextInt(maxPoints - minPoints + 1) + minPoints, 8);
        int remainingCells = totalCells - points;
        int walls = Math.min(random.nextInt(maxWalls - minWalls + 1) + minWalls, remainingCells);
        remainingCells -= walls;
        int bridges = Math.min(random.nextInt(maxBridges - minBridges + 1) + minBridges, remainingCells);
        remainingCells -= bridges;
        int emptyCells = remainingCells;

        return new int[]{points, walls, bridges, emptyCells};
    }
}
