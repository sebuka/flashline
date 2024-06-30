package ru.sebuka.flashline.models;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;

import ru.sebuka.flashline.R;

public class LevelModel {

    private int[][] model;
    private int seed;
    private int size;
    private int optimalPaths;
    private int time;
    private double pathsPersantage;
    private int points;

    public LevelModel(int size) {
        this.size = size;
        model = new int[size][size];
    }

    public int[][] getModel() {
        return model;
    }

    public int getSize() {
        return size;
    }

    public static LevelModel fromJson(String jsonString) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, LevelModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toJson() {
        try {
            Gson gson = new Gson();
            return gson.toJson(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LevelModel fromXML(int level, Context context) {
        try {
            XmlPullParser parser = context.getResources().getXml(R.xml.levels);
            int eventType = parser.getEventType();
            LevelModel currentLevel = null;
            int currentRow = 0;
            boolean insideRow = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("level".equals(tagName)) {
                            int number = Integer.parseInt(parser.getAttributeValue(null, "number"));
                            if (number == level) {
                                int size = Integer.parseInt(parser.getAttributeValue(null, "size"));
                                int time = Integer.parseInt(parser.getAttributeValue(null, "time"));
                                double pathsPercentage = Double.parseDouble(parser.getAttributeValue(null, "pathsPercentage"));
                                int seed = Integer.parseInt(parser.getAttributeValue(null, "seed"));
                                int points = Integer.parseInt(parser.getAttributeValue(null, "points"));
                                int optimalPaths = Integer.parseInt(parser.getAttributeValue(null, "optimalPaths"));

                                currentLevel = new LevelModel(size);
                                currentLevel.setTime(time);
                                currentLevel.setPathsPersantage(pathsPercentage);
                                currentLevel.setSeed(seed);
                                currentLevel.setPoints(points);
                                currentLevel.setOptimalPaths(optimalPaths);
                                currentRow = 0;
                                Log.d("LevelModel", "Found level: " + level);
                            }
                        } else if ("row".equals(tagName) && currentLevel != null) {
                            insideRow = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (currentLevel != null && insideRow) {
                            String[] numbers = parser.getText().split(",");
                            for (int i = 0; i < numbers.length; i++) {
                                currentLevel.model[currentRow][i] = Integer.parseInt(numbers[i].trim());
                            }
                            currentRow++;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("level".equals(tagName) && currentLevel != null) {
                            Log.d("LevelModel", "Completed level: " + level);
                            return currentLevel;
                        } else if ("row".equals(tagName) && currentLevel != null) {
                            insideRow = false;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e("LevelModel", "Error parsing level XML", e);
        }
        return null;
    }



    public int getOptimalPaths() {
        return optimalPaths;
    }

    public void setOptimalPaths(int optimalPaths) {
        this.optimalPaths = optimalPaths;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getPathsPersantage() {
        return pathsPersantage;
    }

    public void setPathsPersantage(double pathsPersantage) {
        this.pathsPersantage = pathsPersantage;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }
}
