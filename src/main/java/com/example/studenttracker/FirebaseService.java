package com.example.studenttracker;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FirebaseService {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int count = 0;
    String documentID = new String();

    public void addCourse(String courseName) {

        Map<String, Object> data = new HashMap<>();
        data.put("course", courseName);
        data.put("isEnabled", true);
        db.collection("courses").add(data);
    }

    public int getTasksLength(String courseCollection){
        Log.d("getTaskLength", courseCollection);
        /*db.collection("courses").document(getCoursedocID(courseCollection)).collection("Tasks").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        count = queryDocumentSnapshots.size();
                    }
                });*/
        return count;
    }

    public void addTask(String Title, String Desc, String date_time){

        Map<String, Object> data = new HashMap<>();
        data.put("Title", Title);
        data.put("Description", Desc);
        data.put("Due date", date_time);
        /*getCoursedocID(GlobalClass.courseName);
        db.collection("courses").document(GlobalClass.courseDocID).collection("Tasks").add(data);*/

    }


    /*public ArrayList<String> getTaskList(String courseName) {
        *//*GlobalClass.getTaskList().clear();*//*

        getCoursedocID(courseName);
        Log.d("courseid", GlobalClass.courseDocID);
        db.collection("courses").document(coursedocID).collection("Tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                        for (DocumentSnapshot ds: list){
                            GlobalClass.taskList.add(ds.toString());
                        }
                    }
                });
        return GlobalClass.taskList;
    }*/

    /*public void getCoursedocID(String courseName){
        FirebaseFirestore.getInstance().collection("courses").whereEqualTo("course",courseName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);;
                        GlobalClass.setCourseDocID(documentSnapshot.getId());
                        Log.d("courseid", GlobalClass.courseDocID);

                        db.collection("courses").document(GlobalClass.courseDocID).collection("Tasks").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                                        for (DocumentSnapshot ds: list){
                                            GlobalClass.taskList.add(ds.toString());
                                        }
                                    }
                                });


                    }
                } );
    }*/



}
