package com.example.smartmailbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class auth extends AppCompatActivity implements LoginTask.LoginListener{

    ImageView mailIllustration;
    TextView description1,description2,registerNow;
    ConstraintLayout loginInputs;
    EditText editTextUsername, editTextPassword;
    Button buttonLogin;
    ProgressBar prgsAuth;

    SessionManager sessionManager;
    void init(){
        mailIllustration= findViewById(R.id.mailIllustration);
        description1 = findViewById(R.id.descriptionText);
        description2 = findViewById(R.id.descriptionText2);
        registerNow = findViewById(R.id.registerNow);
        loginInputs = findViewById(R.id.loginInputs);
        editTextUsername = findViewById(R.id.usernamelogin);
        editTextPassword = findViewById(R.id.passwordlogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        prgsAuth = findViewById(R.id.prgsAuth);
        sessionManager= new SessionManager(getApplicationContext());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        init();
        if(sessionManager.isLoggedIn()){
            startActivity(new Intent(getApplicationContext(),Home.class));
            this.finish();
        }

        registerNow.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(),Register.class));
            this.finish();
        });




        buttonLogin.setOnClickListener(v -> {
            prgsAuth.setVisibility(View.VISIBLE);
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Execute AsyncTask to perform login
            LoginTask loginTask = new LoginTask(auth.this );
            loginTask.execute(username, password);
        });


        mailIllustration.animate().translationY(0).alpha(1f).setDuration(300).withEndAction(() -> {
            description1.animate().translationX(0).alpha(1f).setDuration(300).withEndAction(()->{
                wait(0);
                description2.animate().translationX(0).alpha(1f).setDuration(300).withEndAction(()->{
                    wait(0);
                    loginInputs.animate().alpha(1f).translationY(0).setDuration(300).start();
                }).start();
            }).start();
        }).start();
    }

    void wait(int mls){
        try {
            Thread.sleep(mls);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onLoginResult(String result) {
        prgsAuth.setVisibility(View.GONE);
        try {
            JSONObject jsonResponse = new JSONObject(result);

            String responseCode = jsonResponse.getString("responseCode");

            switch (responseCode) {
                case "112":
                    Toast.makeText(this, "Authenticated successfully", Toast.LENGTH_SHORT).show();
                    sessionManager.saveSession(sessionManager.getSessionToken());
                    String sessionToken = jsonResponse.getString("session_token");
                    sessionManager.saveSession(sessionToken);
                    startActivity(new Intent(getApplicationContext(),Home.class));
                    break;

                case "110":
                    editTextPassword.setError("Check password");
                    Toast.makeText(this, "Password Wrong", Toast.LENGTH_SHORT).show();
                    break;

                case "113":
                    editTextUsername.setError("Check your username");
                    Toast.makeText(this, "No such user found", Toast.LENGTH_SHORT).show();
                    break;

                case "400":
                    Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show();
                    break;

                case "405":
                    Toast.makeText(this, "Invalid request method", Toast.LENGTH_SHORT).show();
                    break;

                case "500":
                    Toast.makeText(this, "Server error, please try again later", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(this, "Unknown response code", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error! Try again", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        /*if (result.startsWith("Error")) {
            Toast.makeText(this, "Error! try again", Toast.LENGTH_SHORT).show();
        } else {
            switch (result){
                case "112":
                    Toast.makeText(this, "Authenticated succefully", Toast.LENGTH_SHORT).show();
                    sessionManager.saveSession("loggedin");
                    break;

                case "110":
                    editTextPassword.setError("Check password");
                    Toast.makeText(this, "Password Wrong", Toast.LENGTH_SHORT).show();
                    break;
                case "113":
                    editTextUsername.setError("Check your username");
                    Toast.makeText(this, "No such user found", Toast.LENGTH_SHORT).show();
                    break;
            }
        }*/

    }
}