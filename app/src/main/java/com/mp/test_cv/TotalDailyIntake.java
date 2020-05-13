package com.mp.test_cv;

public class TotalDailyIntake {
    private int totalCalories;
    private int totalCarbohydrate;
    private int totalProtein;
    private int totalFat;
    private int totalSaturatedFat; //포화지방
    private int totalSugar;
    private int totalSodium;
    private int totalDietaryFiber;

    public TotalDailyIntake(int totalCalories, int totalCarbohydrate, int totalProtein, int totalFat, int totalSaturatedFat
    , int totalSugar, int totalSodium, int totalDietaryFiber) {
        this.totalCalories = totalCalories;
        this.totalCarbohydrate = totalCarbohydrate;
        this.totalProtein = totalProtein;
        this.totalFat = totalFat;
        this.totalSaturatedFat = totalSaturatedFat;
        this.totalSugar = totalSugar;
        this.totalSodium = totalSodium;
        this.totalDietaryFiber = totalDietaryFiber;
    }

    public int gettotalCalories() {
        return this.totalCalories;
    }
    public void settotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }
    public int gettotalCarbohydrate() {
        return this.totalCarbohydrate;
    }
    public void settotalCarbohydrate(int totalCarbohydrate) {
        this.totalCarbohydrate = totalCarbohydrate;
    }
    public int gettotalProtein() {
        return this.totalProtein;
    }
    public void settotalProtein(int totalProtein) {
        this.totalProtein = totalProtein;
    }
    public int gettotalFat() {
        return this.totalFat;
    }
    public void settotalFat(int totalFat) {
        this.totalFat = totalFat;
    }
    public int gettotalSaturatedFat() {
        return this.totalSaturatedFat;
    }
    public void settotalSaturatedFat(int totalSaturatedFat) {
        this.totalSaturatedFat = totalSaturatedFat;
    }
    public int gettotalSugar() {
        return this.totalSugar;
    }
    public void settotalSugar(int totalSugar) {
        this.totalSugar = totalSugar;
    }
    public int gettotalSodium() {
        return this.totalSodium;
    }
    public void settotalSodium(int totalSodium) {
        this.totalSodium = totalSodium;
    }
    public int gettotalDietaryFiber() {
        return this.totalDietaryFiber;
    }
    public void settotalDietaryFiber(int totalDietaryFiber) {
        this.totalDietaryFiber = totalDietaryFiber;
    }
}
