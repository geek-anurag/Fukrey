package com.example.neelmani.fukrey.GCM;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.neelmani.fukrey.Fragments.AreaUpdateFragment;
import com.example.neelmani.fukrey.MainActivity;
import com.example.neelmani.fukrey.R;

public class GCMsgnIntentService extends IntentService {

    public static final int GCM_NOTIFICATION_ID = 1000;
    NotificationManager notificationManager;

    public GCMsgnIntentService() {
        super(GCMsgnIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty()) {
           // if(MainActivity.isAppActive)

            // read extras as sent from server
            String message = extras.getString("message");
            String serverTime = extras.getString("timestamp");
            if( message!=null) {
                sendGCMNotification("Message: " + message + " | " /*+ (MessageDetailsActivity.isActive || MainActivity.isActive || AreaMessageActivity.isActive)*/ + "\n" + "Server Time: "
                        + serverTime);
                System.out.println("InGCMIntentService............................\"NewUpdate\" ....1 " + message.equals("NewUpdate"));

                if (message.split("-")[0].equals("NewUpdate")) {
                    System.out.println("InGCMIntentService.............................2 NewUpdate");
                    FetchUpdate();

                }
            }
        }

        //SendAndReceiveMessage.WebQuery sendUpdate = new SendAndReceiveMessage.WebQuery();
        //sendUpdate.execute();

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMsgnBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendGCMNotification(String msg) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        builder.setContentIntent(contentIntent);
        notificationManager.notify(GCM_NOTIFICATION_ID, builder.build());
    }

    private void FetchUpdate()
    {
        System.out.println("InGCMIntentService.............................FetchUpdate");
        AreaUpdateFragment.RefreshAUF(-1);
    }

}
