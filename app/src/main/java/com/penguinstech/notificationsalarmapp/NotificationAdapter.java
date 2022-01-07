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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<MyNotification> notificationList;
    private final Context context;
    public static final DateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

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
        holder.timeTv.setText(notification.time);
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
