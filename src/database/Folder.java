package database;


public class Folder extends Item
{
    public int notesInside = -1;

    public Folder()
    {
	type = Type.FOLDER;
    }

    public Folder(Folder folderToCopy)
    {
	notesInside = folderToCopy.notesInside;
	title = folderToCopy.title;
	id = folderToCopy.id;
	date = folderToCopy.date;
	type = folderToCopy.type;
	isProtected = folderToCopy.isProtected;
	locationFolder = folderToCopy.locationFolder;
	priority = folderToCopy.priority;
    }
}
