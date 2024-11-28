package com.example.smartmailbox;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class MyBackgroundService extends Service {
    private Handler handler;
    private Runnable runnable;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    SessionManager sm;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sm = new SessionManager(this);
        final String namePref = "isThereAnyNews";

        preferences = getApplicationContext().getSharedPreferences(namePref, Context.MODE_PRIVATE);
        editor = preferences.edit();

        runnable = new Runnable() {
            @Override
            public void run() {
                isThereNotifications(new NotificationCallback() {
                    @Override
                    public void onResult(String response) {
                        if (response.contains("new mails")) {
                            String heChecked = preferences.getString("heChecked", "null");

                            //Toast.makeText(MyBackgroundService.this, "Checking if: " + heChecked, Toast.LENGTH_SHORT).show();

                            // Show notification only if "heChecked" is not "yes"
                            //if ("yes".equals(heChecked)) {
                                makeNotification(response);
                                editor.putString("heChecked", "no");
                                editor.apply();
                            //}

                            //heChecked = preferences.getString("heChecked", "null");
                            //Toast.makeText(MyBackgroundService.this, "It is: " + heChecked, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Schedule the next check
                handler.postDelayed(this, 10000);
            }
        };

        // Start the first check
        handler.post(runnable);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void isThereNotifications(NotificationCallback callback) {
        HttpRequestHelper.getHttpResponseAsync(PublicValues.ip + "mailbox/istherenotifications.php?st="+sm.getSessionToken(), new HttpRequestHelper.ResponseCallback() {
            @Override
            public void onResponse(String response) {
                callback.onResult(response);
            }
        });
    }

    public interface NotificationCallback {
        void onResult(String response);
    }


    private void makeNotification(String msg) {
        String channelID = "CHID277";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.newemail)
                .setContentTitle("Smart MailBox")
                .setContentText(msg)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(getApplicationContext(), Notifications.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("cayon", "collomi");
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID, "Descript it", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(0, builder.build());
    }


    /*
    #old make notif


    void makeNotification(String msg){
        String chanelID = "CHID277";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), chanelID);
        builder.setSmallIcon(R.drawable.newemail)
                .setContentTitle("Smart MailBox")
                .setContentText(msg)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(getApplicationContext(),Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("cayon","collomi");
        PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(chanelID);
            if(notificationChannel == null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(chanelID,"Descript it",importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0,builder.build());

    }*/
}
