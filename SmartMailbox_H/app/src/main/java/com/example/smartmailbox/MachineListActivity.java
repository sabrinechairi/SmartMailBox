package com.example.smartmailbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class MachineListActivity extends AppCompatActivity implements GetMachinesTask.GetMachinesListener{
    private MyMachinesAdapter machineAdapter;
    private ListView lista;
    private ProgressBar progressBar;
    TextView logoutButton;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_list);

        logoutButton = findViewById(R.id.logoutText);
        logoutButton.setOnClickListener(v -> {
            SessionManager sm = new SessionManager(getApplicationContext());
            sm.clearSession();
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        });

        progressBar = findViewById(R.id.prgsAuth33);
        progressBar.setVisibility(View.VISIBLE);
        lista = findViewById(R.id.listMachines);
        machineAdapter = new MyMachinesAdapter(this);
        lista.setAdapter(machineAdapter);
        sessionManager = new SessionManager(getApplicationContext());


        String sessionToken = sessionManager.getSessionToken();
        if (sessionToken != null && !sessionToken.isEmpty()) {
            new GetMachinesTask(this).execute(sessionToken);
        } else {

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadMachines();
    }

    private void loadMachines() {
        progressBar.setVisibility(View.VISIBLE);
        String sessionToken = sessionManager.getSessionToken();
        if (sessionToken != null && !sessionToken.isEmpty()) {
            new GetMachinesTask(this).execute(sessionToken);
        } else {
        }
    }

    @Override
    public void onMachinesFetched(ArrayList<Machine> machines) {
        machineAdapter.setMachines(machines);
        machineAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }
}