package info.androidautobook.detectautoconnection ;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;


import android.util.Log ;

public class DetectAutoBroadcastReceiver extends BroadcastReceiver {

    public void onReceive ( Context context, Intent intent ) {

        Log.d  (  TAG, "onReceive()..." + intent.getAction()) ;


    }

    private static final String TAG = DetectAutoBroadcastReceiver.class.toString() ;
}