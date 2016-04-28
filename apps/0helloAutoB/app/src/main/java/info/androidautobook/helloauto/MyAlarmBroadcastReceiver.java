package info.androidautobook.helloauto;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import android.util.Log;

import java.util.Date;

public class MyAlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int referenceId = (int) SystemClock.elapsedRealtime() ;
        String messageTitle = "HelloAuto" ;
        String messageContent = "HelloAuto Alarm was Triggered at "  + new Date() ;
        Log.d ( TAG, "onReceive()... " + referenceId ) ;
        originateSimpleNotification( context, referenceId, messageTitle, messageContent);
    }

   private  void originateSimpleNotification ( Context context, int referenceId, String messageTitle, String messageContent) {
       Notification notification = new Notification.Builder(context)
               .setSmallIcon(R.drawable.alarm36)
               .setContentTitle(messageTitle)
               .setContentText(messageContent)
               .setColor( context.getResources().getColor(R.color.colorPrimary)).build() ;

       NotificationManager notificationManager =
               (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       notificationManager.notify( referenceId, notification );
   }

    private static  final String TAG = MyAlarmBroadcastReceiver.class.getName() ;
}
