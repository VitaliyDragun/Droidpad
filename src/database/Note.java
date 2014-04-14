package database;


public class Note extends Item
{
    public String note;

    public Note()
    {
	type = Type.NOTE;
    }

    public Note(Note noteToCopy)
    {
	title = noteToCopy.title;
	note = noteToCopy.note;
	id = noteToCopy.id;
	date = noteToCopy.date;
	type = noteToCopy.type;
	isProtected = noteToCopy.isProtected;
	locationFolder = noteToCopy.locationFolder;
	priority = noteToCopy.priority;
    }
}
