package com.mp.test_cv;

public class PersonalDailyIntake {
    private int perCalories;
    private int perCarbohydrate;
    private int perProtein;
    private int perFat;
    private int perSaturatedFat; //포화지방
    private int perSugar;
    private int perSodium;
    private int perDietaryFiber;

    public PersonalDailyIntake(int perCalories, int perCarbohydrate, int perProtein, int perFat, int perSaturatedFat
    , int perSugar, int perSodium, int perDietaryFiber) {
        this.perCalories = perCalories;
        this.perCarbohydrate = perCarbohydrate;
        this.perProtein = perProtein;
        this.perFat = perFat;
        this.perSaturatedFat = perSaturatedFat;
        this.perSugar = perSugar;
        this.perSodium = perSodium;
        this.perDietaryFiber = perDietaryFiber;
    }

    public int getrecCalories() {
        return this.perCalories;
    }
    public void setrecCalories(int perCalories) {
        this.perCalories = perCalories;
    }
    public int getrecCarbohydrate() {
        return this.perCarbohydrate;
    }
    public void setrecCarbohydrate(int perCarbohydrate) {
        this.perCarbohydrate = perCarbohydrate;
    }
    public int getrecProtein() {
        return this.perProtein;
    }
    public void setrecProtein(int perProtein) {
        this.perProtein = perProtein;
    }
    public int getrecFat() {
        return this.perFat;
    }
    public void setrecFat(int perFat) { this.perFat = perFat; }
    public int getperSaturatedFat() {
        return this.perSaturatedFat;
    }
    public void setperSaturatedFat(int perSaturatedFat) { this.perSaturatedFat = perSaturatedFat; }
    public int getperSugar() {
        return this.perSugar;
    }
    public void setperSugar(int perSugar) { this.perSugar = perSugar; }
    public int getperSodium() {
        return this.perSodium;
    }
    public void setperSodium(int perSodium) { this.perSodium = perSodium; }
    public int getperDietaryFiber() {
        return this.perDietaryFiber;
    }
    public void setperDietaryFiber(int perDietaryFiber) { this.perDietaryFiber = perDietaryFiber; }
}
