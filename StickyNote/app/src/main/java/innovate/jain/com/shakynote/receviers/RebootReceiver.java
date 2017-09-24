package innovate.jain.com.shakynote.receviers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

import innovate.jain.com.shakynote.dao.DatabaseHandler;
import innovate.jain.com.shakynote.model.Note;
import innovate.jain.com.shakynote.service.SensorListenerService;
import innovate.jain.com.shakynote.utils.AlarmUtility;
import innovate.jain.com.shakynote.utils.AppUtils;

/**
 * Created by rajat on 11/9/2015
 */
public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppUtils.isShakeActivated(context)) {
            Toast.makeText(context, "Device Restarted so starting the service", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(context, SensorListenerService.class);
            context.startService(serviceIntent);
        }

        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        List<Note> notes = databaseHandler.getAllNotes();

        if (notes != null && notes.size() > 0) {
            for (Note note : notes) {
                if (note.isActive() == 1) {

                    Intent intentForNotification = new Intent(context,
                            AlarmReceiver.class);

                    intentForNotification.putExtra("note", note);

                    long currentTime = System.currentTimeMillis();
                    long setTime = AppUtils.getTimeInMillis(note.getDay(), note.getMonth(),
                            note.getYear(), note.getMin(), note.getHour());

                    if (setTime > currentTime) {
                        AlarmUtility.alarmOn(context, intentForNotification, note.getId(), note.getDay(),
                                note.getMonth(), note.getYear(), note.getHour(), note.getMin());
                    } else {
                        note.setIsActive(0);
                        databaseHandler.updateNote(note);
                    }
                }
            }
        }
    }
}
