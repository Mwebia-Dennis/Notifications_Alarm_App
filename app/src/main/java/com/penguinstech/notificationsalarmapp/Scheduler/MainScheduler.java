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

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainScheduler extends BroadcastReceiver {

    public MainScheduler() {
//        android.os.Debug.waitForDebugger();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class, Constants.DATABASE_NAME).build();//load room db
        //get all notifications between specific dates
        new Thread(()->{

            Calendar midnight = Calendar.getInstance();

            Calendar nextDayMidnight = (Calendar) midnight.clone();
            nextDayMidnight.add(Calendar.MINUTE, 10);
            NotificationAdapter.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            List<MyNotification> notificationList = db
                    .notificationDao()
                    .getTodayNotifications(NotificationAdapter.sdf.format(midnight.getTime()),
                            NotificationAdapter.sdf.format(nextDayMidnight.getTime())
                    );//get list of data
            for (MyNotification myNotification : notificationList){
                //for eaach notification set up a broadcast to show notification
                Calendar _30_minsBefore = Calendar.getInstance();
                try {
                    NotificationAdapter.sdf.setTimeZone(TimeZone.getDefault());
                    _30_minsBefore.setTime(NotificationAdapter.sdf.parse(myNotification.time));
                    if (_30_minsBefore.compareTo(Calendar.getInstance()) > 0) {//if the notification time is beyond current time, set notification

                        Calendar _5_minsBefore = (Calendar) _30_minsBefore.clone();
                        // set 30 mins before and 5 mins before
                        _30_minsBefore.add(Calendar.MINUTE, -30);//30 mins before
                        _5_minsBefore.add(Calendar.MINUTE, -5);//5 mins befo
//                        _2_minsBefore.add(Calendar.MINUTE, -2);//2 mins before
                        NotificationAlertScheduler alertScheduler = new NotificationAlertScheduler();
                        alertScheduler.setScheduler(context, _30_minsBefore, myNotification.id);
                        alertScheduler.setScheduler(context, _5_minsBefore, myNotification.id);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.i("error:time parse", e.getMessage());
                }

            }
        }).start();
    }

    public void setScheduler(Context context) {


        //check if the scheduler has been set
        //if not set the scheduler
        if (!isSchedulerSet(context))
        {
            //set up the alarm manager and the reference point ie pending intent
            //set repeating scheduler which repeats after 24 hours at midnight
            Calendar midnight = Calendar.getInstance();
            midnight.set(Calendar.HOUR_OF_DAY, 12);
            midnight.set(Calendar.AM_PM, Calendar.AM);
//            midnight.set(Calendar.HOUR, 1);
            midnight.set(Calendar.MINUTE, 0);
            midnight.set(Calendar.SECOND, 0);
//            midnight.set(Calendar.AM_PM, Calendar.PM);

            AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, MainScheduler.class);
            //set action so as the onReceive is triggered
            //the action should be the same as the declared action name in the manifest
            intent.setAction("com.penguinstech.notificationsalarmapp.scheduler");
            PendingIntent pi = PendingIntent.getBroadcast(context, 0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);//note flag_update_current which tells system how to handle new and existing pending intent
            am.setRepeating(AlarmManager.RTC_WAKEUP,
                    midnight.getTimeInMillis(), 1000 * 60 * 60, pi); // Millisec * Second * Minute  = 1 hour

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


}
