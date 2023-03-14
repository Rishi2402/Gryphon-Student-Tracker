package com.example.studenttracker;

import java.util.ArrayList;

public class CourseModel {

    String course;
    boolean isEnabled;

    public CourseModel(){

    }

    public CourseModel(String string, boolean isEnabled){
        course = string;
        this.isEnabled = isEnabled;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public String getCourse() {
        return course;
    }

    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }

    public boolean checkIsEnabled(){
        return isEnabled;
    }
}
