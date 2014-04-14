package database;

import java.util.ArrayList;
import java.util.List;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FoldersDataSource
{
    private SQLiteDatabase database;
    private MyDatabaseHelper dbHelper;

    public FoldersDataSource(Context context)
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

    public Folder createNote(Folder folder)
    {
	ContentValues values = new ContentValues();

	values.put( MyDatabaseHelper.TABLE_FOLDERS_COLUMN_TITLE, folder.title );
	values.put( MyDatabaseHelper.TABLE_FOLDERS_COLUMN_DATE, folder.date );
	values.put( MyDatabaseHelper.TABLE_FOLDERS_COLUMN_FOLDER, folder.locationFolder );
	values.put( MyDatabaseHelper.TABLE_FOLDERS_COLUMN_ENTRY_TYPE, folder.type.toString() );
	values.put( MyDatabaseHelper.TABLE_FOLDERS_COLUMN_PROTECTED, folder.isProtected );
	values.put( MyDatabaseHelper.TABLE_FOLDERS_COLUMN_PRIORITY, folder.priority );
	values.put ( MyDatabaseHelper.TABLE_FOLDERS_COLUMN_NUMBER_OF_NOTES_IN_FOLDER, folder.notesInside);

	long insertId = database.insert(MyDatabaseHelper.TABLE_NOTES, null, values);

	Cursor cursor = database.query( MyDatabaseHelper.TABLE_NOTES, MyDatabaseHelper.TABLE_NOTES_ALL_COLUMNS,
		MyDatabaseHelper.TABLE_NOTES_COLUMN_ID + "=" + insertId,
		null, null, null, null );
	cursor.moveToFirst();
	Folder newFolder = cursorToNote(cursor);
	cursor.close();

	return newFolder;
    }

    public Folder updateFolder(Folder folder)
    {
	ContentValues values = new ContentValues();

	values.put(MyDatabaseHelper.TABLE_FOLDERS_COLUMN_TITLE, folder.title);
	values.put(MyDatabaseHelper.TABLE_FOLDERS_COLUMN_DATE, folder.date);
	values.put(MyDatabaseHelper.TABLE_FOLDERS_COLUMN_FOLDER, folder.locationFolder);
	values.put(MyDatabaseHelper.TABLE_FOLDERS_COLUMN_PROTECTED, folder.isProtected);
	values.put(MyDatabaseHelper.TABLE_FOLDERS_COLUMN_PRIORITY, folder.priority);
	values.put ( MyDatabaseHelper.TABLE_FOLDERS_COLUMN_NUMBER_OF_NOTES_IN_FOLDER, folder.notesInside);

	database.update(MyDatabaseHelper.TABLE_NOTES, values, MyDatabaseHelper.TABLE_NOTES_COLUMN_ID + "=" + folder.id, null);

	return folder;
    }

    public void deleteFolder(Folder folder)
    {
	long id = folder.id;

	database.delete( MyDatabaseHelper.TABLE_FOLDERS, MyDatabaseHelper.TABLE_FOLDERS_COLUMN_ID + "=" + id, null );
    }

    public List<Folder> getAllFolders()
    {
	List<Folder> folders = new ArrayList<Folder>();

	Cursor cursor = database.query(MyDatabaseHelper.TABLE_FOLDERS,
		MyDatabaseHelper.TABLE_FOLDERS_ALL_COLUMNS,
		null, null, null, null, null);

	cursor.moveToFirst();

	while(!cursor.isAfterLast())
	{

	    Folder folder = cursorToNote(cursor);
	    folders.add(folder);
	    cursor.moveToNext();
	}

	cursor.close();

	return folders;
    }

    private Folder cursorToNote(Cursor cursor)
    {	
	Folder folder = new Folder();

	folder.id = cursor.getLong( 0 );
	folder.title = cursor.getString( 1 );
	folder.date = cursor.getString( 2 );
	folder.locationFolder = cursor.getString( 3 );
	folder.type = Item.Type.values()[ cursor.getInt( 4 ) ] ;
	folder.isProtected = cursor.getInt( 5 );
	folder.priority = cursor.getInt( 6 );
	folder.notesInside = cursor.getInt( 7 );

	return folder;
    }
}
