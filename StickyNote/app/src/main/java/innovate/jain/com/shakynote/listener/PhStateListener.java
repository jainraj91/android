package innovate.jain.com.shakynote.listener;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import innovate.jain.com.shakynote.service.SensorListenerService;
import innovate.jain.com.shakynote.utils.AppUtils;

/**
 * Created by rajat on 12/28/2015
 */
public class PhStateListener extends PhoneStateListener {

    private final Context context;

    public PhStateListener(Context context) {
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {

            if (AppUtils.isShakeActivated(context)) {
                Intent serviceIntent = new Intent(context, SensorListenerService.class);
                context.startService(serviceIntent);
            }
        }
    }
}
