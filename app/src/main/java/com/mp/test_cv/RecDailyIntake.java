package com.mp.test_cv;

public class RecDailyIntake {
    private int recCalories;
    private int recCarbohydrate;
    private int recProtein;
    private int recFat;
    public RecDailyIntake(int recCalories, int recCarbohydrate, int recProtein, int recFat) {
        this.recCalories = recCalories;
        this.recCarbohydrate = recCarbohydrate;
        this.recProtein = recProtein;
        this.recFat = recFat;
    }

    public int getrecCalories() {
        return this.recCalories;
    }
    public void setrecCalories(int recCalories) {
        this.recCalories = recCalories;
    }
    public int getrecCarbohydrate() {
        return this.recCarbohydrate;
    }
    public void setrecCarbohydrate(int recCarbohydrate) {
        this.recCarbohydrate = recCarbohydrate;
    }
    public int getrecProtein() {
        return this.recProtein;
    }
    public void setrecProtein(int recProtein) {
        this.recProtein = recProtein;
    }
    public int getrecFat() {
        return this.recFat;
    }
    public void setrecFat(int recFat) { this.recFat = recFat; }
    }
