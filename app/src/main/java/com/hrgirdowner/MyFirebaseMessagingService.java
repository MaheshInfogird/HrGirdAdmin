package com.hrgirdowner;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    public static final String MyPREFERENCES = "MyPrefs_notify" ;
    int PRIVATE_MODE = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) 
    {
        pref = getApplicationContext().getSharedPreferences(MyPREFERENCES, PRIVATE_MODE);
        boolean notification = pref.getBoolean("notification", false);
        
        if (notification)
        {
            /*try
            {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                Log.i("json: ", ""+ json);
                String title = json.getString("title");
                Log.i("title: ", ""+ title);
                String body = json.getString("message");
                Log.i("body: ", ""+ body);

                sendNotification(title, body);
            }
            catch (Exception e) {
                Log.e("Exception: ", ""+ e.getMessage());
            }*/

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.i("body: ", ""+ body);
            Log.i("title: ", ""+ title);
            
            sendNotification(title, body);
        }
        else {
            Log.i("disable", ""+notification);
        }
    }

    private void sendNotification(String messageTitle, String messageBody) 
    {
        Intent intent;
        
        if (messageTitle.equals("Birthday"))
        {
            intent = new Intent(this, BirthdayActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else {
            intent = new Intent(this, DashBoard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Uri sound = Uri.parse(("android.resource://" + getPackageName() + "/" + R.raw.app_audio));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(new long[] {1000, 1000, 1000, 1000})
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
