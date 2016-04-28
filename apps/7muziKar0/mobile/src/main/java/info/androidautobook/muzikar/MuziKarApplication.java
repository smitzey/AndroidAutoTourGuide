package info.androidautobook.muzikar;

import android.app.Application;
import android.util.Log;

/**
 * Android Auto Tour Guide / muzikar0
 *
 * MuziKarApplication
 */


public class MuziKarApplication extends Application{

    public MuziKarApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()...") ;
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate()...") ;
        super.onTerminate();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory()...") ;

    }

    private static final String TAG = MuziKarApplication.class.getName();

}
