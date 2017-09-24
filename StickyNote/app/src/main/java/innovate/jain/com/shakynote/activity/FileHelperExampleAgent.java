
package innovate.jain.com.shakynote.activity;

import java.io.File;
import java.io.IOException;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import innovate.jain.com.shakynote.dao.DatabaseHandler;

public class FileHelperExampleAgent extends BackupAgentHelper {
    static final String FILE_HELPER_KEY = DatabaseHandler.DATABASE_NAME;
    static final Object[] sDataLock = new Object[0];

    @Override
    public void onCreate() {

        File file = getApplicationContext().getDatabasePath(DatabaseHandler.DATABASE_NAME);
        FileBackupHelper helper = new FileBackupHelper(this, file.getPath());
        addHelper(FILE_HELPER_KEY, helper);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        synchronized (sDataLock) {
            super.onBackup(oldState, data, newState);

            File file = getApplicationContext().getDatabasePath(DatabaseHandler.DATABASE_NAME);
            long fileSize = file.length();
            Log.i("Testing testing", "on Backup called with file size " + fileSize);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        synchronized (sDataLock) {
            super.onRestore(data, appVersionCode, newState);

            while (data.readNextHeader()) {
                Log.i("Testing testing", "on Restore called and the data key is " + data.getKey());
            }

//            File file = getApplicationContext().getDatabasePath(DatabaseHandler.DATABASE_NAME);
//            long fileSize = file.length();
            Log.i("Testing testing", "on Restore called with file size " + data.getKey() );
        }
    }
}