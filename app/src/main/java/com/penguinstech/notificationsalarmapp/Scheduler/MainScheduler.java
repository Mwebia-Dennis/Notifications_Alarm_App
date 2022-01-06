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
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.penguinstech.notificationsalarmapp.R;

import java.util.Calendar;

public class MainScheduler extends BroadcastReceiver {

//    public MainScheduler() {
//        android.os.Debug.waitForDebugger();
//    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //send notification
        Log.i("received", "true");
        showNotification(context, "Notification Alert", 1);
    }

    public void setScheduler(Context context) {


        //check if the scheduler has been set
        //if not set the scheduler
        if (!isSchedulerSet(context))
        {
            //set up the alarm manager and the reference point ie pending intent
            //set repeating scheduler which repeats after 24 hours at midnight
            Calendar midnight = Calendar.getInstance();
//            midnight.set(Calendar.HOUR_OF_DAY, 12);
//            midnight.set(Calendar.AM_PM, Calendar.AM);
            midnight.set(Calendar.HOUR, 6);
            midnight.set(Calendar.MINUTE, 0);
            midnight.set(Calendar.SECOND, 0);
            midnight.set(Calendar.AM_PM, Calendar.PM);

            AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, MainScheduler.class);
            //set action so as the onReceive is triggered
            //the action should be the same as the declared action name in the manifest
            intent.setAction("com.penguinstech.notificationsalarmapp.scheduler");
            PendingIntent pi = PendingIntent.getBroadcast(context, 0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);//note flag_update_current which tells system how to handle new and existing pending intent
            am.setRepeating(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute

        }
    }

    private boolean isSchedulerSet(Context context) {
        return (PendingIntent.getBroadcast(context, 0,
                new Intent(context, MainScheduler.class),
                PendingIntent.FLAG_NO_CREATE) != null);
    }

    public void cancelScheduler(Context context)
    {
        //cancelling the scheduler when needed
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(context, 0,
                new Intent(context, MainScheduler.class),
                PendingIntent.FLAG_UPDATE_CURRENT));
    }


    private void showNotification(Context context, String message, int notifId) {

        final String NOTIFICATION_CHANNEL_ID = "com.penguinstech.notificationsalarmapp.scheduler";
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
