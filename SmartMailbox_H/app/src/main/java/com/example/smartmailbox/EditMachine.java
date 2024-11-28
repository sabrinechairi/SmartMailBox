package com.example.smartmailbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EditMachine extends AppCompatActivity {
    Machine machine;
    TextView machineNameEdit;
    Button button;
    SessionManager  sm;

    EditText newName,newLoc;
    ProgressBar prgsAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_machine);
        machineNameEdit = findViewById(R.id.machineNameEdit);
        newName = findViewById(R.id.editName);
        newLoc = findViewById(R.id.editLocal);
        button = findViewById(R.id.updateBtn);
        prgsAuth = findViewById(R.id.esefe);
        sm= new SessionManager(getApplicationContext());
        Intent intent = getIntent();
        if (intent != null) {
            String jsonStr = intent.getStringExtra("machine_json");
            Gson gson = new Gson();
            machine = gson.fromJson(jsonStr, Machine.class);
            machineNameEdit.setText(machine.getName());
            newName.setText(machine.getName());
            newLoc.setText(machine.getLocation());
        }
        button.setOnClickListener(v -> {
            prgsAuth.setVisibility(View.VISIBLE);
            updateMachineData();
        });
    }

    private void updateMachineData() {
        String name = newName.getText().toString();
        String loc = newLoc.getText().toString();
        String URL = PublicValues.ip+"/mailbox/update.php";
        new UpdateMachineTask(machine.getId(), name, loc, URL).execute();


    }
    private class UpdateMachineTask extends AsyncTask<Void, Void, String> {
        private int machineId;
        private String name, location, url;

        UpdateMachineTask(int machineId, String name, String location, String url) {
            this.machineId = machineId;
            this.name = name;
            this.location = location;
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                StringBuilder postData = new StringBuilder();
                postData.append(URLEncoder.encode("session_token", "UTF-8"));
                postData.append("=");
                postData.append(URLEncoder.encode(String.valueOf(sm.getSessionToken()), "UTF-8"));
                postData.append("&");
                postData.append(URLEncoder.encode("id", "UTF-8"));
                postData.append("=");
                postData.append(URLEncoder.encode(String.valueOf(machineId), "UTF-8"));
                postData.append("&");
                postData.append(URLEncoder.encode("name", "UTF-8"));
                postData.append("=");
                postData.append(URLEncoder.encode(name, "UTF-8"));
                postData.append("&");
                postData.append(URLEncoder.encode("location", "UTF-8"));
                postData.append("=");
                postData.append(URLEncoder.encode(location, "UTF-8"));

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

                    return responseBuffer.toString();
                } else {
                    return "HTTP error code: " + responseCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            prgsAuth.setVisibility(View.GONE);
            try {
                JSONObject jsonresponse = new JSONObject(result);
                if (jsonresponse.getString("status").contains("success")){
                    Toast.makeText(EditMachine.this, jsonresponse.getString("message"), Toast.LENGTH_SHORT).show();
                } else if (jsonresponse.getString("status").contains("failure")) {
                    Toast.makeText(EditMachine.this, jsonresponse.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(EditMachine.this, "Error Try again", Toast.LENGTH_SHORT).show();
            }
            System.out.println("900 xx-: "+result);

        }
    }
}