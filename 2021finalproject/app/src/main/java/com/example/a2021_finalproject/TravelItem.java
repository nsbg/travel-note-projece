package com.example.a2021_finalproject;

public class TravelItem {
    String place;
    String date;
    float rate;
    int resId;

    public TravelItem(String place, String date, float rate, int resId) {
        this.place = place;
        this.date = date;
        this.rate = rate;
        this.resId = resId;
    }

    public String getPlace() { return place; }

    public void setPlace(String place) { this.place = place; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public float getRate() { return rate; }

    public void setRate(float rate) { this.rate = rate; }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
