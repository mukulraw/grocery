package com.mrtecks.grocery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class Spalsh extends AppCompatActivity implements InstallReferrerStateListener {

    private static final String TAG = "referrer";
    private static final int MY_REQUEST_CODE = 123;
    Timer t;
    InstallReferrerClient mReferrerClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        mReferrerClient = InstallReferrerClient.newBuilder(this).build();
        mReferrerClient.startConnection(this);

        FirebaseMessaging.getInstance().subscribeToTopic("epk").addOnCompleteListener(task -> Log.d("task", task.toString()));


        // Creates instance of the manager.
        /*AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);



// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            FLEXIBLE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                startapp();
            }
        });*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                //.setUpdateFrom(UpdateFrom.AMAZON)
                //.setUpdateFrom(UpdateFrom.GITHUB)
                //.setGitHubUserAndRepo("javiersantos", "AppUpdater")
                //...
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        Log.d("Latest Version", update.getLatestVersion());
                        Log.d("Latest Version Code", String.valueOf(update.getLatestVersionCode()));
                        Log.d("Release notes", update.getReleaseNotes());
                        Log.d("URL", String.valueOf(update.getUrlToDownload()));
                        Log.d("Is update available?", Boolean.toString(isUpdateAvailable));

                        if (isUpdateAvailable) {
                            AppUpdater appUpdater = new AppUpdater(Spalsh.this);
                            appUpdater.setDisplay(Display.DIALOG);
                            appUpdater.setButtonDoNotShowAgain(null);
                            appUpdater.setButtonDismissClickListener(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            appUpdater.start();
                        } else {
                            startapp();
                        }

                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d("AppUpdater Error", "Something went wrong");
                    }
                });
        appUpdaterUtils.start();
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

    void startapp() {
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
    public void onInstallReferrerServiceDisconnected() {
        mReferrerClient.startConnection(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.d("Updater", "Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            } else {
                startapp();
            }
        }
    }
}
