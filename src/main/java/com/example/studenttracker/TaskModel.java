package com.example.studenttracker;

public class TaskModel {
    public String title, description, due_date, reminder;
    public boolean isEnabled;

    public TaskModel() {
    }

    public TaskModel(String title, String description, String due_date, boolean isEnabled, String reminder) {
        this.title = title;
        this.description = description;
        this.due_date = due_date;
        this.isEnabled = isEnabled;
        this.reminder = reminder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public boolean checkIsEnabled(){
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }
}
