package innovate.jain.com.shakynote.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import innovate.jain.com.shakynote.model.Note;
import innovate.jain.com.shakynote.receviers.AlarmReceiver;
import innovate.jain.com.shakynote.utils.AlarmUtility;


/**
 * Created by jain on 03-09-2015
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private final Context context;

    // Database Name
    public static final String DATABASE_NAME = "note_db";

    private static final String TABLE_NOTE = "note";
    private static final String COLUMN_NOTE_ID = "note_id";
    private static final String COLUMN_NOTE_TITLE = "title";
    private static final String COLUMN_NOTE_DESCRIPTION = "description";
    private static final String COLUMN_DATE_CREATED = "note_date";
    private static final String COLUMN_NOTE_COLOR = "note_color";
    private static final String COLUMN_ALARM_DAY = "alarm_day";
    private static final String COLUMN_ALARM_YEAR = "alarm_year";
    private static final String COLUMN_ALARM_MONTH = "alarm_month";
    private static final String COLUMN_ALARM_HOUR = "alarm_hour";
    private static final String COLUMN_ALARM_MIN = "alarm_minute";
    private static final String COLUMN_IS_ACTIVE = "is_active";
    private static final String COLUMN_TYPE = "note_type";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private static final String CREATE_NOTE = "CREATE TABLE " + TABLE_NOTE + " ("
            + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NOTE_TITLE + " TEXT, "
            + COLUMN_DATE_CREATED + " LONG, "
            + COLUMN_NOTE_COLOR + " INTEGER, "
            + COLUMN_NOTE_DESCRIPTION + " TEXT, "
            + COLUMN_ALARM_DAY + " INTEGER, "
            + COLUMN_ALARM_MONTH + " INTEGER, "
            + COLUMN_ALARM_YEAR + " INTEGER, "
            + COLUMN_ALARM_HOUR + " INTEGER, "
            + COLUMN_ALARM_MIN + " INTEGER, "
            + COLUMN_IS_ACTIVE + " INTEGER, "
            + COLUMN_TYPE + " INTEGER DEFAULT 0);";

    private static final String UPDATE_NOTE = "ALTER TABLE " + TABLE_NOTE + " ADD COLUMN " + COLUMN_TYPE + " INTEGER DEFAULT 0";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UPDATE_NOTE);
//        onCreate(db);
    }

    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_NOTE_DESCRIPTION, note.getDescription());
        values.put(COLUMN_NOTE_COLOR, note.getColor());
        values.put(COLUMN_DATE_CREATED, note.getDateCreated());
        values.put(COLUMN_ALARM_DAY, note.getDay());
        values.put(COLUMN_ALARM_MONTH, note.getMonth());
        values.put(COLUMN_ALARM_YEAR, note.getYear());
        values.put(COLUMN_ALARM_HOUR, note.getHour());
        values.put(COLUMN_ALARM_MIN, note.getMin());
        values.put(COLUMN_IS_ACTIVE, note.isActive());
        values.put(COLUMN_TYPE, note.getType());

        long id = db.insert(TABLE_NOTE, null, values);
        Intent intent = new Intent(context,
                AlarmReceiver.class);

        intent.putExtra("note", note);

        if (note.isActive() == 1) {
            AlarmUtility.alarmOn(context, intent, (int) id, note.getDay(),
                    note.getMonth(), note.getYear(), note.getHour(), note.getMin());
        }

        db.close();
    }


    public void updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_NOTE_DESCRIPTION, note.getDescription());
        values.put(COLUMN_NOTE_COLOR, note.getColor());
        values.put(COLUMN_DATE_CREATED, note.getDateCreated());
        values.put(COLUMN_ALARM_DAY, note.getDay());
        values.put(COLUMN_ALARM_MONTH, note.getMonth());
        values.put(COLUMN_ALARM_YEAR, note.getYear());
        values.put(COLUMN_ALARM_HOUR, note.getHour());
        values.put(COLUMN_ALARM_MIN, note.getMin());
        values.put(COLUMN_IS_ACTIVE, note.isActive());
        values.put(COLUMN_TYPE, note.getType());

        Intent intent = new Intent(context,
                AlarmReceiver.class);

        intent.putExtra("note", note);
//        intent.putExtra("id", note.getId());
//        intent.putExtra("title", note.getTitle());
//        intent.putExtra("Description", note.getDescription());

        if (note.isActive() == 1) {
            AlarmUtility.alarmOn(context, intent, note.getId(), note.getDay(),
                    note.getMonth(), note.getYear(), note.getHour(), note.getMin());
        } else {
            AlarmUtility.alarmOff(context, intent, note.getId());
        }

        db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + "= ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NOTE, COLUMN_NOTE_ID + "=" + id, null);
        db.close();
    }


    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Note note = new Note();
        Cursor cursor = db.query(TABLE_NOTE, new String[]{COLUMN_NOTE_ID, COLUMN_NOTE_TITLE,
                        COLUMN_NOTE_DESCRIPTION, COLUMN_DATE_CREATED, COLUMN_NOTE_COLOR, COLUMN_ALARM_YEAR, COLUMN_ALARM_MONTH, COLUMN_ALARM_DAY,
                        COLUMN_ALARM_HOUR, COLUMN_ALARM_MIN, COLUMN_IS_ACTIVE}, COLUMN_NOTE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            note.setId(id);
            note.setTitle(cursor.getString(1));
            note.setDescription(cursor.getString(2));
            note.setDateCreated(cursor.getLong(3));
            note.setColor(cursor.getInt(4));
            note.setDay(cursor.getInt(5));
            note.setMonth(cursor.getInt(6));
            note.setYear(cursor.getInt(7));
            note.setHour(cursor.getInt(8));
            note.setMin(cursor.getInt(9));
            note.setIsActive(cursor.getInt(10));

            cursor.close();

        }
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE + " ORDER BY " + COLUMN_DATE_CREATED + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setDateCreated(cursor.getLong(2));
                note.setColor(cursor.getInt(3));
                note.setDescription(cursor.getString(4));
                note.setDay(cursor.getInt(5));
                note.setMonth(cursor.getInt(6));
                note.setYear(cursor.getInt(7));
                note.setHour(cursor.getInt(8));
                note.setMin(cursor.getInt(9));
                note.setIsActive(cursor.getInt(10));
                note.setType(cursor.getInt(11));
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return noteList;
    }


}
