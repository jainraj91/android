package innovate.jain.com.shakynote.receviers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import innovate.jain.com.shakynote.listener.PhStateListener;

/**
 * Created by rajat on 12/28/2015.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tmgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        //Create Listner
        PhStateListener phStateListener = new PhStateListener(context);

        // Register listener for LISTEN_CALL_STATE
        tmgr.listen(phStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
