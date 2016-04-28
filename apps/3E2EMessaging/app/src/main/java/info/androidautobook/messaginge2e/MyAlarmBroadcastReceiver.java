package info.androidautobook.messaginge2e;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;

import android.util.Log;

import java.util.Date;

public class MyAlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION_READ = "info.androidautobook.messaginge2e.ACTION_READ" ;
    private static  final String REPLY_ACTION =  "info.androidautobook.messaginge2e.REPLY_ACTION" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        int referenceId = (int) SystemClock.elapsedRealtime() ;

        String messageTitle = "HelloAuto" ;
        String messageContent = "HelloAuto Alarm was Triggered at "  + new Date() ;

        Log.d ( TAG, "onReceive() NCT... " + referenceId ) ;
        originateCarExtendedNotification( context, referenceId, messageTitle, messageContent);
    }


    private  void originateCarExtendedNotification ( Context context, int referenceId,
                                                     String messageTitle, String messageContent ) {

        PendingIntent readPendingIntent = PendingIntent.getBroadcast(context,
                referenceId,
                new Intent().addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES).setAction(ACTION_READ).putExtra("reference_id", referenceId),
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel( messageTitle)
                .build();

        PendingIntent replyIntent = PendingIntent.getBroadcast(context,
                referenceId,
                new Intent().addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES).setAction(REPLY_ACTION).putExtra("conversation_id", referenceId),
                PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.CarExtender.Builder unReadConversationsBuilder = new Notification.CarExtender.Builder(messageTitle) ;
        unReadConversationsBuilder.addMessage(messageContent);
        unReadConversationsBuilder.setLatestTimestamp(System.currentTimeMillis()) ;
        unReadConversationsBuilder.setReadPendingIntent( readPendingIntent)  ;
        unReadConversationsBuilder.setReplyAction(replyIntent, remoteInput);

        Log.d ( TAG, "originateCarExtendedNotification() carExtender") ;

        Notification.CarExtender carExtender =  new Notification.CarExtender() ;
        carExtender.setColor(context.getResources().getColor(R.color.colorPrimary));
        carExtender.setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.alarm36)) ;
        carExtender.setUnreadConversation( unReadConversationsBuilder.build() ) ;
        Log.d ( TAG, "originateCarExtendedNotification() carExtender=" + carExtender) ;

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.alarm36)
                .setContentTitle ( messageTitle)
                .setContentText(messageContent )
                .setContentIntent(readPendingIntent)
                .setColor( context.getResources().getColor(R.color.colorPrimary))
                .extend( carExtender)
                .build() ;

        Log.d ( TAG, "originateCarExtendedNotification() notification=" + notification) ;

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify( referenceId, notification );

    }

   private  void originateSimpleNotification ( Context context, int referenceId,
                                               String messageTitle, String messageContent) {
       Notification notification = new Notification.Builder(context)
               .setSmallIcon(R.drawable.alarm36)
               .setContentTitle(messageTitle)
               .setContentText(messageContent )
               .setColor( context.getResources().getColor(R.color.colorPrimary)).build() ;

       NotificationManager notificationManager =
               (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       notificationManager.notify( referenceId, notification );
   }

    private static  final String EXTRA_VOICE_REPLY = "EXTRA_VOICE_REPLY" ;
    private static  final String TAG = MyAlarmBroadcastReceiver.class.getName() ;
}
