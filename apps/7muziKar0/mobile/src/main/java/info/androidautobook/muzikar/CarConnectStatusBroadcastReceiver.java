package info.androidautobook.muzikar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Android Auto Tour Guide / muzikar0
 *
 * CarConnectStatusBroadcastReceiver
 */


public class CarConnectStatusBroadcastReceiver  {

    private final MuziKarMusicService muziKarMusicService;

    public CarConnectStatusBroadcastReceiver( MuziKarMusicService service) {

        muziKarMusicService = service ;

        IntentFilter filter = new IntentFilter("com.google.android.gms.car.media.STATUS");
        BroadcastReceiver receiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("media_connection_status");
                boolean isConnectedToCar = "media_connected".equals(status);
                Log.d ( TAG, "onReceive() isConnectedToCar=" + isConnectedToCar) ;
                // adjust settings based on the connection status


            }
        };
        muziKarMusicService.registerReceiver(receiver, filter);
    }




    private static final String TAG = CarConnectStatusBroadcastReceiver.class.getName() ;
}
