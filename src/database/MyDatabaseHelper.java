package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "DROIDPAD_DATABASE";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NOTES = "NOTES";
    public static final String TABLE_FOLDERS = "FOLDERS";

    public static final String TABLE_NOTES_COLUMN_ID = "_ID";
    public static final String TABLE_NOTES_COLUMN_TEXT = "TEXT";
    public static final String TABLE_NOTES_COLUMN_TITLE = "TITLE";
    public static final String TABLE_NOTES_COLUMN_DATE = "DATE";
    public static final String TABLE_NOTES_COLUMN_FOLDER = "FOLDER";
    public static final String TABLE_NOTES_COLUMN_ENTRY_TYPE = "ENTRY_TYPE";
    public static final String TABLE_NOTES_COLUMN_PROTECTED = "PROTECTED";
    public static final String TABLE_NOTES_COLUMN_PRIORITY = "PRIORITY";

    public static final String TABLE_FOLDERS_COLUMN_ID = "_ID";
    public static final String TABLE_FOLDERS_COLUMN_TITLE = "TITLE";
    public static final String TABLE_FOLDERS_COLUMN_DATE = "DATE";
    public static final String TABLE_FOLDERS_COLUMN_FOLDER = "FOLDER";
    public static final String TABLE_FOLDERS_COLUMN_ENTRY_TYPE = "ENTRY_TYPE";
    public static final String TABLE_FOLDERS_COLUMN_PROTECTED = "PROTECTED";
    public static final String TABLE_FOLDERS_COLUMN_PRIORITY = "PRIORITY";
    public static final String TABLE_FOLDERS_COLUMN_NUMBER_OF_NOTES_IN_FOLDER = "NUMBER_OF_NOTES_IN_FOLDER";

    public static final String [] TABLE_NOTES_ALL_COLUMNS = { TABLE_NOTES_COLUMN_ID,
	TABLE_NOTES_COLUMN_TEXT,
	TABLE_NOTES_COLUMN_TITLE,
	TABLE_NOTES_COLUMN_DATE,
	TABLE_NOTES_COLUMN_FOLDER,
	TABLE_NOTES_COLUMN_ENTRY_TYPE,
	TABLE_NOTES_COLUMN_PROTECTED,
	TABLE_NOTES_COLUMN_PRIORITY };

    public static final String [] TABLE_FOLDERS_ALL_COLUMNS = { TABLE_FOLDERS_COLUMN_ID,
	TABLE_FOLDERS_COLUMN_TITLE,
	TABLE_FOLDERS_COLUMN_DATE,
	TABLE_FOLDERS_COLUMN_FOLDER,
	TABLE_FOLDERS_COLUMN_ENTRY_TYPE,
	TABLE_FOLDERS_COLUMN_PROTECTED,
	TABLE_FOLDERS_COLUMN_PRIORITY,
	TABLE_FOLDERS_COLUMN_NUMBER_OF_NOTES_IN_FOLDER };

    public static final String SCRIPT_CREATE_TABLE_NOTES = "CREATE TABLE IF NOT EXISTS "+ 
	    TABLE_NOTES + 
	    "(" +
		    TABLE_NOTES_COLUMN_ID + " INTEGER PRIMERY_KEY, " +
			    TABLE_NOTES_COLUMN_TITLE + " TEXT, "+
				    TABLE_NOTES_COLUMN_TEXT + " TEXT, " +
					    TABLE_NOTES_COLUMN_DATE + " TEXT, " +
					    TABLE_NOTES_COLUMN_FOLDER + " TEXT, " +
					    TABLE_NOTES_COLUMN_ENTRY_TYPE + " INTEGER, " +
					    TABLE_NOTES_COLUMN_PROTECTED + " INTEGER, " +
					    TABLE_NOTES_COLUMN_PRIORITY + " INTEGER" +
					    ")";

    public static final String SCRIPT_CREATE_TABLE_FOLDERS = "CREATE TABLE IF NOT EXISTS "+ 
	    TABLE_FOLDERS + 
	    "(" +
	    TABLE_FOLDERS_COLUMN_ID + " INTEGER PRIMERY_KEY, " +
	    TABLE_FOLDERS_COLUMN_TITLE + " TEXT, "+
	    TABLE_FOLDERS_COLUMN_DATE + " TEXT, " +
	    TABLE_FOLDERS_COLUMN_FOLDER + " TEXT, " +
	    TABLE_FOLDERS_COLUMN_ENTRY_TYPE + " INTEGER, " +
	    TABLE_FOLDERS_COLUMN_PROTECTED + " INTEGER, " +
	    TABLE_FOLDERS_COLUMN_PRIORITY + " INTEGER, " +
	    TABLE_FOLDERS_COLUMN_NUMBER_OF_NOTES_IN_FOLDER + " INTEGER" +
	    ")";


    public MyDatabaseHelper(Context context)
    {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {	
	db.execSQL(SCRIPT_CREATE_TABLE_NOTES);
	db.execSQL(SCRIPT_CREATE_TABLE_FOLDERS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {	
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
	db.execSQL("DROP_TABLE IF EXISTS " + TABLE_FOLDERS);

	onCreate(db);
    }
}