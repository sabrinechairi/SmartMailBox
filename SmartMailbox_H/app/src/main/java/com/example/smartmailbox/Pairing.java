package com.example.smartmailbox;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class Pairing extends AppCompatActivity {
    TextView tvStatus ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        tvStatus = (TextView) findViewById(R.id.textv);
        new FindESP32Task().execute();

    }

    private class FindESP32Task extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String esp32IpAddress = null;
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int ipAddress = wifiManager.getDhcpInfo().ipAddress;

            String ipBase = String.format("%d.%d.%d.",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff));

            for (int i = 1; i < 255; i++) {
                String testIp = ipBase + i;
                try {
                    InetAddress address = InetAddress.getByName(testIp);

                    if (address.isReachable(100)) {
                        runOnUiThread(() -> {
                            tvStatus.setText("testing: " + testIp);
                        });
                        if (checkIfESP32(testIp)) {
                            esp32IpAddress = testIp;
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return esp32IpAddress;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                tvStatus.setText("ESP32 Found: " + result);
            } else {
                tvStatus.setText("ESP32 Not Found");
            }
        }

        private boolean checkIfESP32(String ip) {
            try {
                URL url = new URL("http://" + ip);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(1000); // 1 second timeout
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    urlConnection.disconnect();
                    runOnUiThread(() -> {
                        Toast.makeText(Pairing.this, content, Toast.LENGTH_LONG).show();
                    });

                    // Check if the response contains the expected content
                    return content.toString().contains("129I");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}