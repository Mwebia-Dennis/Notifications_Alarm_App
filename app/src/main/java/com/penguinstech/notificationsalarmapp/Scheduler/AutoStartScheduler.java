package com.penguinstech.notificationsalarmapp.Scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartScheduler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {

            //used to restart the scheduler at phone boot time
            //the receiver is registered to manifest to receive broadcast when phone is booted up
            //rather than calling setScheduler directly, we will start our service so as it does that for us
            context.startService(new Intent(context, SchedulerService.class));
        }
    }
}
