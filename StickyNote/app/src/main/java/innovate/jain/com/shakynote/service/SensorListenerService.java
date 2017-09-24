package innovate.jain.com.shakynote.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import innovate.jain.com.shakynote.activity.NoteActivity;
import innovate.jain.com.shakynote.listener.AccelerometerListener;
import innovate.jain.com.shakynote.listener.AccelerometerManager;

public class SensorListenerService extends Service implements AccelerometerListener {

    private LockBroadcastReceiver lockBroadcastReceiver;
    private ScreenOnBroadcastReceiver screenOnBroadcastReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Testing testing", "Starting the service");
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        lockBroadcastReceiver = new LockBroadcastReceiver();
        registerReceiver(lockBroadcastReceiver, filter);

        screenOnBroadcastReceiver = new ScreenOnBroadcastReceiver();
        IntentFilter filterScreenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenOnBroadcastReceiver, filterScreenOn);


        if (AccelerometerManager.isSupported(this)) {
            int minMovements = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("shake_intensity", 10);
            AccelerometerManager.startListening(this, minMovements);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Testing testing", "OnStart of a service called");
        if (AccelerometerManager.isSupported(this)) {
            int minMovements = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("shake_intensity", 10);
            AccelerometerManager.startListening(this, minMovements);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onShake() {
        Log.i("Testing testing", "On shake event detected");

        Intent intent = new Intent(this, NoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        Log.i("Testing testing", "Destroying the service and unregistering the receiver");
        AccelerometerManager.stopListening();
        unregisterReceiver(lockBroadcastReceiver);
        unregisterReceiver(screenOnBroadcastReceiver);
        super.onDestroy();
    }

    public class LockBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AccelerometerManager.stopListening();
            Log.i("Testing testing", "I am going to lock thanx for the service");


        }
    }

    public class ScreenOnBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AccelerometerManager.isSupported(context)) {
                int minMovements = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("shake_intensity", 10);
                AccelerometerManager.startListening(SensorListenerService.this, minMovements);
            }
            Log.i("Testing testing", "The screen is on now so enjoy");
//            stopSelf();
        }
    }
}