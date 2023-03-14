package com.example.studenttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.Inflater;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TaskActivity extends AppCompatActivity implements RecyclerViewCourseInterface {

    RecyclerView tasksRecyclerView;
    FloatingActionButton fabTasks;
    TaskAdapter taskAdapter;
    String deletedTaskTitle, deletedTaskDesc, deletedTaskdate_time, deletedReminder, reminderTask, dueDate;
    FirebaseService fs = new FirebaseService();
    ArrayList<TaskModel> taskList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Calendar calendar = Calendar.getInstance();
    String reminderString = new String();
    private AlarmManager alarmManager;
    PendingIntent pendingIntent;
    EditText title, description, due_date;
    ImageButton reminderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        //Log.d("courseNameXXXX", GlobalClass.getCourseName());
        createNotificationChannel();

        tasksRecyclerView = findViewById(R.id.TaskContainer);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        taskAdapter = new TaskAdapter(taskList, this);
        tasksRecyclerView.setAdapter(taskAdapter);

        getTasksList(GlobalClass.courseName);

        fabTasks = findViewById(R.id.fabTasks);
        fabTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addTask = new Intent(TaskActivity.this, TasksDetailActivity.class);
                startActivity(addTask);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);
    }

    @Override
    public void onItemClick(int position) {
        String taskTitle = taskList.get(position).getTitle();
        db.collection("courses").document(GlobalClass.courseDocID).collection("Tasks").document();

    }

    @Override
    public void onImageClick(int position) {

    }

    public void getTasksList(String courseName){
        taskList.clear();
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
                                        for (DocumentSnapshot ds : list) {
                                            TaskModel objModel = new TaskModel
                                                    (ds.getString("title"), ds.getString("description"),
                                                            ds.getString("due_date"), ds.getBoolean("isEnabled"),
                                                            ds.getString("reminder"));
                                            if(objModel.checkIsEnabled()) {
                                                Log.d("tasklist", String.valueOf(objModel.checkIsEnabled()));
                                                taskList.add(objModel);
                                            }
                                        }

                                        taskAdapter.notifyDataSetChanged();
                                    }
                                });


                    }
                } );
    }

    public void DeleteTask(String TaskTitleName)
    {
        Log.d("Delete TaskTitleName", TaskTitleName);
        CollectionReference taskColref =  FirebaseFirestore.getInstance().collection("courses")
                .document(GlobalClass.courseDocID).collection("Tasks");
        taskColref.whereEqualTo("title",TaskTitleName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {
                            Map<String, Object> data = new HashMap<>();
                            data.put("isEnabled", false);
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();
                            Log.d("taskdocid", documentID);
                            GlobalClass.setTaskDocID(documentID);
                            taskColref.document(documentID).update(data);
                            taskAdapter.notifyDataSetChanged();
                            getTasksList(GlobalClass.courseName);
                        }
                    }
                });
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
                    setContentView(R.layout.activity_tasks_detail);

                    Button editTask = findViewById(R.id.addTaskBtn);
                    editTask.setText("Edit Task");
                    Button cancelEdit = findViewById(R.id.cancelTaskBtn);
                    cancelEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(TaskActivity.this, TaskActivity.class);
                            startActivity(intent);
                        }
                    });

                    title = findViewById(R.id.editTextTask);
                    description = findViewById(R.id.editTextDescription);
                    due_date = findViewById(R.id.date_time_input);
                    reminderBtn = findViewById(R.id.TasksReminderBtn);


                    title.setText(taskList.get(position).getTitle());
                    description.setText(taskList.get(position).getDescription());
                    due_date.setText(taskList.get(position).getDue_date());
                    reminderString = taskList.get(position).getReminder();

                    due_date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDateTimeDialog("due_date");
                            /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            due_date.setText(simpleDateFormat.format(dueCalender.getTime()));*/
                        }
                    });

                    reminderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDateTimeDialog("reminder");

                            /*reminderString = String.valueOf(reminderCalender.getTimeInMillis());*/
                        }
                    });

                    CollectionReference taskColref =  FirebaseFirestore.getInstance().collection("courses")
                            .document(GlobalClass.courseDocID).collection("Tasks");
                    taskColref.whereEqualTo("title", title.getText().toString()).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            String documentID = task.getResult().getDocuments().get(0).getId();
                                            GlobalClass.setTaskDocID(documentID);
                                        }
                                    });

                    editTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reminderTask = title.getText().toString();
                            dueDate = due_date.getText().toString();
                            updateTask(title.getText().toString(), description.getText().toString(),
                                    due_date.getText().toString(), reminderString);
                            Intent intent = new Intent(TaskActivity.this, TaskActivity.class);
                            startActivity(intent);
                        }
                    });

                    break;
                case ItemTouchHelper.RIGHT:
                    deletedTaskTitle = taskList.get(position).getTitle();
                    deletedTaskDesc = taskList.get(position).getDescription();
                    deletedTaskdate_time = taskList.get(position).getDue_date();
                    deletedReminder = taskList.get(position).getReminder();

                    DeleteTask(deletedTaskTitle);
                    taskAdapter.notifyItemRemoved(position);

                    Snackbar.make(tasksRecyclerView, deletedTaskTitle, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Undo", new View.OnClickListener(){

                                @Override
                                public void onClick(View view) {
                                    reminderTask = deletedTaskTitle;
                                    dueDate = deletedTaskdate_time;
                                    updateTask(deletedTaskTitle, deletedTaskDesc, deletedTaskdate_time, deletedReminder);
                                }
                            }).show();
                    break;

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
                viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(TaskActivity.this,R.color.red))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(TaskActivity.this,R.color.green))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_edit_24)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void updateTask(String title, String description, String due_date, String reminder){

        CollectionReference taskColref =  FirebaseFirestore.getInstance().collection("courses")
                .document(GlobalClass.courseDocID).collection("Tasks");
        Log.d("title", title);
        Log.d("desc", description);
        Log.d("duedate", due_date);
        taskColref.document(GlobalClass.taskDocID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Map<String, Object> data = new HashMap<>();
                            data.put("title", title);
                            data.put("description", description);
                            data.put("due_date", due_date);
                            data.put("isEnabled", true);
                            data.put("reminder", reminder);
                            if (!reminder.isEmpty()){
                                setReminder(reminder,due_date,title);
                                Toast.makeText(TaskActivity.this, "Reminder set successfully!", Toast.LENGTH_SHORT).show();
                            }
                            taskColref.document(GlobalClass.taskDocID).update(data);
                            getTasksList(GlobalClass.courseName);
                            taskAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void showDateTimeDialog(String str)
    {
        DatePickerDialog datePickerDialog = null;
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                /*calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);*/

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        /*calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);*/
                        calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                        Log.d("editcalender", String.valueOf(calendar.getTime()));
                        if(str.equalsIgnoreCase("due_date")){
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            due_date.setText(simpleDateFormat.format(calendar.getTime()));
                        }else if(str.equalsIgnoreCase("reminder")){
                            reminderString = String.valueOf(calendar.getTimeInMillis());
                        }

                    }

                };
                new TimePickerDialog(TaskActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };
        datePickerDialog =  new DatePickerDialog(TaskActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
        Log.d("editcalender", String.valueOf(calendar.getTime()));

    }

    public void setReminder(String reminder, String due_date, String Title) {
        //int randomID = (int) Math.random();
        int randomID = (int) System.currentTimeMillis();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("courseName", GlobalClass.courseName);
        intent.putExtra("taskTitle",title.getText().toString());
        intent.putExtra("dueDate", due_date);
        intent.putExtra("title",Title);
        Log.d("setRemindercourseName", GlobalClass.courseName);
        Log.d("setRemindertaskTitle", title.getText().toString());
        Log.d("setReminderdueDate", due_date);
        Log.d("setReminderTitle", Title);

        pendingIntent = PendingIntent.getBroadcast(this, randomID, intent, PendingIntent.FLAG_IMMUTABLE);
        long l = Long.parseLong(reminder);
        calendar.setTimeInMillis(l);
        Log.d("millitime", String.valueOf(calendar.getTimeInMillis()));
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("ntificationchannel", "createNotificationChannel: ");
            CharSequence name = "taskreminderChannel";
            String description = "Channel for task reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("taskreminder",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}