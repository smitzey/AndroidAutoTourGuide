package info.androidautobook.helloauto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


public class AlarmSetterActivity extends AppCompatActivity {

    private AlarmManager alarmManager ;
    private PendingIntent operationPendingIntent ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.set_alarm_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setAlarm () ;
                Snackbar.make(view, "Alarm is set for 3 minutes from now", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_setter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private  void setAlarm ()  {
        Log.d ( TAG, "setAlarm()...") ;
        operationPendingIntent = PendingIntent.getBroadcast( getApplicationContext(), 0,
                new Intent( getApplicationContext(), MyAlarmBroadcastReceiver.class) , 0);
        alarmManager=  ( AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime () + 3* SECONDS_IN_MINUTE *  MILLIS_IN_A_SECOND,
                operationPendingIntent );
        Log.d ( TAG, "setAlarm() setExact alarm for 3 minutes later...") ;
        // in real world applications, exact alarms shows be avoided as much as possible for
        // reasons of efficiency / overhead minimization; inexact alarms are less resource intensive
    }

    // trivial, routine constants at the end
    private static  final long MILLIS_IN_A_SECOND = 1000 ;
    private static  final long SECONDS_IN_MINUTE = 60 ;
    private static final String TAG = AlarmSetterActivity.class.getName() ;

}
