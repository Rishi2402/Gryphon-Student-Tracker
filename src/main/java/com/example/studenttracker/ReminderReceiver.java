package com.example.studenttracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class ReminderReceiver  extends BroadcastReceiver {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        int randomID = intent.getIntExtra("randomID",0);
        Log.d("randomIDRem", String.valueOf(randomID));
        String courseName = intent.getStringExtra("courseName");
        String task = intent.getStringExtra("taskTitle");
        String Title = intent.getStringExtra("title");
        String dueDate = intent.getStringExtra("dueDate");
        Log.d("courseNameRem", courseName);
        Log.d("taskTitleRem", task);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"taskreminder")
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle("Task Reminder")
                .setContentText("Course Name: "+courseName+ " Task Name: "+task+" is due on "+dueDate+" date")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        Log.d("notifydone", "onReceive: ");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManagerCompat.notify(m, builder.build());




    }


}
