package com.example.smartmailbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    LinearLayout introHolder;
    SessionManager sm ;
    private static final int JOB_ID = 1;


    final String namePref="isThereAnyNews";

    void init() {
        introHolder = findViewById(R.id.introElementsHolder);

    }

    private static final String CHECK_SESSION_URL = PublicValues.ip+"/mailbox/auth_check.php";
    private String SESSION_TOKEN ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();



        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS)!=
            PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},1011);

            }
        }

        Context context = getApplicationContext();
        Intent serviceIntent = new Intent(context, MyBackgroundService.class);
        context.startService(serviceIntent);

        //Intent serviceIntent = new Intent(this, MyForegroundService.class);
        //startForegroundService(serviceIntent);
        //NotificationReceiver.scheduleNotificationCheck(this);



        introHolder.animate().alpha(1f).setDuration(1000).start();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            introHolder.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .rotation(720)
                    .setDuration(1000)
                    .withEndAction(()->{
                        startActivity(new Intent(getApplicationContext(),auth.class));
                    })
                    .start();
        }, 3000);
        new Handler(Looper.getMainLooper()).postDelayed( () -> {
            this.finish();
        },4000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

















        /*
        sm= new SessionManager(getApplicationContext());




        introHolder = findViewById(R.id.introElementsHolder);
        introHolder.animate().alpha(1f).setDuration(1000).start();

        if(sm.isLoggedIn()) {
            SESSION_TOKEN = sm.getSessionToken();
            new Handler(Looper.getMainLooper()).postDelayed(() -> checkSessionValidity(SESSION_TOKEN), 1000);
        }else {
            startActivity(new Intent(getApplicationContext(), auth.class));
        }
    }

    private void checkSessionValidity(String sessionToken) {
        new SessionCheckerTask().execute(sessionToken);
    }

    private void clearSession() {
        sm.clearSession();
        startActivity(new Intent(getApplicationContext(), auth.class));
        finish();
    }

    private class SessionCheckerTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            if (params.length == 0) {
                return false;
            }

            String sessionToken = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(CHECK_SESSION_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Build JSON object with session token
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("session_token", sessionToken);

                // Write session token to the connection output stream
                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                // Read response
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();

                        }
                    });

                    // Parse JSON response
                    //JSONObject jsonResponse = new JSONObject(result.toString());
                    return true;//jsonResponse.optBoolean("success", false);
                } else {
                }
            } catch (IOException | JSONException e) {
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean sessionValid) {
            if (!sessionValid) {
                clearSession();
            } else {
            }
        }
    }
}
      */