package com.example.studenttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.studenttracker.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TasksDetailActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Calendar calendar = Calendar.getInstance();
    Calendar due_calendar = Calendar.getInstance();
    TimePickerDialog timePickerDialog;
    EditText date_time_in, taskTitle, taskDesc;
    String reminderDate = new String();
    String reminder = new String();
    FirebaseService fs = new FirebaseService();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button addTask, cancelTask;
    TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_tasks_detail);

        createNotificationChannel();

        date_time_in = findViewById(R.id.date_time_input);
        date_time_in.setInputType(InputType.TYPE_NULL);
        date_time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog(date_time_in);
            }
        });


        taskTitle = findViewById(R.id.editTextTask);
        taskDesc = findViewById(R.id.editTextDescription);
        addTask = findViewById(R.id.addTaskBtn);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(taskTitle.getText().toString()))
                {
                    Toast.makeText(TasksDetailActivity.this, "Empty field not allowed!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addTask(taskTitle.getText().toString(), taskDesc.getText().toString(), date_time_in.getText().toString(), reminder);
                    Intent taskIntent = new Intent(TasksDetailActivity.this, TaskActivity.class);
                    startActivity(taskIntent);
                }

            }
        });
        cancelTask = findViewById(R.id.cancelTaskBtn);
        cancelTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent taskIntent = new Intent(TasksDetailActivity.this, TaskActivity.class);
                startActivity(taskIntent);
            }
        });

        ImageButton reminderBtn = findViewById(R.id.TasksReminderBtn);

        reminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        /*calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        Log.d("remindermonth", String.valueOf(month));
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);*/

                        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                /*calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                calendar.set(Calendar.MINUTE,minute);*/
                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                reminderDate = simpleDateFormat.format(calendar.getTime());
                                reminder = String.valueOf(calendar.getTimeInMillis());
                            }
                        };
                        timePickerDialog = new TimePickerDialog(TasksDetailActivity.this,timeSetListener,calendar
                                .get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                        timePickerDialog.show();
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(TasksDetailActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
    }

    public void setReminder(String reminder, String due_date, String Title) {
        //int randomID = (int) Math.random();
        int randomID = (int) System.currentTimeMillis();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("courseName", GlobalClass.courseName);
        intent.putExtra("taskTitle",taskTitle.getText().toString());
        intent.putExtra("dueDate", due_date);
        intent.putExtra("title",Title);
        Log.d("setRemindercourseName", GlobalClass.courseName);
        Log.d("setRemindertaskTitle", taskTitle.getText().toString());
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

    public void showDateTimeDialog(EditText date_time_in)
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                /*due_calendar.set(Calendar.YEAR,year);
                due_calendar.set(Calendar.MONTH,month);
                due_calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);*/

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        /*due_calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        due_calendar.set(Calendar.MINUTE,minute);*/
                        due_calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        date_time_in.setText(simpleDateFormat.format(due_calendar.getTime()));
                    }
                };
                new TimePickerDialog(TasksDetailActivity.this,timeSetListener,due_calendar.get(Calendar.HOUR_OF_DAY),
                        due_calendar.get(Calendar.MINUTE),false).show();
            }
        };
        new DatePickerDialog(TasksDetailActivity.this,dateSetListener,due_calendar.get(Calendar.YEAR),
                due_calendar.get(Calendar.MONTH), due_calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void addTask(String Title, String Desc, String date_time, String reminder){

        Map<String, Object> data = new HashMap<>();
        data.put("title", Title);
        data.put("description", Desc);
        data.put("due_date", date_time);
        data.put("isEnabled", true);
        data.put("reminder", reminder);

        Boolean bool = db.collection("courses").document(GlobalClass.courseDocID).collection("Tasks")
                .whereEqualTo("title",Title).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String taskDocID = task.getResult().getDocuments().get(0).getId();
                        GlobalClass.setTaskDocID(taskDocID);
                    }
                })
                .isSuccessful();
        if(!bool) {
            db.collection("courses").document(GlobalClass.courseDocID).collection("Tasks").add(data);
            if(!reminder.isEmpty()){
                setReminder(reminder,date_time,Title);
                Toast.makeText(TasksDetailActivity.this, "Reminder set successfully!", Toast.LENGTH_SHORT).show();
            }
        }else {
            TaskActivity taskActivity = new TaskActivity();
            taskActivity.updateTask(Title, Desc, date_time, reminder);
        }
    }

}