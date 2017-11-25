package com.appdevelapp.samplealarmapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CLASS_TAG = AlarmReceiver.class.getSimpleName();
    int notifyId=1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(CLASS_TAG, "Setting Alarm");
        NotificationCompat.Builder mNotify=new NotificationCompat.Builder(context);
        mNotify.setSmallIcon(R.drawable.ic_launcher_foreground);
        mNotify.setContentTitle("Coding");
        mNotify.setContentText("INVENTO: Coding competition is going to be conducted today.");
        Intent resultIntent=new Intent(context,EndActivity.class);
        TaskStackBuilder stackBuilder=TaskStackBuilder.create(context);
        stackBuilder.addParentStack(EndActivity.class); //add the to-be-displayed activity to the top of stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mNotify.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyId,mNotify.build());
    }
}
