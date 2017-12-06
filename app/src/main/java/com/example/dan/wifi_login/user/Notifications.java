package com.example.dan.wifi_login.user;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.example.dan.wifi_login.R;

/**
 * Created by DAN on 2017/5/3.
 */

public class Notifications {
    Context mContext;
   int notifyID = 10; // 通知的識別號碼
    Notification notification;
    NotificationManager notificationManager;
    public Notifications(Context mContext){this.mContext = mContext;
    }
public  void sendNofifications(String SSid) {
    notifyID=10;
    final int priority = Notification.PRIORITY_MAX; // 通知的優先權，可用PRIORITY_MAX、PRIORITY_HIGHT、PRIORITY_LOW、PRIORITY_MIN、PRIORITY_DEFAULT
         notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        notification = new Notification.Builder(mContext.getApplicationContext()).setSmallIcon(R.drawable.wifisharing).setContentTitle("WIFI連線中").setContentText("目前WIFI\n "+SSid).setPriority(priority).build(); // 建立通知
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 將ongoing(持續)的flag添加到通知中
        notificationManager.notify(notifyID, notification); // 發送通知
    }
    public  void StopNofifications() {
        if(notifyID==10){
        notificationManager.cancel(notifyID);
        }
    }
}
