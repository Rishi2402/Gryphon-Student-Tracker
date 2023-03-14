package com.example.studenttracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.studenttracker.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Console;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewCourseInterface{



    FloatingActionButton fab_courses;
    String courseText;
    Button add, cancel, edit, cancelEdit;
    LinearLayout layout;
    Dialog dialog, dialogEdit;
    ArrayList<CourseModel> courseList;
    RecyclerView recyclerView;
    TasksDetailActivity tasksDetailActivity = new TasksDetailActivity();
    FirebaseService fs = new FirebaseService();
    GryphAdapter objGryphAdapter;
    String deletedCourse,preEditCourse = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
/*
        tasksDetailActivity.createNotificationChannel();*/

        recyclerView = (RecyclerView) findViewById(R.id.courseContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //fs.listenToCourses();

        //getting list of course from firebase Start.
        courseList = new ArrayList<>();
            objGryphAdapter = new GryphAdapter(courseList,this);
        recyclerView.setAdapter(objGryphAdapter);
        //getting list of course from firebase End.
        courseList = getCourses();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        //layout = findViewById(R.id.courseContainer);

        dialog = new Dialog(this);
        buildDialog();

        EditText et = dialog.findViewById(R.id.addCourseText);

        fab_courses = findViewById(R.id.fabCourses);
        fab_courses.setOnClickListener(view ->  {
            et.setText(null);
            dialog.show();
        });

        //To add course
        add = dialog.findViewById(R.id.addCourseBtn);
        cancel = dialog.findViewById(R.id.cancelCourseBtn);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);

        //To Edit course

        dialogEdit = new Dialog(this);
        buildDialogEdit();
        edit = dialogEdit.findViewById(R.id.EditCourseBtn);
        cancelEdit = dialogEdit.findViewById(R.id.cancelEditBtn);

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|
            ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
        {
            int position = viewHolder.getAbsoluteAdapterPosition();
            switch (direction)
            {
                 case ItemTouchHelper.LEFT:
                     GlobalClass.setCourseName(courseList.get(position).getCourse());
                     buildDialogEdit();
                     EditText editCourse = dialogEdit.findViewById(R.id.EditCourseText);
                     editCourse.setText(GlobalClass.courseName);
                     dialogEdit.show();
                     break;
                 case ItemTouchHelper.RIGHT:
                     deletedCourse = courseList.get(position).getCourse();
                     /*courseList.remove(position);*/
                     GlobalClass.setCourseName(deletedCourse);
                     DeleteCourse(deletedCourse);
                     objGryphAdapter.notifyItemRemoved(position);

                     Snackbar.make(recyclerView, deletedCourse, Snackbar.LENGTH_INDEFINITE)
                             .setAction("Undo", new View.OnClickListener(){

                                 @Override
                                 public void onClick(View view) {
                                     //courseList.add(position,deletedCourse);
                                     UpdateCourse(deletedCourse);
//                                     courseList = getCourses();
//                                     objGryphAdapter.notifyDataSetChanged();
                                 }
                             }).show();
                     break;

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
                viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.red))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.green))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_edit_24)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void buildDialog() {
        dialog.setContentView(R.layout.add_course);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void buildDialogEdit() {
        dialogEdit.setContentView(R.layout.edit_course);
        dialogEdit.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /*public void addView(String text){
        View view = getLayoutInflater().inflate(R.layout.courses_layout,null);
        Button courseBtn = view.findViewById(R.id.courseButton);
        courseBtn.setText(text);
        layout.addView(view);
        objGryphAdapter.notifyDataSetChanged();
    }*/

    public ArrayList<CourseModel>  getCourses() {
        Log.d("Value:", "getCourses method called 1.");
        courseList.clear();
        FirebaseFirestore.getInstance().collection("courses").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("isSuccessful", String.valueOf(task.isSuccessful()));
                        if(task.isSuccessful())
                        {
                            List<DocumentSnapshot> list = task.getResult().getDocuments();
                            for(DocumentSnapshot d:list)
                            {
                                CourseModel objCourseModel = new CourseModel(d.getString("course"), d.getBoolean("isEnabled"));
                                Log.d("courseenabled", String.valueOf(objCourseModel.checkIsEnabled()));
                                if(objCourseModel.checkIsEnabled()) {
                                    courseList.add(objCourseModel);
                                }
                                Log.d("arraylist size:", String.valueOf(courseList.size()));
                                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    courseList.sort(Comparator.comparing(CourseModel::getCourse));
                                }*/
                                objGryphAdapter.notifyDataSetChanged();
//                                for(int i= 0; i < arrayList.size(); i++ ) {
//                                    addView(String.valueOf(arrayList.get(i).getCourse()));
//                                    Log.d("arraylist value:", String.valueOf(arrayList.get(i).getCourse()));
//                                }
                            }
                        }
                    }
                });

        return courseList;
    }

    public void DeleteCourse(String courseName)
    {
        Log.d("Delete CourseName", courseName);
        FirebaseFirestore.getInstance().collection("courses").whereEqualTo("course",courseName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("Delete Success", String.valueOf(task.isSuccessful()));
                        if(task.isSuccessful())
                        {
                            Map<String, Object> data = new HashMap<>();
                            /*data.put("course", editedCourseName);*/
                            data.put("isEnabled", false);
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();
                            FirebaseFirestore.getInstance().collection("courses")
                                    .document(documentID).update(data);

                            courseList = getCourses();
                            objGryphAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void UpdateCourse(String editedCourseName)
    {
        Log.d("Updated CourseName", editedCourseName);
        FirebaseFirestore.getInstance().collection("courses").whereEqualTo("course",GlobalClass.courseName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("Delete Success", String.valueOf(task.isSuccessful()));
                        if(task.isSuccessful())
                        {
                            Map<String, Object> data = new HashMap<>();
                            data.put("course", editedCourseName);
                            data.put("isEnabled", true);
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            GlobalClass.setCourseDocID(documentSnapshot.getId());
                            FirebaseFirestore.getInstance().collection("courses")
                                    .document(GlobalClass.courseDocID)
                                    .update(data);

                            courseList = getCourses();
                            objGryphAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        Log.d("onClickCalled","XXXXXX");
        switch (view.getId()) {
            case R.id.addCourseBtn:
                Log.d("ADD course", String.valueOf(view.getId()));
                EditText et = dialog.findViewById(R.id.addCourseText);
                if (TextUtils.isEmpty(et.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Empty field not allowed!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    courseText = et.getText().toString();
                    //addView(courseText);
                    fs.addCourse(courseText);
                    dialog.dismiss();
                    courseList = getCourses();
                    objGryphAdapter.notifyDataSetChanged();
                }
                break;
            /*case R.id.EditCourseBtn:
                Log.d("EDIT course", String.valueOf(view.getId()));
                EditText et1 = dialogEdit.findViewById(R.id.EditCourseText);
                if (TextUtils.isEmpty(et1.getText().toString())){
                    Toast.makeText(MainActivity.this, "Empty field not allowed!", Toast.LENGTH_SHORT).show();
                }else {
                    courseText = et1.getText().toString();
                    //addView(courseText);
                    UpdateCourse(preEditCourse,courseText);
                    dialogEdit.dismiss();
                    courseList = getCourses();
                    objGryphAdapter.notifyDataSetChanged();
                }
                break;*/
            case R.id.cancelCourseBtn:
                Log.d("CANCEL course", String.valueOf(view.getId()));
                dialogEdit.dismiss();
                courseList = getCourses();
                objGryphAdapter.notifyDataSetChanged();
                break;
            /*case R.id.cancelEditBtn:
                Log.d("CANCELEDIT course", String.valueOf(view.getId()));
                dialogEdit.dismiss();
                courseList = getCourses();
                objGryphAdapter.notifyDataSetChanged();
                break;*/
            default:
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.d("onItemClick","onItemClick called!!");
        Intent intent = new Intent(MainActivity.this, TaskActivity.class);
        /*intent.putExtra("courseName",courseList.get(position).getCourse());*/
        GlobalClass.setCourseName(courseList.get(position).getCourse());
        startActivity(intent);

    }

    @Override
    public void onImageClick(int position) {
        Log.d("onImageClick","onImageClick called!!");
        Intent intent = new Intent(MainActivity.this, TaskActivity.class);
        /*intent.putExtra("courseName",courseList.get(position).getCourse());*/
        GlobalClass.setCourseName(courseList.get(position).getCourse());
        startActivity(intent);
    }

    public void EditCourseClick(View view) {
        EditText et1 = dialogEdit.findViewById(R.id.EditCourseText);
        if (TextUtils.isEmpty(et1.getText().toString())) {
            Toast.makeText(MainActivity.this, "Empty field not allowed!", Toast.LENGTH_SHORT).show();
        } else {
            courseText = et1.getText().toString();
            UpdateCourse(courseText);
            dialogEdit.dismiss();
        }
    }

    public void cancelEditClick(View view) {
        dialogEdit.dismiss();
        courseList = getCourses();
        objGryphAdapter.notifyDataSetChanged();
    }

}