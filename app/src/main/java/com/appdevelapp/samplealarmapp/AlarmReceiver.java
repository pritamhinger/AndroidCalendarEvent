package com.appdevelapp.samplealarmapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CLASS_TAG = AlarmReceiver.class.getSimpleName();
    int notifyId=1;

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d(CLASS_TAG, "Setting Alarm");
//        NotificationCompat.Builder mNotify=new NotificationCompat.Builder(context);
//        mNotify.setSmallIcon(R.drawable.ic_launcher_foreground);
//        mNotify.setContentTitle("Coding");
//        mNotify.setContentText("INVENTO: Coding competition is going to be conducted today.");
//        Intent resultIntent=new Intent(context,EndActivity.class);
//        TaskStackBuilder stackBuilder=TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(EndActivity.class); //add the to-be-displayed activity to the top of stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//        mNotify.setContentIntent(resultPendingIntent);
//        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(notifyId,mNotify.build());
        long when = System.currentTimeMillis();
        String title = "Byaj Name";
        if(intent.hasExtra("LoanName")){
            title = intent.getStringExtra("LoanName");
        }

        Log.d(CLASS_TAG, "Creating Notification Manager");
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d(CLASS_TAG, "Creating Intent");
        Intent notificationIntent = new Intent(context, MainActivity.class);

        Log.d(CLASS_TAG, "Setting Flags to Intent");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.d(CLASS_TAG, "Creating Pending Intent");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Log.d(CLASS_TAG, "Setting Alarm Sound");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Log.d(CLASS_TAG, "Creating Notify Builder");
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText("Events to be Performed").setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(notifyId, mNotifyBuilder.build());

        Log.d(CLASS_TAG, "Incrementing Notification Id");
        notifyId++;
    }
}
