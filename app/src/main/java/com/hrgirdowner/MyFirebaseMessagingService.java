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

import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    public static final String MyPREFERENCES = "MyPrefs" ;
    int PRIVATE_MODE = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    UserSessionManager session;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) 
    {

        session = new UserSessionManager(getApplicationContext());
        Log.i("From: ", ""+ remoteMessage.getFrom());
        //Log.i("Message Body:- ", ""+ remoteMessage.getNotification().getBody());
        //Log.i("Message Title:- ", ""+ remoteMessage.getNotification().getTitle());
        
        /*pref = getApplicationContext().getSharedPreferences(MyPREFERENCES, PRIVATE_MODE);
        boolean notification = pref.getBoolean("notification", false);
        
        if (notification){*/
            //sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
            //sendNotification("Hello", "New Notification");
       /* }
        else {
            Log.i("disable", ""+notification);
        }*/

        try
        {
            JSONObject json = new JSONObject(remoteMessage.getData().toString());
            Log.i(TAG, "json: " + json);
            String title = json.getString("title");
            Log.i(TAG, "title: " + title);
            String body = json.getString("message");
            Log.i(TAG, "body: " + body);
            
            sendNotification(title, body);
        }
        catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void sendNotification(String messageTitle, String messageBody) 
    {
        Intent intent;
        
        if (session.isUrlPresent())
        {
            if (session.isUserLoggedIn())
            {
                if (messageTitle.equals("bithday"))
                {
                    intent = new Intent(this, BirthdayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                else {
                    intent = new Intent(this, DashBoard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
            }
            else {
                intent = new Intent(this, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        }
        else {
            intent = new Intent(this, UrlActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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
