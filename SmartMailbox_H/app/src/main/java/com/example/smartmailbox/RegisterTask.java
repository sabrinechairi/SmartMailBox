package com.example.smartmailbox;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "RegisterTask";
    private static final String REGISTER_URL = PublicValues.ip+"/mailbox/register.php";

    private RegisterListener listener;

    public RegisterTask(RegisterListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String username = params[0];
        String password = params[1];
        String email = params[2];
        String response = "";

        try {
            URL url = new URL(REGISTER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Build query parameters
            StringBuilder postData = new StringBuilder();
            postData.append(URLEncoder.encode("username", "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(username, "UTF-8"));
            postData.append("&");
            postData.append(URLEncoder.encode("password", "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(password, "UTF-8"));
            postData.append("&");
            postData.append(URLEncoder.encode("email", "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(email, "UTF-8"));

            // Write data to connection
            OutputStream os = connection.getOutputStream();
            os.write(postData.toString().getBytes());
            os.flush();
            os.close();

            // Get response from server
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder responseBuffer = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    responseBuffer.append(inputLine);
                }
                in.close();

                response = responseBuffer.toString();
            } else {
                response = "HTTP error code: " + responseCode;
            }

            connection.disconnect();
        } catch (IOException e) {
            Log.e(TAG, "Error in HTTP connection: " + e.getMessage());
            response = "Error: " + e.getMessage();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onRegisterResult(result);
    }

    public interface RegisterListener {
        void onRegisterResult(String result);
    }
}
