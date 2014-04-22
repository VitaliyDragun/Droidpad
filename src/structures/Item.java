package structures;

import logic.Preferences;
import android.util.Log;
import interfaces.ItemInterface;

public class Item implements ItemInterface, Comparable<Item>
{
    //public enum Priority {NORMAL, HIGH, VERY_HIGH};
	private final String TAG = Item.class.getName();
	
	private static Preferences.SortType sortType;
    
    public static final int PRIORITY_NORMAL = 0;
    public static final int PRIORITY_HIGHT = 1;
    public static final int PRIORITY_VERY_HIGH = 2;

    public enum Type {NOTE, FOLDER};

    private Type type;
    private long id;
    private String date;
    private String title;
    private boolean isTitleAdded = false;
    private int priority = PRIORITY_NORMAL;
	private boolean isProtected = false;
    private String locationFolder;
    private String note;
    private int notesInside = -1;
    
    /**
     * To protect data getter functions return copies of instance variables
     */
    
    public Type getType()
    {
    	return type;
	}
    
	public long getId()
	{ 
		return id;
	}
	
	public String getDate()
	{
		String copy = new String (this.date);
		return copy;
	}
	
	public String getTitle()
	{
		String copy = new String (this.title);
		return copy;
	}
	
	public boolean getIsTitleAdded() {
		return isTitleAdded;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public boolean getIsProtected() {
		return isProtected;
	}
	
	public String getLocationFolder()
	{
		String copy = new String (this.locationFolder); 
		return copy;
	}
	
	public String getNote()
	{
		/*
		final String ERROR_MESSAGE = "ERROR : This is folder, not note";

		if (type == Type.FOLDER)
		{
			Log.e(TAG, ERROR_MESSAGE);
			return ERROR_MESSAGE;
		}
		*/
		String copy = new String (this.note);
		return copy;
	}

	public int getNotesInside()
	{
		return notesInside;
	}
	
	public void setType(Type type)
	{
		/*
		if (type != null)
		{
			Log.e(TAG, "ERROR : type can be set only once at item creation");
			return;
		}
		*/
		
		this.type = type;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setDate(String date)
	{
		if (date == null)
		{
			Log.e(TAG, "ERROR : date is NULL");
			return;
		}
		
		this.date = date;
	}
	
	public void setTitle(String title)
	{
		if (title == null)
		{
			Log.e(TAG, "ERROR : title is NULL");
			return;
		}
		
		this.title = title;
	}
	
	public void setIsTitleAdded(boolean isTitleAdded) {
		this.isTitleAdded = isTitleAdded;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public void setIsProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}
	
	public void setLocationFolder(String locationFolder)
	{
		if (title == null)
		{
			Log.e(TAG, "ERROR : locationFolder is NULL");
			return;
		}
		
		this.locationFolder = locationFolder;
	}

	public void setNote(String note)
	{
		if (note == null)
		{
			Log.e(TAG, "ERROR : note is NULL");
			return;
		}
		else if (type == Type.FOLDER)
		{
			Log.e(TAG, "ERROR : folder can not have text inside");
			return;
		}
		
		this.note = note;
	}

	public void setNotesInside(int notesInside)
	{
		if (type == Type.NOTE)
		{
			Log.e(TAG, "ERROR : note can not have notes inside");
			return;
		}
		
		this.notesInside = notesInside;
	}
	
	public static void setSortType (Preferences.SortType sortType) { Item.sortType = sortType; }
	
public int compareTo(Item comparedItem)
{
	int result;
	
	switch (sortType)
	{
	case BY_DATE:

		result = this.date.compareTo(comparedItem.getDate());
		if (result == 0)
			return 0;
		else if (result < 0)
			return +1;
		else
			return -1;

	case ALPHABATICALLY:
		
		result = this.title.toLowerCase().compareTo(comparedItem.getTitle().toLowerCase());
		if (result == 0)
			return 0;
		else if (result > 0)
			return +1;
		else
			return -1;
		
	case BY_TYPE:
		
		if(this.type == Type.FOLDER && comparedItem.getType() == Type.FOLDER)
			return -2;
		else if(this.type == Type.FOLDER && comparedItem.getType() == Type.FOLDER)
			return +2;
		else if(this.type == Type.FOLDER && comparedItem.getType() == Type.FOLDER)
		{
			result = this.date.compareTo(comparedItem.getDate());
			if(result == 0)
				return 0;
			if(result > 0)
				return -1;
			if(result < 0)
				return +1;
		}
		else if(this.type == Type.FOLDER && comparedItem.getType() == Type.FOLDER)
		{
			result = this.date.compareTo(comparedItem.getDate());
			if(result == 0)
				return 0;
			if(result > 0)
				return -1;
			if(result < 0)
				return +1;
		}
		
	default:
		return 0;
	}
}
}
