package com.example.smartmailbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Notification2> listNotifications;

    public NotificationAdapter(Context context){
        this.context = context;
        listNotifications = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return listNotifications.size();
    }

    @Override
    public Object getItem(int position) {
        return listNotifications.get(position);
    }
    public void setNotifications(ArrayList<Notification2> notifications){
        listNotifications = notifications;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            LayoutInflater inflater =LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.notifications_item,parent,false);
        }
        TextView machineName = convertView.findViewById(R.id.textViewName);
        TextView notificationDate = convertView.findViewById(R.id.textViewCreatedAt);
        ImageView readed = convertView.findViewById(R.id.readedIcon);
        Notification2 not = listNotifications.get(position);
        machineName.setText(not.getMachineName());
        notificationDate.setText(not.getTime());
        if (not.isReaded()){
            readed.setImageDrawable(context.getResources().getDrawable(R.drawable.emailreaded));
        }
        return convertView;
    }
}
