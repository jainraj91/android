package innovate.jain.com.shakynote.task;

import android.content.Context;
import android.os.AsyncTask;

import innovate.jain.com.shakynote.dao.DatabaseHandler;
import innovate.jain.com.shakynote.model.Note;

/**
 * Created by rajat on 1/1/2016
 */
public class UpdateNote extends AsyncTask<Void, Void, Void> {

    private DatabaseHandler databaseHandler;
    private Note note;

    public UpdateNote(Context context, Note note) {
        databaseHandler = new DatabaseHandler(context);
        this.note = note;
    }

    @Override
    protected Void doInBackground(Void... params) {
        databaseHandler.updateNote(note);
        return null;
    }
}
