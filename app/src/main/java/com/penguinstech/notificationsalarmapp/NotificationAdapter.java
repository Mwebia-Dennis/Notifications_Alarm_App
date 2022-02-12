package com.penguinstech.notificationsalarmapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstech.notificationsalarmapp.RoomDb.MyNotification;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<MyNotification> notificationList;
    private final Context context;
    public static final DateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss", Locale.ENGLISH);

    public NotificationAdapter (Context context, List<MyNotification> notificationList) {
        this.notificationList = notificationList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View myView = LayoutInflater.from(context).inflate(R.layout.notification_layout, parent, false);
        return new ViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyNotification notification = notificationList.get(position);
        holder.msgTv.setText(notification.message);
        sdf.setTimeZone(TimeZone.getDefault());
        try {

            Calendar time = Calendar.getInstance();
            NotificationAdapter.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            time.setTime(Objects.requireNonNull(NotificationAdapter.sdf.parse(notification.time)));
            NotificationAdapter.sdf.setTimeZone(TimeZone.getDefault());
            time.setTime(Objects.requireNonNull(NotificationAdapter.sdf.parse(NotificationAdapter.sdf.format(time.getTime()))));
            holder.timeTv.setText(NotificationAdapter.sdf.format(time.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView msgTv, timeTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msgTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.reminderTimeTv);

        }
    }
}
