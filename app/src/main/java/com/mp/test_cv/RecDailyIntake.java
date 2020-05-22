package com.mp.test_cv;

public class RecDailyIntake {
    private int recCalories;
    private int recCarbohydrate;
    private int recDietaryFiber;
    private int recFat;
    private int recProtein;
    private int recSaturatedFat;
    private int recSodium;
    private int recSugar;
    public RecDailyIntake(int recCalories, int recCarbohydrate, int recProtein, int recFat, int recSaturatedFat
            , int recSugar, int recSodium, int recDietaryFiber) {
        this.recCalories = recCalories;
        this.recCarbohydrate = recCarbohydrate;
        this.recProtein = recProtein;
        this.recFat = recFat;
        this.recSaturatedFat = recSaturatedFat;
        this.recSugar = recSugar;
        this.recSodium = recSodium;
        this.recDietaryFiber = recDietaryFiber;
    }
    public RecDailyIntake(){}

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
    public int getrecSaturatedFat() {
        return this.recSaturatedFat;
    }
    public void setrecSaturatedFat(int recSaturatedFat) {
        this.recSaturatedFat = recSaturatedFat;
    }
    public int getrecSugar() {
        return this.recSugar;
    }
    public void setrecSugar(int recSugar) {
        this.recSugar = recSugar;
    }
    public int getrecSodium() {
        return this.recSodium;
    }
    public void setrecSodium(int recSodium) {
        this.recSodium = recSodium;
    }
    public int getrecDietaryFiber() {
        return this.recDietaryFiber;
    }
    public void setrecDietaryFiber(int recDietaryFiber) {
        this.recDietaryFiber = recDietaryFiber;
    }
    }
