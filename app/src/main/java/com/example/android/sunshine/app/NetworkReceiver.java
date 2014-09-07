package com.example.android.sunshine.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {

    private Command command;

    public NetworkReceiver(Command command) {
        this.command = command;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.v("NetworkReceiver", "Network status changed");
        if (command != null){
            command.Execute();
        }
    }
}
