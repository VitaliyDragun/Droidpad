package logic;

import java.text.BreakIterator;

import vitaliy.dragun.droidpad_2nd_edition.MyApplication;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preferences
{
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());;
	Editor editor = preferences.edit();
	
	public final static String SORT_TYPE = "Sort Type";
	public final static String ALPHABETICALLY = "Alphabetically";
	public final static String BY_DATE = "By Date";
	public final static String BY_TYPE = "By Type";
	
	public final static String ROW_SIZE = "Row Size";
	public final static String BIG = "Big";
	public final static String MEDIUM = "Medium";
	public final static String SMALL = "Small";
	
	public final static String PASSWORD = "Password";
	public final static String DEFAULT_PASSWORD = "1111";
	
	private final static String ENABLE_SOUND_EFFECTS = "enable sound effects";
	private final static String CREATE_NEW_NOTE_AT_LAUNCH = "Create new note at launch";
	private final static String ASK_BEFORE_EXIT = "Ask before exit";
	private final static String NOTE_OPEN_MODE = "Open note mode";
	
	public final static String EDIT_MODE = "Edit mode";
	public final static String VIEW_MODE = "View mode";
	
	public enum SortType {ALPHABATICALLY, BY_DATE, BY_TYPE};
	public enum RowSize {BIG, MEDIUM, SMALL};
	
	public boolean isSoundEnabled()
	{
		if ( preferences.getBoolean( ENABLE_SOUND_EFFECTS, false ) == true )
			return true;
		else return false;
	}

	public boolean checkPasswordValid(String enteredPassword)
	{
		String validPassword = preferences.getString(PASSWORD, null);
		if (enteredPassword == validPassword)
			return true;
		else return false;
	}
	
	public void setPassword( String newPassword )
	{
		editor.putString( PASSWORD, DEFAULT_PASSWORD );
		editor.commit();
	}
	
	public void setRowSize( RowSize rowSize )
	{
		switch (rowSize)
		{
		case BIG:
			editor.putString( ROW_SIZE, BIG);
			break;
		case MEDIUM:
			editor.putString( ROW_SIZE, MEDIUM);
			break;
		case SMALL:
			editor.putString( ROW_SIZE, SMALL);
		}
		
		editor.commit();
	}
	
	public RowSize getRowSize()
	{
		String returnedValue = preferences.getString( ROW_SIZE, MEDIUM );
		
		if (returnedValue.equals(BIG))
			return RowSize.BIG;
		else if (returnedValue.equals(MEDIUM))
			return RowSize.MEDIUM;
		else
			return RowSize.SMALL;
	}
	
	public void setSortType ( SortType sortType )
	{
		switch (sortType)
		{
		case BY_DATE:
			editor.putString( SORT_TYPE, BY_DATE);
			break;
		case ALPHABATICALLY:
			editor.putString( SORT_TYPE, ALPHABETICALLY);
			break;
		case BY_TYPE:
			editor.putString( SORT_TYPE, BY_TYPE);
		}
		
		editor.commit();
	}
	
	public SortType getSortType() 
	{
		String returnedValue = preferences.getString( SORT_TYPE, "" );

		if (returnedValue.equals(ALPHABETICALLY))
			return SortType.ALPHABATICALLY;
		else if (returnedValue.equals(BY_DATE))
			return SortType.BY_DATE;
		else
			return SortType.BY_TYPE;
	}
	
	public void setNoteOpenMode( String value )
	{
		if(value == EDIT_MODE || value == VIEW_MODE)
		{
			editor.putString(NOTE_OPEN_MODE, value);
			editor.commit();
		}
	}
	
	public String getNoteOpenMode() { return preferences.getString( NOTE_OPEN_MODE, EDIT_MODE ); } 
	
	public void setCreateNoteAtLaunch( boolean value ) { editor.putBoolean( CREATE_NEW_NOTE_AT_LAUNCH, value ); editor.commit(); }
	public boolean getCreateNoteAtLaunch() { return preferences.getBoolean( CREATE_NEW_NOTE_AT_LAUNCH, false ); }
	
	public void setAskBeforeExit( boolean value ) { editor.putBoolean( ASK_BEFORE_EXIT, value); editor.commit(); }
	public boolean getAskBeforeExit() { return preferences.getBoolean( ASK_BEFORE_EXIT, true ); }
	
	public void setEnableSoundAffects( boolean value ) { editor.putBoolean(ENABLE_SOUND_EFFECTS, value);  editor.commit(); }
	public boolean getEnableSoundEffects() { return preferences.getBoolean(ENABLE_SOUND_EFFECTS, true); }
}
