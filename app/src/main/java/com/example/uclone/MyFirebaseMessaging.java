package com.example.uclone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

       try{
           if (firebaseUser != null &&
                   sented.equals
                           (FirebaseAuth.getInstance().getUid())){
               if (!currentUser.equals(user)) {
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                       sendOreoNotification(remoteMessage);
                   } else {
                       sendNotification(remoteMessage);
                   }
               }
           }
       }
       catch (Exception e){}

    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        Intent intent;
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        Bundle bundle = new Bundle();

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        if(title.equals("Property Notification")){


            String phone = remoteMessage.getData().get("phone");
            String allimg = remoteMessage.getData().get("allimg");
            String price = remoteMessage.getData().get("price");
            String propertyname = remoteMessage.getData().get("propertyname");
            String bedroom = remoteMessage.getData().get("bedroom");
            String bathroom = remoteMessage.getData().get("bathroom");
            String user_id = remoteMessage.getData().get("user_id");
            String appid = remoteMessage.getData().get("appid");
            String locationltlng = remoteMessage.getData().get("locationltlng");
            String sqft = remoteMessage.getData().get("sqft");
            String location = remoteMessage.getData().get("location");
            String status = remoteMessage.getData().get("status");
            String freetext = remoteMessage.getData().get("freetext");

            intent = new Intent(this, MapFragment.class);

            bundle.putString("phone", phone);
            bundle.putString("allimg", allimg);
            bundle.putString("price", price);
            bundle.putString("propertyname", propertyname);
            bundle.putString("bedroom", bedroom);
            bundle.putString("bathroom", bathroom);
            bundle.putString("proid", appid);
            bundle.putString("locationltlng", locationltlng);
            bundle.putString("sqft", sqft);
            bundle.putString("location", location);
            bundle.putString("status", status);
            bundle.putString("statusprop", status);
            bundle.putString("freetext", freetext);
            bundle.putString("user_id", user_id);
            intent.putExtras(bundle);
        }else{
            intent = new Intent(this, LoginActivity.class);
            bundle.putString("user_id", user);
            intent.putExtras(bundle);
        }

        /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = 0;
        if (j > 0){
            i = j;
        }

        oreoNotification.getManager().notify(i, builder.build());

    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent;
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        Bundle bundle = new Bundle();

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        if(title.equals("Property Notification")){


            String phone = remoteMessage.getData().get("user");
            String allimg = remoteMessage.getData().get("allimg");
            String price = remoteMessage.getData().get("price");
            String propertyname = remoteMessage.getData().get("propertyname");
            String bedroom = remoteMessage.getData().get("bedroom");
            String bathroom = remoteMessage.getData().get("bathroom");
            String user_id = remoteMessage.getData().get("user_id");
            String appid = remoteMessage.getData().get("appid");
            String locationltlng = remoteMessage.getData().get("locationltlng");
            String sqft = remoteMessage.getData().get("sqft");
            String location = remoteMessage.getData().get("sqft");
            String status = remoteMessage.getData().get("sqft");
            intent = new Intent(this, LoginActivity.class);

            bundle.putString("phone", phone);
            bundle.putString("allimg", allimg);
            bundle.putString("price", price);
            bundle.putString("propertyname", propertyname);
            bundle.putString("bedroom", bedroom);
            bundle.putString("bathroom", bathroom);
            bundle.putString("appid", appid);
            bundle.putString("locationltlng", locationltlng);
            bundle.putString("sqft", sqft);
            bundle.putString("location", location);
            bundle.putString("statusprop", status);
            bundle.putString("status", status);
            bundle.putString("user_id", user_id);
            intent.putExtras(bundle);
        }else{
            intent = new Intent(this, LoginActivity.class);
            bundle.putString("user_id", user);
            intent.putExtras(bundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0){
            i = j;
        }

        noti.notify(i, builder.build());
    }
}