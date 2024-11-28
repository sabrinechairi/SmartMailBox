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

public class AddMachineTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "AddMachineTask";
    private static final String ADD_MACHINE_URL = PublicValues.ip+"/mailbox/addMachine.php";

    private AddMachineListener listener;

    public AddMachineTask(AddMachineListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String name = params[0];
        String machinecode = params[1];
        String location = params[2];
        String sessionToken = params[3];
        String response = "";

        try {
            URL url = new URL(ADD_MACHINE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Build query parameters
            StringBuilder postData = new StringBuilder();
            postData.append(URLEncoder.encode("name", "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(name, "UTF-8"));
            postData.append("&");
            postData.append(URLEncoder.encode("machinecode", "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(machinecode, "UTF-8"));
            postData.append("&");
            postData.append(URLEncoder.encode("location", "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(location, "UTF-8"));
            postData.append("&");
            postData.append(URLEncoder.encode("session_token", "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(sessionToken, "UTF-8"));

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
        listener.onAddMachineResult(result);
    }

    public interface AddMachineListener {
        void onAddMachineResult(String result);
    }
}
