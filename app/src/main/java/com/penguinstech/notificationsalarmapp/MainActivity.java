package com.penguinstech.notificationsalarmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.penguinstech.notificationsalarmapp.Scheduler.MainScheduler;
import com.penguinstech.notificationsalarmapp.Scheduler.SchedulerService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.startService(new Intent(this, SchedulerService.class));
    }
}