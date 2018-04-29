package com.example.jasim.plateup.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.jasim.plateup.MainRActivity;
import com.example.jasim.plateup.MainUActivity;
import com.example.jasim.plateup.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        if (MainRActivity.mediaPlayer == null || !MainRActivity.mediaPlayer.isPlaying()) {

            String message = remoteMessage.getData().get("message");


            Intent resultIntent = new Intent(this, MainRActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                            .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                            .setContentTitle("PlateUP")
                            .setContentIntent(resultPendingIntent)
                            .setAutoCancel(true)
                            .setContentText("You have a new order")
                            .setPriority(Notification.PRIORITY_HIGH);


            int mNotificationId = 1;
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

            Uri notification = Uri.parse("android.resource://" + getPackageName() + "/raw/new_order");
            MainRActivity.mediaPlayer = MediaPlayer.create(this, notification);
            MainRActivity.mediaPlayer.setLooping(true);


            if(MainRActivity.getCompleteOrders() == null || MainRActivity.getCompleteOrders().size() != 0) {
                MainRActivity.mediaPlayer.start();
            }
        }
    }
}


