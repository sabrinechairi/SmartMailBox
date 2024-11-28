package com.example.smartmailbox;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    TextView logoutButton;
    final String namePref="isThereAnyNews";
    LinearLayout addMachineBtn,seeBoxes,seeNotifications;
    ImageView mail;
    void init(){
        logoutButton = findViewById(R.id.logoutText);
        addMachineBtn= findViewById(R.id.addMachineBtn);
        seeBoxes= findViewById(R.id.seeMailboxes);
        seeNotifications= findViewById(R.id.seeNotifications);
        mail = findViewById(R.id.notifctions);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        logoutButton.setOnClickListener(v -> {
            SessionManager sm = new SessionManager(getApplicationContext());
            sm.clearSession();
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        });
        addMachineBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),AddMachine.class));
        });
        seeBoxes.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),MachineListActivity.class));
        });
        seeNotifications.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),Notifications.class));
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity(); // Close all activities and exit the app
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getApplicationContext().getSharedPreferences(namePref, Context.MODE_PRIVATE);
        String heChecked = sp.getString("heChecked","null");
        if (!"yes".equals(heChecked)){
         mail.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.newRed));
         TextView tv = findViewById(R.id.notifctionsText);
         tv.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.newRed));
         //Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
         //mail.startAnimation(shakeAnimation);
        }else {
            mail.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.darkblue));
            TextView tv = findViewById(R.id.notifctionsText);
            tv.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.darkblue));
            //mail.clearAnimation();
        }
    }
}