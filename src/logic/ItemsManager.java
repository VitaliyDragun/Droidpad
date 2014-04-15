package logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import structures.Item;
import structures.Item.Type;
import vitaliy.dragun.droidpad_2nd_edition.MyApplication;
import structures.Item;
import database.NotesDataSource;

public class ItemsManager
{
	private final String MAIN_FOLDER = "My Notes";

	private List<Item> allItems = new ArrayList<Item>();
	private List<Item> itemsToDisplay = new ArrayList<Item>();
	private String currentFolder;

	private Item selectedNote;

	private NotesDataSource notesDataSource;

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
		
		allItems.addAll( notesDataSource.getAllNotes() );

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
			if ( allItems.get(i).getType() == Type.NOTE )
				if ( ( (Item)allItems.get( i ) ).getLocationFolder().equals( selectedFolder ) )
					if ( allItems.get( i ).getTitle().equals( title) )
						return false;
		}

		return true;
	}

	public void findItemsThatStartWithText(String startText)
	{
		for (int i = 0; i < allItems.size(); i++)
		{
			if ( allItems.get(i).getTitle().toLowerCase().startsWith( startText.toLowerCase() ) )
				if ( allItems.get(i).getLocationFolder().equals(rootFolderTitle) )
					itemsToDisplay.add( allItems.get( i ) );
				else
				{
					String itemFolderTitle = allItems.get(i).getLocationFolder();

					for ( int y = 0; y < allItems.size(); y++ )
						if ( allItems.get( y ).getTitle().equals( itemFolderTitle ) && allItems.get( y ).getIsProtected() != true )
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
		Item newFolder = new Item();

		newFolder.setTitle(title);
		newFolder.setType(Type.FOLDER);
		newFolder.setLocationFolder(currentFolder);
		newFolder.setIsTitleAdded(true);
		newFolder.setIsProtected(false);
		newFolder.setPriority(0);
		newFolder.setNotesInside(0);

		SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		String currentDate = formatter.format(new Date());
		newFolder.setDate(currentDate);
	}

	public boolean createNote(String title, String text)
	{
		Item newNote = new Item();

		if ( ! title.isEmpty() )
			newNote.setTitle(title);
		else
		{	
			if(text.indexOf("\n") != -1)
				text = text.substring(0, text.indexOf("\n"));

			if(text.length() > 30)
				text = text.substring(0, 29);

			newNote.setNote(text);
		}

		newNote.setNote(text);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(new Date());
		newNote.setDate(currentDate);

		newNote.setPriority(0);
		newNote.setType(Item.Type.NOTE);
		newNote.setLocationFolder(selectedFolder);
		newNote.setIsProtected(false);

		if ( isTitleUnique( newNote.getTitle() ) )
		{
			for ( int i = 0; i < allItems.size(); i++ )
			{
				if ( allItems.get(i).getTitle().equals(selectedFolder) )
				{

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
		
		notesDataSource.deleteNote ( (Item) itemToDelete );

		itemsToDisplay.remove(index);
	}

	public void changeItemTitle(String newTitle, int index)
	{
		if (itemsToDisplay.get(index).getType() != Item.Type.NOTE)
		{
			Item note = (Item)itemsToDisplay.get( index );
			note.setTitle(newTitle);

			notesDataSource.updateNote(note);
		}
		else
		{
			String notePrevTitle = itemsToDisplay.get( index ).getTitle();

			Item folder = itemsToDisplay.get(index);
			folder.setTitle(newTitle);

			notesDataSource.updateNote(folder);

			for (int i = 0; i < allItems.size(); i++)
			{
				if (allItems.get(i).getLocationFolder().equals(notePrevTitle))
				{
					Item note = (Item)allItems.get(i);
					note.setLocationFolder(newTitle);
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

	public Item getOpenedNote ()
	{
		return selectedNote;
	}

	public void selectNote (int index)
	{
		selectedNote = (Item) allItems.get(index);
	}
}
