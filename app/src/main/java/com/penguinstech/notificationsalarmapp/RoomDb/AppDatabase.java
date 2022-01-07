package com.penguinstech.notificationsalarmapp.RoomDb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MyNotification.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NotificationDao notificationDao();
}
