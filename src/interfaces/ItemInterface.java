package interfaces;

import structures.Item.Type;

public interface ItemInterface 
{
    public Type getType();
	public long getId();
	public String getDate();
	public String getTitle();
	public boolean getIsTitleAdded();
	public int getPriority();
	public boolean getIsProtected();
	public String getLocationFolder();
	public String getNote();
	public int getNotesInside();
}
