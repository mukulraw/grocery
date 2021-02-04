package com.mrtecks.grocery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Timer;
import java.util.TimerTask;

public class Spalsh extends AppCompatActivity implements InstallReferrerStateListener {

    private static final String TAG = "referrer";
    Timer t;
    InstallReferrerClient mReferrerClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        mReferrerClient = InstallReferrerClient.newBuilder(this).build();
        mReferrerClient.startConnection(this);

        FirebaseMessaging.getInstance().subscribeToTopic("epk").addOnCompleteListener(task -> Log.d("task", task.toString()));

        String id = SharePreferenceUtils.getInstance().getString("userId");

        t = new Timer();

        t.schedule(new TimerTask() {
            @Override
            public void run() {

                if (id.length() > 0) {
                    Intent intent = new Intent(Spalsh.this, MainActivity2.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Spalsh.this, Login.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1200);

    }

    @Override
    public void onInstallReferrerSetupFinished(int responseCode) {
        switch (responseCode) {
            case InstallReferrerClient.InstallReferrerResponse.OK:
                try {
                    Log.v(TAG, "InstallReferrer conneceted");
                    ReferrerDetails response = mReferrerClient.getInstallReferrer();

                    SharePreferenceUtils.getInstance().saveString("referrer", response.getInstallReferrer());

                    mReferrerClient.endConnection();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                Log.w(TAG, "InstallReferrer not supported");
                break;
            case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                Log.w(TAG, "Unable to connect to the service");
                break;
            default:
                Log.w(TAG, "responseCode not found.");
        }
    }

    @Override
    public void onInstallReferrerServiceDisconnected() {
        mReferrerClient.startConnection(this);
    }

}
