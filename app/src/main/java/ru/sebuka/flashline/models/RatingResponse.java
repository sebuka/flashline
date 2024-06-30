package ru.sebuka.flashline.models;

import java.util.List;

public class RatingResponse {
    private List<String> topGoogleIds;

    public List<String> getTopGoogleIds() {
        return topGoogleIds;
    }

    public void setTopGoogleIds(List<String> topGoogleIds) {
        this.topGoogleIds = topGoogleIds;
    }
}
