package ru.sebuka.flashline.models;

import java.util.List;

public class User {
    private String googleId;
    private String name;
    private String desc;
    private String img;
    private List<String> friendlist;
    private int mmr;
    private int rating;

    // Геттеры и сеттеры для всех полей
    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<String> getFriendlist() {
        return friendlist;
    }

    public void setFriendlist(List<String> friendlist) {
        this.friendlist = friendlist;
    }

    public int getMmr() {
        return mmr;
    }

    public void setMmr(int mmr) {
        this.mmr = mmr;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
