package innovate.jain.com.shakynote.receviers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import innovate.jain.com.shakynote.R;
import innovate.jain.com.shakynote.activity.NoteActivity;
import innovate.jain.com.shakynote.dao.DatabaseHandler;
import innovate.jain.com.shakynote.model.Note;

/**
 * Created by rajat on 12/31/2015
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Note note = (Note) intent.getSerializableExtra("note");
        String title = note.getTitle();
        String description = note.getDescription();

        Notification.Builder mBuilder =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.stickyellow)
                        .setContentTitle(title).setPriority(Notification.PRIORITY_HIGH)
                        .setContentText(description);

        Intent notificationIntent = new Intent(context, NoteActivity.class );
        notificationIntent.putExtra("id", note.getId());

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intentForNoteActivity = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        mBuilder.setContentIntent(intentForNoteActivity);

        Notification notification = new Notification.BigTextStyle(mBuilder)
                .bigText(description).build();
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotifyMgr.notify(001, notification);

        long[] pattern = {0,400,100,600};
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, -1);
        try {
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notificationSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        note.setIsActive(0);
        new DatabaseHandler(context).updateNote(note);
    }
}
