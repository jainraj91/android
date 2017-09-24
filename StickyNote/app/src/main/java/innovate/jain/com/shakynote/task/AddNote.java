package innovate.jain.com.shakynote.task;

import android.content.Context;
import android.os.AsyncTask;

import innovate.jain.com.shakynote.dao.DatabaseHandler;
import innovate.jain.com.shakynote.model.Note;

/**
 * Created by rajat on 1/1/2016
 */
public class AddNote extends AsyncTask<Void, Void, Void> {

    private final DatabaseHandler databaseHandler;
    private final Note note;

    public AddNote(Context context, Note note) {
        databaseHandler = new DatabaseHandler(context);
        this.note = note;
    }

    @Override
    protected Void doInBackground(Void... params) {
        databaseHandler.addNote(note);
        return null;
    }
}
