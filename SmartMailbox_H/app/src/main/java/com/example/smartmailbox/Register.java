package com.example.smartmailbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {
    ImageView signupIllustration;
    TextView signupDescription1, signupDescription2;
    ConstraintLayout signupInputs;
    EditText editTextUsername, editTextEmail, editTextPassword;
    Button buttonSignup;

    SessionManager sessionManager;
    ProgressBar prgsAuth;

    void init() {
        signupIllustration = findViewById(R.id.signupIllustration);
        signupDescription1 = findViewById(R.id.signupDescriptionText);
        signupDescription2 = findViewById(R.id.signupDescriptionText2);
        signupInputs = findViewById(R.id.signupInputs);
        editTextUsername = findViewById(R.id.usernameSignup);
        editTextEmail = findViewById(R.id.emailSignup);
        editTextPassword = findViewById(R.id.passwordSignup);
        buttonSignup = findViewById(R.id.buttonSignup);
        sessionManager = new SessionManager(getApplicationContext());
        prgsAuth = findViewById(R.id.prgsAuth37);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        if(sessionManager.isLoggedIn()){
            finish();
        }
        signupIllustration.animate().translationY(0).alpha(1f).setDuration(300).withEndAction(() -> {
            signupDescription1.animate().translationX(0).alpha(1f).setDuration(300).withEndAction(() -> {
                wait(0);
                signupDescription2.animate().translationX(0).alpha(1f).setDuration(300).withEndAction(() -> {
                    wait(0);
                    signupInputs.animate().alpha(1f).translationY(0).setDuration(300).start();
                }).start();
            }).start();
        }).start();

        RegisterTask registerTask = new RegisterTask(new RegisterTask.RegisterListener() {
            @Override
            public void onRegisterResult(String result) {
                prgsAuth.setVisibility(View.GONE);
                try {
                    System.out.println("01xx -> " + result);
                    JSONObject jsonResponse = new JSONObject(result);
                    String responseCode = jsonResponse.getString("code");

                    if(responseCode=="100"){
                        Toast.makeText(Register.this, jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Register.this, jsonResponse.getString("success"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        buttonSignup.setOnClickListener(v -> {

            prgsAuth.setVisibility(View.VISIBLE);
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            registerTask.execute(username, password, email);
            // Perform signup logic here
        });

    }

    void wait(int mls) {
        try {
            Thread.sleep(mls);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}