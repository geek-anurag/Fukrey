package com.example.neelmani.fukrey.GCM;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GCMsgnBroadcastReceiver extends WakefulBroadcastReceiver{

    @Override
    public void onReceive(Context contextReceived, Intent intent) {
        // Attach component of GCMsgnIntentService that will handle the intent in background thread
        ComponentName component = new ComponentName(contextReceived.getPackageName(),
                GCMsgnIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(contextReceived, (intent.setComponent(component)));
        setResultCode(Activity.RESULT_OK);
    }
}