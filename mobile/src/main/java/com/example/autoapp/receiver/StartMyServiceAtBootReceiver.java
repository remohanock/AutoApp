package com.example.autoapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.autoapp.activity.HomeActivity;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, HomeActivity.class);
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(serviceIntent);
        }
    }
}
