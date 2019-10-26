package com.mrtecks.grocery;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Html;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {

        SharePreferenceUtils.getInstance().saveString("token" , s);

        Log.d("toekn" , s);



        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        int c = SharePreferenceUtils.getInstance().getInteger("count");

        c++;

        SharePreferenceUtils.getInstance().saveInt("count" , c);

        Log.d("asdasd" , remoteMessage.getData().toString());

        JSONObject object;
        object = new JSONObject(remoteMessage.getData());

        try {
            handleNotification(object.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent registrationComplete = new Intent("count");

        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);


        super.onMessageReceived(remoteMessage);
    }

    private void handleNotification(String message) {

        Log.d("notificationData", message);
        String idChannel = "southman messages";
        Intent mainIntent;

        mainIntent = new Intent(Bean.getContext(), Spalsh.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(Bean.getContext(), 0, mainIntent, 0);

        NotificationManager mNotificationManager = (NotificationManager) Bean.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel;
        // The id of the channel.

        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationCompat.Builder builder;

        if (isAppRunning(this))
        {
            builder = new NotificationCompat.Builder(Bean.getContext(), idChannel);
            builder.setContentTitle(Bean.getContext().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.loggg)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));
        }
        else
        {
            builder = new NotificationCompat.Builder(Bean.getContext(), idChannel);
            builder.setContentTitle(Bean.getContext().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.loggg)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(message)))
                    .setAutoCancel(true)
                    .setContentText(Html.fromHtml(message));
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(idChannel, Bean.getContext().getString(R.string.app_name), importance);
            // Configure the notification channel.
            mChannel.setDescription(Bean.getContext().getString(R.string.alarm_notification));
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        } else {
            builder.setContentTitle(Bean.getContext().getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(ContextCompat.getColor(Bean.getContext(), R.color.transparent))
                    .setVibrate(new long[]{100, 250})
                    .setLights(Color.YELLOW, 500, 5000)
                    .setAutoCancel(true);
        }
        if (mNotificationManager != null) {
            mNotificationManager.notify(1, builder.build());
        }


    }

    public static boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE) {
                    return true;
                }
            }
        }
        return false;
    }
}
