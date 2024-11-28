package com.example.smartmailbox;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetNotifications extends AsyncTask<String, Void, ArrayList<Notification2>> {

    private static final String GET_NOTIFICATIONS_URL = PublicValues.ip+"/mailbox/getnotifications.php";
    private GetNotificationsListener listener;

    public GetNotifications(GetNotifications.GetNotificationsListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<Notification2> doInBackground(String... params) {
        String sessionToken=params[0];
        ArrayList<Notification2> notifications = new ArrayList<>();
        try {
            URL url = new URL(GET_NOTIFICATIONS_URL + "?session_token=" + sessionToken);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder responseBuffer = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseBuffer.append(inputLine);
                }
                in.close();
                String response = responseBuffer.toString();
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int notificationId = Integer.parseInt(jsonObject.getString("id"));
                    String machine = jsonObject.getString("machName");
                    String notificationTime = jsonObject.getString("notTime");
                    String notificationReaded = jsonObject.getString("notR");
                    Notification2 notifications1 = new Notification2(notificationId,machine,notificationTime,notificationReaded);
                    notifications.add(notifications1);
                }
            } else {
            }
            connection.disconnect();
        } catch (IOException | JSONException e) {
        }

        return notifications;
    }

    @Override
    protected void onPostExecute(ArrayList<Notification2> notifications) {
        listener.onNotificationsFetched(notifications);
    }

    public interface GetNotificationsListener {
        void onNotificationsFetched(ArrayList<Notification2> notifications);
    }
}
