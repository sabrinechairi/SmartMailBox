package com.example.smartmailbox;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetMachinesTask extends AsyncTask<String, Void, ArrayList<Machine>> {
    private static final String TAG = "GetMachinesTask";
    private static final String GET_MACHINES_URL = PublicValues.ip+"/mailbox/getmachines.php";

    private GetMachinesListener listener;

    public GetMachinesTask(GetMachinesListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<Machine> doInBackground(String... params) {
        String sessionToken = params[0];
        ArrayList<Machine> machines = new ArrayList<>();

        try {
            URL url = new URL(GET_MACHINES_URL + "?session_token=" + sessionToken);
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
                    int machineId = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    String location = jsonObject.getString("location");
                    String createdAt = jsonObject.getString("created_at");

                    Machine machine = new Machine(machineId, name, location, createdAt);
                    machines.add(machine);
                }
            } else {
            }

            connection.disconnect();
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error in HTTP connection: " + e.getMessage());
        }

        return machines;
    }

    @Override
    protected void onPostExecute(ArrayList<Machine> machines) {
        listener.onMachinesFetched(machines);
    }

    public interface GetMachinesListener {
        void onMachinesFetched(ArrayList<Machine> machines);
    }
}
