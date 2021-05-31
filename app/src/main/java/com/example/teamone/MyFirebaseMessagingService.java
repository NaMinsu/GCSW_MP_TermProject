package com.example.teamone;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);


    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {  super.onMessageReceived (remoteMessage);

        SendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),remoteMessage.getData().get("Subtext"),remoteMessage.getNotification().getChannelId());

    }
    private void SendNotification(String title, String msg,String subtext,String id) {

        Uri Sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" +R.raw.bell);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* Android Oreo version compatible */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel;
            if(id.equals("Group"))
                channel = new NotificationChannel(id, id, NotificationManager.IMPORTANCE_HIGH);
            else
                channel = new NotificationChannel(id, id, NotificationManager.IMPORTANCE_LOW); /*This is a low-priority notification*/

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            if(id.equals("Group"))
                channel.setSound(Sound,audioAttributes);

            if(manager !=null)
                manager.createNotificationChannel(channel);

        }
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, id)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                .setContentText(msg)
                .setVibrate(new long[]{500, 1000, 500, 1000})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        if(id.equals("Group")) {
            notiBuilder.setSound(Sound);
            Intent intent = new Intent(this,FirstAuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            notiBuilder.setContentIntent(pendingIntent);
        }
        else
            notiBuilder.setSubText(subtext);

        manager.notify(0, notiBuilder.build());

    }


}