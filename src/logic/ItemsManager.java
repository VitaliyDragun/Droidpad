package logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vitaliy.dragun.droidpad_2nd_edition.MyApplication;
import database.Folder;
import database.FoldersDataSource;
import database.Item;
import database.Item.Type;
import database.Note;
import database.NotesDataSource;

public class ItemsManager
{
    private final String MAIN_FOLDER = "My Notes";

    private List<Item> allItems = new ArrayList<Item>();
    private List<Item> itemsToDisplay = new ArrayList<Item>();
    private String currentFolder;

    private Note selectedNote;

    private NotesDataSource notesDataSource;
    private FoldersDataSource foldersDataSource;

    public static String rootFolderTitle = "My Notes";
    public static String backupFolderTitle = "My Notes BACKUP";
    public static String selectedFolder = rootFolderTitle;

    public List<Item> getItems ()
    {
	List <Item> itemsCopy = new ArrayList <Item> (itemsToDisplay);
	return itemsCopy;
    }

    private ItemsManager()
    {
	notesDataSource = new NotesDataSource( MyApplication.getAppContext() );
	notesDataSource.open();

	foldersDataSource = new FoldersDataSource( MyApplication.getAppContext() );
	foldersDataSource.open();

	allItems.addAll( notesDataSource.getAllNotes() );
	allItems.addAll( foldersDataSource.getAllFolders() );
	
	//Fix this
	itemsToDisplay = allItems;
    }

    private static ItemsManager instance = null;

    public static ItemsManager getInstance()
    {
	if(instance == null)
	{
	    instance = new ItemsManager();
	}

	return instance;
    }

    public boolean isTitleUnique( final String title )
    {
	for ( int i = 0; i < allItems.size(); i++ )
	{
	    if ( allItems.get(i).type == Type.NOTE )
		if ( ( (Note)allItems.get( i ) ).locationFolder.equals( selectedFolder ) )
		    if ( allItems.get( i ).title.equals( title) )
			return false;
	}

	return true;
    }

    public void findItemsThatStartWithText(String startText)
    {
	for (int i = 0; i < allItems.size(); i++)
	{
	    if ( allItems.get(i).title.toLowerCase().startsWith( startText.toLowerCase() ) )
		if ( allItems.get(i).locationFolder.equals(rootFolderTitle) )
		    itemsToDisplay.add( allItems.get( i ) );
		else
		{
		    String itemFolderTitle = allItems.get(i).locationFolder;

		    for ( int y = 0; y < allItems.size(); y++ )
			if ( allItems.get( y ).title.equals( itemFolderTitle ) && allItems.get( y ).isProtected != 1 )
			    itemsToDisplay.add( allItems.get(i) );
		}
	}
    }

    public boolean changeSecureStatus(boolean protect, int index)
    {
	return true;
    }

    public void createFolder( String title )
    {
	Folder newFolder = new Folder();

	newFolder.title = title;
	newFolder.type = Type.FOLDER;
	newFolder.locationFolder = currentFolder;
	newFolder.isTitleAdded = 1;
	newFolder.isProtected = 0;
	newFolder.priority = 0;
	newFolder.notesInside = 0;

	SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	String currentDate = formatter.format(new Date());
	newFolder.date = currentDate;
    }

    public boolean createNote(String title, String text)
    {
	Note newNote = new Note();

	if ( ! title.isEmpty() )
	    newNote.title = title;
	else
	{	
	    if(text.indexOf("\n") != -1)
		text = text.substring(0, text.indexOf("\n"));

	    if(text.length() > 30)
		text = text.substring(0, 29);

	    newNote.title = text;
	}

	newNote.note = text;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String currentDate = formatter.format(new Date());
	newNote.date = currentDate;

	newNote.priority = 0;
	newNote.type = Item.Type.NOTE;
	newNote.locationFolder = selectedFolder;
	newNote.isProtected = 0;

	if ( isTitleUnique( newNote.title ) )
	{
	    for ( int i = 0; i < allItems.size(); i++ )
	    {
		if ( allItems.get(i).title.equals(selectedFolder) )
		{
		    ( (Folder) allItems.get( i ) ).notesInside++;
		    foldersDataSource.updateFolder( (Folder) allItems.get(i) );
		}
	    }

	    notesDataSource.createNote( newNote );

	    return true;
	}

	return false;
    }

    public void deleteItem(int index)
    {
	Item itemToDelete = itemsToDisplay.get (index);
	
	if (itemToDelete.type == Item.Type.FOLDER)
	    foldersDataSource.deleteFolder ( (Folder) itemToDelete );
	else
	    notesDataSource.deleteNote ( (Note) itemToDelete );
	
	itemsToDisplay.remove(index);
    }

    public void changeItemTitle(String newTitle, int index)
    {
	if (itemsToDisplay.get(index).type != Item.Type.NOTE)
	{
	    Note note = (Note)itemsToDisplay.get( index );
	    note.title = newTitle;

	    notesDataSource.updateNote(note);
	}
	else
	{
	    String notePrevTitle = itemsToDisplay.get( index ).title;

	    Folder folder = (Folder)itemsToDisplay.get(index);
	    folder.title = newTitle;

	    foldersDataSource.updateFolder(folder);

	    for (int i = 0; i < allItems.size(); i++)
	    {
		if (allItems.get(i).locationFolder.equals(notePrevTitle))
		{
		    Note note = (Note)allItems.get(i);
		    note.locationFolder = newTitle;
		    allItems.set(i, notesDataSource.updateNote(note));
		}
	    }
	}
    }
    
    public void setPriority (int priority, int itemIndex)
    {
	
    }

    public boolean isItemProtected (int index)
    {
	return true;
    }

    public boolean isNote (int index)
    {
	return true;
    }

    public boolean openFolder (int index)
    {
	return true;
    }

    public void updateItems()
    {

    }

    public void sortItems (String sortType)
    {

    }

    public boolean editNote (String title, String text)
    {
	return true;
    }

    public Note getOpenedNote ()
    {
    	Note noteCopy = new Note(selectedNote);
    	return noteCopy;
    }
    
    public void selectNote (int index)
    {
    	selectedNote = (Note) allItems.get(index);
    }
}
