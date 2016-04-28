package info.androidautobook.helloauto;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

public class MessageReadBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d ( TAG, "onReceive()..." + new Date() ) ;
        int referenceId = intent.getIntExtra("reference_id", -1);

        if ( referenceId > 0 ) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel( referenceId);
        } else {
            Log.w ( TAG, "onReceive() could not find notification to cancel "   ) ;
        }
    }

    private static  final String TAG = MessageReadBroadcastReceiver.class.getName() ;
}
