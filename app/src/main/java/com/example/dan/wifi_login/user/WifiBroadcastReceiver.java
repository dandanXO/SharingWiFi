package com.example.dan.wifi_login.user;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;

import com.example.dan.wifi_login.LoginActivity;
import com.example.dan.wifi_login.R;

/**
 * Created by DAN on 2017/5/4.
 */

public class WifiBroadcastReceiver extends BroadcastReceiver {

    String ssid ="";
    public  WifiBroadcastReceiver(String ssids  ) {
        ssid=ssids;
        ssid="\""+ssid+"\"";
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if (SupplicantState.isValidState(state) && state == SupplicantState.COMPLETED) {
                boolean connected = checkConnectedToDesiredWifi(context);
                if(connected){
                  System.out.println("WifiBroadcastReceiverWifiBroadcastReceiverWifiBroadcastReceiverWifiBroadcastReceiverWifiBroadcastReceiverWifiBroadcastReceiverWifiBroadcastReceiverWifiBroadcastReceiver");
                }else {
                    System.out.println("WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22WB22");
                    showNotification(context,"無法連線");
                }

            }
        }
    }

    /** Detect you are connected to a specific network. */
    private boolean checkConnectedToDesiredWifi(Context context) {
        boolean connected = false;
        String desiredMacAddress = ssid;

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = wifiManager.getConnectionInfo();
        if (wifi != null) {
            // get current router Mac address
            String bssid = wifi.getSSID();
            System.out.println(ssid+"      "+bssid);
            connected = desiredMacAddress.equals(bssid);
    }else{
            connected=false;
        }
        showNotification(context,wifi.getSSID());

        return connected;
    }
    private void showNotification(Context context,String ssids) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, LoginActivity.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.wifisharing)
                        .setContentTitle("WIFI/0")
                        .setContentText("目前WIFI是:"+ssids);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(8, mBuilder.build());

    }
}
