package com.mp.test_cv;

public class MemberInfo {
    private float height;
    private float weight;
    private int age;
    private double activityMeasure;
    private int gender; // 0 female 1 male
    private double bmi;
    public MemberInfo(float height, float weight, int age, double activityMeasure, int gender, double bmi) {
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.activityMeasure = activityMeasure;
        this.gender = gender;
        this.bmi = bmi;
    }
    public MemberInfo() {}

    public float getHeight() {
        return this.height;
    }
    public void setHeight(float height) {
        this.height = height;
    }
    public float getWeight() {
        return this.weight;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }
    public int getAge() {
        return this.age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public double getActivityMeasure() {
        return this.activityMeasure;
    }
    public void setActivityMeasure(double activityMeasure) {
        this.activityMeasure = activityMeasure;
    }
    public int getGender() {
        return this.gender;
    }
    public void setGender(int gender) {
        this.gender = gender;
    }
    public double getBmi() {
        return this.bmi;
    }
    public void setBmi(double bmi) { this.bmi = bmi; }
}
