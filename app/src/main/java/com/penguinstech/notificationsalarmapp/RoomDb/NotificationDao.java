package com.penguinstech.notificationsalarmapp.RoomDb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM mynotification")
    List<MyNotification> getAll();


    @Query("SELECT * FROM mynotification where id = :id")
    MyNotification getNotificationById(int id);

    @Query("SELECT * FROM mynotification where time > :currentMidnight AND time < :nextDayMidnight")
    List<MyNotification> getTodayNotifications(String currentMidnight, String nextDayMidnight);

    @Insert
    void insertAll(MyNotification... myNotifications);

    @Delete
    void delete(MyNotification myNotification);
}
