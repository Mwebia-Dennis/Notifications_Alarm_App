package com.penguinstech.notificationsalarmapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.penguinstech.notificationsalarmapp.RoomDb.AppDatabase;
import com.penguinstech.notificationsalarmapp.RoomDb.MyNotification;

import java.time.Year;
import java.util.Calendar;
import java.util.TimeZone;

public class NewNotificationBottomSheet extends BottomSheetDialogFragment {

    Calendar calendar = Calendar.getInstance();
    AppDatabase db;

    public NewNotificationBottomSheet(AppDatabase db) {
        this.db = db;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_notification_layout,
                container, false);

        Calendar mCalendar = Calendar.getInstance();
        final TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (timePicker,selectedHour, selectedMinute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);
                },
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                false);
        timePickerDialog.setTitle("Select Reminder");

        v.findViewById(R.id.reminderBtn).setOnClickListener(v1->{

            timePickerDialog.show();
        });


        v.findViewById(R.id.saveNotificationBtn).setOnClickListener(view->{

            EditText messageEt = v.findViewById(R.id.messageEt);

            //validation
            if(messageEt.getText().toString().trim().equals("")) {
                messageEt.setError("This Field is required");
            } else if(calendar == null) {
                Toast.makeText(getContext(), "You haven't selected the reminder time", Toast.LENGTH_LONG).show();
            }else if(calendar.compareTo(Calendar.getInstance()) <= 0){
                Toast.makeText(getContext(), "Invalid date", Toast.LENGTH_LONG).show();
            } else {

                new Thread(()->{

                    //insert to local database
                    NotificationAdapter.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));//set timezone to UTC
                    db.notificationDao()
                            .insertAll(new MyNotification(
                                    NotificationAdapter.sdf.format(calendar.getTime()),
                                    messageEt.getText().toString().trim()
                            ));
                }).start();

                this.dismiss();

            }

        });

        return v;
    }
}
