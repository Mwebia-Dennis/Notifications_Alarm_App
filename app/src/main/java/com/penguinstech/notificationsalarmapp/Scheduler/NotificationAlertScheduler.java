package com.penguinstech.notificationsalarmapp.Scheduler;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.penguinstech.notificationsalarmapp.R;
import com.penguinstech.notificationsalarmapp.RoomDb.AppDatabase;
import com.penguinstech.notificationsalarmapp.RoomDb.Constants;
import com.penguinstech.notificationsalarmapp.RoomDb.MyNotification;

import java.util.Calendar;

public class NotificationAlertScheduler extends BroadcastReceiver {

    public NotificationAlertScheduler() {

//        android.os.Debug.waitForDebugger();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class, Constants.DATABASE_NAME).build();//load room db
        new Thread(()->{

            MyNotification notification = db.notificationDao().getNotificationById(intent.getIntExtra("id", 1));
            showNotification(context, notification.message, notification.id);
        }).start();
    }


    public void setScheduler(Context context, Calendar time, int requestCode) {


        //check if the scheduler has been set
        //if not set the scheduler
        if (!isSchedulerSet(context, requestCode))
        {
            AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationAlertScheduler.class);
            //set action so as the onReceive is triggered
            //the action should be the same as the declared action name in the manifest
            intent.setAction("com.penguinstech.notificationsalarmapp.scheduler.reminder");
            intent.putExtra("id", requestCode);
            PendingIntent pi = PendingIntent.getBroadcast(context, requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);//note flag_update_current which tells system how to handle new and existing pending intent
            am.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pi); // Millisec * Second * Minute

        }
    }

    private boolean isSchedulerSet(Context context, int requestCode) {
        return (PendingIntent.getBroadcast(context, requestCode,
                new Intent(context, NotificationAlertScheduler.class),
                PendingIntent.FLAG_NO_CREATE) != null);
    }


    private void showNotification(Context context, String message, int notifId) {

        final String NOTIFICATION_CHANNEL_ID = "com.penguinstech.notificationsalarmapp.scheduler.reminder";
        final String channelName = "Scheduler Notifications";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setContentTitle("New Appointment Request")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(message)
                .setAutoCancel(true)
                .setOngoing(false)
                .setCategory(Notification.CATEGORY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            notificationBuilder.setPriority(NotificationManager.IMPORTANCE_MIN);
        }
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notifId, notificationBuilder.build());
    }
}
