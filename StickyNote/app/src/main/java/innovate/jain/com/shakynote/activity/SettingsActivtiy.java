package innovate.jain.com.shakynote.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import innovate.jain.com.shakynote.R;
import innovate.jain.com.shakynote.service.SensorListenerService;
import innovate.jain.com.shakynote.utils.AppUtils;

/**
 * Created by rajat on 1/10/2016
 */
public class SettingsActivtiy extends Activity {

    private static Context context;
    public static final String IS_SHAKE_ACTIVATED = "shake_open";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(R.id.container, new PrefFragment()).commit();
        context = this;
    }

    public static class PrefFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals("shake_open")) {
                boolean isShakeActivated = AppUtils.isShakeActivated(context);
                if (isShakeActivated) {
                    Intent intent = new Intent(context, SensorListenerService.class);
                    context.startService(intent);
                    Toast.makeText(context, "Shake Activated", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(context, SensorListenerService.class);
                    context.stopService(intent);
                    Toast.makeText(context, "Shake Deactivated", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
