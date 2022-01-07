package com.penguinstech.notificationsalarmapp.RoomDb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyNotification {


    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "message")
    public String message;

    public MyNotification() {
    }

    public MyNotification(String time, String message) {
        this.time = time;
        this.message = message;
    }
    public MyNotification(int id, String time, String message) {
        this.id = id;
        this.time = time;
        this.message = message;
    }
}
