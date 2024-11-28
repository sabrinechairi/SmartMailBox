package com.example.smartmailbox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class AddMachine extends AppCompatActivity {

    Button launchScan,next;
    String machineCode,finalMachineCode;
    TextView devicefound;
    EditText nameInput,locationInput;
    SessionManager sm ;
    void  init(){
        launchScan=findViewById(R.id.launchScan);
        next = findViewById(R.id.addnNxt);
        devicefound= findViewById(R.id.devicefound);
        nameInput = findViewById(R.id.machineName);
        locationInput = findViewById(R.id.adressMachine);
        sm = new SessionManager(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_machine);
        init();
        launchScan.setOnClickListener(v -> {
            // Initialize QR Code reader
            IntentIntegrator integrator = new IntentIntegrator(AddMachine.this);
            integrator.setOrientationLocked(false);  // Optional
            integrator.initiateScan();
            devicefound.setVisibility(View.GONE);
        });
        next.setOnClickListener(v -> {
            findViewById(R.id.prgsAuth).setVisibility(View.VISIBLE);
            String location = locationInput.getText().toString();

            String name = nameInput.getText().toString();

            if(finalMachineCode=="" || name =="" || !sm.isLoggedIn()){
                Toast.makeText(this, "Scan the machine code and fill Name and Location ", Toast.LENGTH_SHORT).show();
                return;
            }
            String sessionToken = sm.getSessionToken();
            AddMachineTask addMachineTask = new AddMachineTask(new AddMachineTask.AddMachineListener() {
                @Override
                public void onAddMachineResult(String result) {

                    try {
                        JSONObject jsresponse = new JSONObject(result);
                        System.out.println("xx--"+result);
                        String msg = jsresponse.getString("message");
                        Toast.makeText(AddMachine.this, msg, Toast.LENGTH_SHORT).show();
                        findViewById(R.id.prgsAuth).setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        finish();


                    } catch (JSONException e) {
                        Toast.makeText(AddMachine.this, "Error Try again", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            addMachineTask.execute(name, finalMachineCode, location, sessionToken);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                machineCode=result.getContents();
                if(machineCode.startsWith("smb:")){
                    finalMachineCode=machineCode.replace("smb:","");
                    devicefound.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(this, "invalid machine code", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void addMachine(String code){

    }


}