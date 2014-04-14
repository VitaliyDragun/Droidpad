package database;

import java.util.ArrayList;
import java.util.List;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NotesDataSource
{
    private SQLiteDatabase database;
    private MyDatabaseHelper dbHelper;

    public NotesDataSource(Context context)
    {
	dbHelper = new MyDatabaseHelper(context);
    }

    public void open() throws SQLException
    {
	database = dbHelper.getWritableDatabase();
    }
    public void close()
    {
	dbHelper.close();
    }

    public Note createNote(Note note)
    {
	ContentValues values = new ContentValues();
	values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_TEXT, note.note );
	values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_TITLE, note.title );
	values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_DATE, note.date );
	values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_FOLDER, note.locationFolder );
	values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_ENTRY_TYPE, note.type.toString() );
	values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_PROTECTED, note.isProtected );
	values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_PRIORITY, note.priority );

	long insertId = database.insert(MyDatabaseHelper.TABLE_NOTES, null, values);

	Cursor cursor = database.query( MyDatabaseHelper.TABLE_NOTES, MyDatabaseHelper.TABLE_NOTES_ALL_COLUMNS,
		"rowid"+ "=" + insertId,
		null, null, null, null );
	cursor.moveToFirst();
	Note newNote = cursorToNote(cursor);
	cursor.close();

	return newNote;
    }

    public Note updateNote(Note note)
    {
	ContentValues values = new ContentValues();
	values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_TEXT, note.note);
	values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_TITLE, note.title);
	values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_DATE, note.date);
	values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_FOLDER, note.locationFolder);
	values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_PROTECTED, note.isProtected);
	values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_PRIORITY, note.priority);

	database.update(MyDatabaseHelper.TABLE_NOTES, values, MyDatabaseHelper.TABLE_NOTES_COLUMN_ID + "=" + note.id, null);

	return note;
    }

    public void deleteNote(Note note)
    {
	long id = note.id;

	database.delete( MyDatabaseHelper.TABLE_NOTES, MyDatabaseHelper.TABLE_NOTES_COLUMN_ID + "=" + id, null );
    }

    public List<Note> getAllNotes()
    {
	List<Note> notes = new ArrayList<Note>();

	Cursor cursor = database.query(MyDatabaseHelper.TABLE_NOTES,
		MyDatabaseHelper.TABLE_NOTES_ALL_COLUMNS,
		null, null, null, null, null);

	cursor.moveToFirst();

	while(!cursor.isAfterLast())
	{
	    Note newNote = cursorToNote(cursor);
	    notes.add(newNote);
	    cursor.moveToNext();
	}

	cursor.close();

	return notes;
    }

    private Note cursorToNote(Cursor cursor)
    {	
	Note newNote = new Note();

	newNote.id = cursor.getLong( 0 );
	newNote.note = cursor.getString( 1 );
	newNote.title = cursor.getString( 2 );
	newNote.date = cursor.getString( 3 );
	newNote.locationFolder = cursor.getString( 4 );
	newNote.type = Item.Type.values()[ cursor.getInt( 5 ) ] ;
	newNote.isProtected = cursor.getInt( 6 );
	newNote.priority = cursor.getInt( 7 );

	return newNote;
    }
}
