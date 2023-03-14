package com.example.studenttracker;

import android.app.Application;

import java.util.ArrayList;

public class GlobalClass extends Application {

        public static String courseName;
        public static ArrayList<String> courseList = new ArrayList<>();
        public static ArrayList<String> taskList = new ArrayList<>();
        public static String courseDocID = new String();
        public static String taskDocID = new String();

    public static String getCourseName() {
        return courseName;
    }

    public static void setCourseName(String courseName) {
        GlobalClass.courseName = courseName;
    }

    public static ArrayList<String> getCourseList() {
        return courseList;
    }

    public static void setCourseList(ArrayList<String> courseList) {
        GlobalClass.courseList = courseList;
    }

    public static ArrayList<String> getTaskList() {
        return taskList;
    }

    public static void setTaskList(ArrayList<String> taskList) {
        GlobalClass.taskList = taskList;
    }

    public static void setCourseDocID(String courseDocID) {
        GlobalClass.courseDocID = courseDocID;
    }

    public static void setTaskDocID(String taskDocID) {
        GlobalClass.taskDocID = taskDocID;
    }
}
