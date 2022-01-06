package com.penguinstech.notificationsalarmapp.Scheduler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SchedulerService extends Service {

    MainScheduler mainScheduler = new MainScheduler();

    @Override
    public void onCreate() {
        super.onCreate();
//        android.os.Debug.waitForDebugger();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mainScheduler.setScheduler(this);
        return START_STICKY;
    }

}
