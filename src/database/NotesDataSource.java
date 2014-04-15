package database;

import java.util.ArrayList;
import java.util.List;

import structures.Item;
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

	public Item createNote(Item item)
	{
		ContentValues values = new ContentValues();
		values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_TEXT, item.getNote() );
		values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_TITLE, item.getTitle() );
		values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_DATE, item.getDate() );
		values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_FOLDER, item.getLocationFolder() );
		values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_ENTRY_TYPE, item.getType().toString() );
		values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_PROTECTED, item.getIsProtected() );
		values.put( MyDatabaseHelper.TABLE_NOTES_COLUMN_PRIORITY, item.getPriority() );

		long insertId = database.insert(MyDatabaseHelper.TABLE_NOTES, null, values);

		Cursor cursor = database.query( MyDatabaseHelper.TABLE_NOTES, MyDatabaseHelper.TABLE_NOTES_ALL_COLUMNS,
				"rowid"+ "=" + insertId,
				null, null, null, null );
		cursor.moveToFirst();
		Item newNote = cursorToNote(cursor);
		cursor.close();

		return newNote;
	}

	public Item updateNote(Item item)
	{
		ContentValues values = new ContentValues();
		values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_TEXT, item.getNote());
		values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_TITLE, item.getTitle());
		values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_DATE, item.getDate());
		values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_FOLDER, item.getLocationFolder());
		values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_PROTECTED, item.getIsProtected());
		values.put(MyDatabaseHelper.TABLE_NOTES_COLUMN_PRIORITY, item.getPriority());

		database.update(MyDatabaseHelper.TABLE_NOTES, values, MyDatabaseHelper.TABLE_NOTES_COLUMN_ID + "=" + item.getId(), null);

		return item;
	}

	public void deleteNote(Item item)
	{
		long id = item.getId();

		database.delete( MyDatabaseHelper.TABLE_NOTES, MyDatabaseHelper.TABLE_NOTES_COLUMN_ID + "=" + id, null );
	}

	public List<Item> getAllNotes()
	{
		List<Item> notes = new ArrayList<Item>();

		Cursor cursor = database.query(MyDatabaseHelper.TABLE_NOTES,
				MyDatabaseHelper.TABLE_NOTES_ALL_COLUMNS,
				null, null, null, null, null);

		cursor.moveToFirst();

		while(!cursor.isAfterLast())
		{
			Item newNote = cursorToNote(cursor);
			notes.add(newNote);
			cursor.moveToNext();
		}

		cursor.close();

		return notes;
	}

	private Item cursorToNote(Cursor cursor)
	{	
		Item newNote = new Item();

		newNote.setId(cursor.getLong( 0 ));
		newNote.setNote( cursor.getString( 1 ) );
		newNote.setTitle( cursor.getString( 2 ) );
		newNote.setDate( cursor.getString( 3 ) );
		newNote.setLocationFolder( cursor.getString( 4 ) );
		newNote.setType( Item.Type.values()[ cursor.getInt( 5 ) ] );
		
		boolean isProtected = cursor.getInt( 6 ) == 1 ? true : false;
		newNote.setIsProtected( isProtected );
		
		newNote.setPriority( cursor.getInt( 7 ) );

		return newNote;
	}
}
