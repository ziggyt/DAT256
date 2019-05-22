package com.muk.sami.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.muk.sami.App;
import com.muk.sami.MainActivity;
import com.muk.sami.R;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.annotation.Nullable;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();

        String tripId = null;
        if (remoteMessage.getFrom() != null) {
            // Strip the string to leave the trip ID only
            tripId = remoteMessage.getFrom().replace("/topics/", "");
        }

        sendNotification(notification, data, tripId);
    }

    /**
     * Create and show a custom notification containing the received FCM message.
     *
     * @param notification  FCM notification payload received.
     * @param data          FCM data payload received.
     * @param tripId        The ID of the trip that the notification came form
     */
    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data, @Nullable String tripId) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_car_black_24dp);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String notificationData = data.get("typeOfChange");

        String title = "";
        String body = "";
        final String toastMessage;

        String loggedInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String[] typeOfChange = notificationData.split(",");
        String change = typeOfChange[0];
        String userId = typeOfChange[1];
        String driverId = typeOfChange[2];

        if (tripId != null && !change.equals("Trip removed")) { // Set pending intent to the detail view of the trip
            Bundle bundle = new Bundle();
            bundle.putString("tripId", tripId);

            NavDeepLinkBuilder navBuilder = new NavDeepLinkBuilder(this);
            navBuilder.setGraph(R.navigation.nav_graph);
            navBuilder.setArguments(bundle);
            if (driverId.equals(loggedInUserId)) {
                navBuilder.setDestination(R.id.driverDetailViewFragment);
            } else {
                navBuilder.setDestination(R.id.tripDetailViewFragment);
            }

            pendingIntent = navBuilder.createPendingIntent();
        }

        if (change.equals("Passenger joined")) {

            if (userId.equals(loggedInUserId)) {

                title = "Inbokad på resa";//TODO replace with string values
                body = "Du är nu inbokad på resan";
                toastMessage = "Inbokad på resa";

            } else if (driverId.equals(loggedInUserId)) {

                title = "Ny passagerare";//TODO replace with string values
                body = "En passagerare har bokat in sig på din resa";
                toastMessage = "Ny passagerare";

            } else {

                title = "Ny passagerare";//TODO replace with string values
                body = "En passagerare har gått med en resa du är inbokad på";
                toastMessage = "Ny passagerare";
            }

        } else if (change.equals("Trip started")) {

            title = "Resan har börjat";
            body = "Nu är resan igång";
            toastMessage = "Resan startad";

        } else if (change.equals("Trip removed")) {

            title = "Resa borttagen";
            body = "En resa du var med i har tagits bort";
            toastMessage = "Resa borttagen";

        } else {
            toastMessage = "";
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, App.CHANNEL_NORMAL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setContentInfo("Ny passagerare")
                .setLargeIcon(icon)
                .setColor(Color.RED)
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_directions_car_black_24dp);

        try {
            String picture_url = data.get("picture_url");
            if (picture_url != null && !"".equals(picture_url)) {
                URL url = new URL(picture_url);
                Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(notification.getBody())
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) SystemClock.uptimeMillis(), notificationBuilder.build());

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
            }
        });


        if (change.equals("Trip started")) {
            Notification activeTripNotification = new NotificationCompat.Builder(this, App.CHANNEL_ACTIVE_TRIP_ID)
                    .setContentTitle("Pågående resa")
                    .setContentText("Klicka för att gå till resan")
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_directions_car_black_24dp)
                    .build();
            if (tripId == null) throw new IllegalStateException("there should be a trip for this type");
            notificationManager.notify(tripId.hashCode(), activeTripNotification);
        }

    }

}
