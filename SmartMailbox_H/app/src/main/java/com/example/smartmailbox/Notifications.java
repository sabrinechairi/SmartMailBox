package com.example.smartmailbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity implements GetNotifications.GetNotificationsListener{

    private NotificationAdapter notificationAdapter;
    private ListView lista;
    private ProgressBar progressBar;
    TextView refreshButton;
    private SessionManager sessionManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    final String namePref="isThereAnyNews";

    void init(){
        progressBar = findViewById(R.id.prgsAuth34);
        progressBar.setVisibility(View.VISIBLE);
        lista = findViewById(R.id.listNotifications);
        notificationAdapter = new NotificationAdapter(this);
        lista.setAdapter(notificationAdapter);
        sessionManager = new SessionManager(getApplicationContext());
        refreshButton = findViewById(R.id.refreshText);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        preferences = getApplicationContext().getSharedPreferences(namePref, Context.MODE_PRIVATE);
        editor= preferences.edit();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        init();
        editor.putString("heChecked","yes");
        editor.apply();
        refreshButton.setOnClickListener(v -> loadNotifications());
        swipeRefreshLayout.setOnRefreshListener(() -> { loadNotifications();});

       /* String sessionToken = sessionManager.getSessionToken();
        if (sessionToken != null && !sessionToken.isEmpty()) {
            new GetNotifications(this).execute(sessionToken);
        } else {

        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        String sessionToken = sessionManager.getSessionToken();
        if (sessionToken != null && !sessionToken.isEmpty()) {
            new GetNotifications(this).execute(sessionToken);
        } else {
        }
    }
    @Override
    public void onNotificationsFetched(ArrayList<Notification2> notifications) {
        notificationAdapter.setNotifications(notifications);
        notificationAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }
}