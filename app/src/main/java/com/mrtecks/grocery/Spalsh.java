package com.mrtecks.grocery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class Spalsh extends AppCompatActivity {

    Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        t = new Timer();

        t.schedule(new TimerTask() {
            @Override
            public void run() {

                Intent intent = new Intent(Spalsh.this , MainActivity.class);
                startActivity(intent);
                finish();

            }
        } , 1200);

    }
}
