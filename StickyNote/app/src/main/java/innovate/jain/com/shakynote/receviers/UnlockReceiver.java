package innovate.jain.com.shakynote.receviers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import innovate.jain.com.shakynote.service.SensorListenerService;
import innovate.jain.com.shakynote.utils.AppUtils;

/**
 * Created by rajat on 11/17/2015.
 */
public class UnlockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppUtils.isShakeActivated(context)) {
            Intent intentToService = new Intent(context, SensorListenerService.class);
            context.startService(intentToService);
        }
    }
}
