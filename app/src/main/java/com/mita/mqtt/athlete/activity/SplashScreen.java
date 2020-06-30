package com.mita.mqtt.athlete.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mita.athlete.login.MainActivity;
import com.mita.mqtt.athlete.R;

public class SplashScreen extends AppCompatActivity {

    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        if (loginPreferences.getBoolean("saveLogin", false)) {

            Thread myThread = new Thread() {

                @Override
                public void run() {
                    try {
                        sleep(2000);

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("==SplashErr", e.getMessage().toString());

                    }
                }

            };
            myThread.start();
        } else {
            Thread myThread = new Thread() {

                @Override
                public void run() {
                    try {
                        sleep(2000);
                        Log.i("==Splash1", "Splash222");

                        Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(i);
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("==SplashErr111", e.getMessage().toString());

                    }
                }

            };
            myThread.start();
        }
    }


}