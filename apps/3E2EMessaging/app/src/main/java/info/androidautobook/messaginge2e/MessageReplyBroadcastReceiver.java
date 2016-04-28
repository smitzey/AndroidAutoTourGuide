package info.androidautobook.messaginge2e;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.RemoteInput;
import android.os.Bundle ;
import java.lang.CharSequence ;
import android.app.NotificationManager;
import android.app.Notification;

import android.util.Log;
import java.util.Date;

/**
 * This broadcast receiver class is associated with the reply action
 */

public class MessageReplyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d ( TAG, "onReceive()..." + new Date() ) ;

        String voiceReplyContent = "No reply";

        Bundle remoteInputBundle = RemoteInput.getResultsFromIntent(intent);
        Log.d ( TAG, "onReceive() remoteInputBundle=" + remoteInputBundle ) ;
        if (remoteInputBundle != null) {
             CharSequence charSequence = remoteInputBundle.getCharSequence(EXTRA_VOICE_REPLY) ;
            voiceReplyContent = charSequence.toString() ;
        }

        Log.d ( TAG, "onReceive()... voiceReplyContent=" + voiceReplyContent ) ;

        // originate notification that will end up showing on the handheld
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.alarm36)
                .setContentTitle("From Head unit")
                .setContentText( voiceReplyContent )
                .setColor( context.getResources().getColor(R.color.colorPrimary)).build() ;

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify( 7200, notification );

    }

    private static  final String EXTRA_VOICE_REPLY = "EXTRA_VOICE_REPLY" ;
    private static  final String TAG = MessageReplyBroadcastReceiver.class.getName() ;
}
