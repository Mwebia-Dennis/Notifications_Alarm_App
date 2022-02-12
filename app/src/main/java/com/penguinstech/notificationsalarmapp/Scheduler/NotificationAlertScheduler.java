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
import androidx.room.Room;

import com.penguinstech.notificationsalarmapp.NotificationAdapter;
import com.penguinstech.notificationsalarmapp.R;
import com.penguinstech.notificationsalarmapp.RoomDb.AppDatabase;
import com.penguinstech.notificationsalarmapp.RoomDb.Constants;
import com.penguinstech.notificationsalarmapp.RoomDb.MyNotification;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

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

        if (time.compareTo(Calendar.getInstance()) > 0) {//if the notification time is beyond current time, set notification

            Calendar _30_minsBefore = (Calendar) time.clone();
            Calendar _5_minsBefore = (Calendar) _30_minsBefore.clone();
            // set 30 mins before and 5 mins before
            _30_minsBefore.add(Calendar.MINUTE, -30);//30 mins before
            Log.i("30 mins", NotificationAdapter.sdf.format(_30_minsBefore.getTime()));
            _5_minsBefore.add(Calendar.MINUTE, -5);//5 mins befo
//                        _2_minsBefore.add(Calendar.MINUTE, -2);//2 mins before
            if(_30_minsBefore.compareTo(Calendar.getInstance()) > 0) {
                //since we cannot use same requestCode for 2 notification
                // we create an unique code for
                setReminder(context, _30_minsBefore, requestCode);
            }
            if(_5_minsBefore.compareTo(Calendar.getInstance()) > 0) {
                setReminder(context, _5_minsBefore, getUniqueCode(requestCode));
            }else {
                setReminder(context, time, requestCode);
            }
        }


    }

    private void setReminder(Context context, Calendar time, int requestCode) {
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

    public void updateScheduler(Context context, Calendar time, int requestCode)
    {
        if(isSchedulerSet(context, requestCode)) {
            cancelScheduler(context,requestCode);
        }
        setScheduler(context, time, requestCode);

    }

    public void cancelScheduler(Context context,int requestCode)
    {
        //cancelling the scheduler when needed
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(context, requestCode,
                new Intent(context, MainScheduler.class),
                PendingIntent.FLAG_UPDATE_CURRENT));
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
    public static int getUniqueCode(int requestCode) {
        //add a very big integer to the request code to maintain uniqueness
        //get int value
        return BigInteger.valueOf(1000000000 + requestCode).intValue();
    }
}
