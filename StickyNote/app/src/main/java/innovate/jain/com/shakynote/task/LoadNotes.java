package innovate.jain.com.shakynote.task;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

import innovate.jain.com.shakynote.dao.DatabaseHandler;
import innovate.jain.com.shakynote.model.Note;

/**
 * Created by rajat on 1/1/2016
 */
public class LoadNotes extends AsyncTaskLoader<List<Note>> {

    private final DatabaseHandler databaseHandler;
    private List<Note> notes;

    public LoadNotes(Context context) {
        super(context);
        databaseHandler = new DatabaseHandler(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (notes != null) {
            deliverResult(notes);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Note> loadInBackground() {
        return databaseHandler.getAllNotes();
    }

    @Override
    public void deliverResult(List<Note> data) {
        super.deliverResult(data);
        notes = data;
    }
}
