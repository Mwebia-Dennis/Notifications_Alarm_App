 package com.penguinstech.notificationsalarmapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.penguinstech.notificationsalarmapp.RoomDb.AppDatabase;
import com.penguinstech.notificationsalarmapp.RoomDb.Constants;
import com.penguinstech.notificationsalarmapp.RoomDb.MyNotification;
import com.penguinstech.notificationsalarmapp.Scheduler.MainScheduler;
import com.penguinstech.notificationsalarmapp.Scheduler.SchedulerService;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

 public class MainActivity extends AppCompatActivity {

    AppDatabase db;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init () {
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, Constants.DATABASE_NAME).build();//load room db
        recyclerView = findViewById(R.id.mainRv);
        this.startService(new Intent(this, SchedulerService.class));//start the scheduler service
        loadNotifications();//update ui
    }

    private void loadNotifications() {

        new Thread(()->{
//            List<MyNotification> notificationList = db.notificationDao().getAll();//get list of data
            Calendar midnight = Calendar.getInstance();
            midnight.set(Calendar.HOUR_OF_DAY, 0);
            midnight.set(Calendar.MINUTE, 0);
            midnight.set(Calendar.SECOND, 0);

            Calendar nextDayMidnight = (Calendar) midnight.clone();
            nextDayMidnight.add(Calendar.DATE, 1);

            Log.i("date", NotificationAdapter.sdf.format(nextDayMidnight.getTime()));
            List<MyNotification> notificationList = db
                    .notificationDao()
                    .getTodayNotifications(NotificationAdapter.sdf.format(midnight.getTime()),
                            NotificationAdapter.sdf.format(nextDayMidnight.getTime())
                    );//get list of data
            Log.i("size", String.valueOf(notificationList.size()));
            runOnUiThread(() -> {
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                NotificationAdapter notificationAdapter = new NotificationAdapter(this, notificationList);
                recyclerView.setAdapter(notificationAdapter);
            });

        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.addNotification) {
            //open form
            new NewNotificationBottomSheet(db)
                    .show(getSupportFragmentManager(), "NewNotificationPopUp");
            return true;
        }else if (item.getItemId() == R.id.refresh) {
            loadNotifications();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

     @Override
     protected void onResume() {
         super.onResume();
         loadNotifications();
     }
 }