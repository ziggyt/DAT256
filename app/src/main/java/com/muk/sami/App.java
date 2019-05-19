package com.muk.sami;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ACTIVE_TRIP_ID = "channelActiveTrip";
    public static final String CHANNEL_NORMAL_ID = "channelNormalTrip";

    @Override
    public void onCreate() {
        super.onCreate();
        
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_NORMAL_ID,
                    getString(R.string.notification_channel_normal_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription(getString(R.string.notification_channel_normal_description));

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_ACTIVE_TRIP_ID,
                    getString(R.string.notification_channel_active_trip_name),
                    NotificationManager.IMPORTANCE_LOW);
            channel2.setDescription(getString(R.string.notification_channel_active_trip_description));
            channel2.setShowBadge(true);
            channel2.canShowBadge();
            channel2.enableLights(true);
            channel2.setLightColor(Color.RED);
            channel2.enableVibration(true);
            channel2.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
