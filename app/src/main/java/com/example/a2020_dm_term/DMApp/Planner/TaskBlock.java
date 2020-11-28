package com.example.a2020_dm_term.DMApp.Planner;

class TaskBlock {
    int period;
    int hour = -1;
    int day = -1;
    String title;

    public int getPeriod() { return period; }
    public void setPeriod(int period) { this.period = period; }
    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }
    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}