package logic;

import interfaces.ItemInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import logic.Item.Type;

import vitaliy.dragun.droidpad.MyApplication;
import database.ItemsDatabase;

public class ItemsManager
{
	private final String MAIN_FOLDER = "My Notes";
	
	public static String rootFolderTitle = "My Notes";
	public static String backupFolderTitle = "My Notes BACKUP";
	public static String selectedFolder = rootFolderTitle;

	private List<Item> allItems = new ArrayList<Item>();
	private List<Item> itemsInSelectedFolder = new ArrayList<Item>();
	
	private Item currentFolder;		//If null then we are in root folder

	private Item selectedItem;
	private int selectedItemIndex;

	private ItemsDatabase notesDataSource;

	private static ItemsManager instance = null;

	public static ItemsManager getInstance()
	{
		if(instance == null)
		{
			instance = new ItemsManager();
		}

		return instance;
	}

	private ItemsManager()
	{
		currentFolder = new Item();
		currentFolder.setTitle("My Notes");
		
		notesDataSource = new ItemsDatabase( MyApplication.getAppContext() );
		
		notesDataSource.open();
		
		allItems.addAll( notesDataSource.getAllNotes() );

		itemsInSelectedFolder = allItems;
	}
	
	public List<ItemInterface> getItemsInSelectedFolder ()
	{
		List <ItemInterface> copy = new ArrayList<ItemInterface> ();
		
		for (int i = 0; i < itemsInSelectedFolder.size(); i++)
			copy.add(itemsInSelectedFolder.get(i));
		
		return copy;
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
					itemsInSelectedFolder.add( allItems.get( i ) );
				else
				{
					String itemFolderTitle = allItems.get(i).getLocationFolder();

					for ( int y = 0; y < allItems.size(); y++ )
						if ( allItems.get( y ).getTitle().equals( itemFolderTitle ) && allItems.get( y ).getIsProtected() != true )
							itemsInSelectedFolder.add( allItems.get(i) );
				}
		}
	}

	public void changeItemSecurityStatus()
	{
		selectedItem.setIsProtected( selectedItem.getIsProtected() ? false : true );
	}

	public void createFolder( String title )
	{
		Item newFolder = new Item();

		newFolder.setTitle(title);
		newFolder.setNote("No note");
		newFolder.setType(Type.FOLDER);
		newFolder.setLocationFolder(currentFolder.getTitle());
		newFolder.setIsTitleAdded(true);
		newFolder.setIsProtected(false);
		newFolder.setPriority(0);
		newFolder.setNotesInside(0);

		SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		String currentDate = formatter.format(new Date());
		newFolder.setDate(currentDate);
		
		notesDataSource.createNote(newFolder);
		
		itemsInSelectedFolder.add(newFolder);
		
	}

	public boolean createNote(String title, String note)
	{
		Item newNote = new Item();

		if ( ! title.isEmpty() )
			newNote.setTitle(title);
		else
		{	
			if(note.indexOf("\n") != -1)
				note = note.substring(0, note.indexOf("\n"));

			if(note.length() > 30)
				note = note.substring(0, 29);

			newNote.setTitle(note);
		}

		newNote.setNote(note);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(new Date());
		newNote.setDate(currentDate);

		newNote.setPriority(0);
		newNote.setType(Item.Type.NOTE);
		newNote.setLocationFolder(selectedFolder);
		newNote.setIsProtected(false);

		if ( isTitleUnique( newNote.getTitle() ) )
		{
			notesDataSource.createNote( newNote );
			
			itemsInSelectedFolder.add(newNote);

			return true;
		}

		return false;
	}

	public void deleteSelectedItem()
	{
		itemsInSelectedFolder.remove(selectedItem);
		allItems.remove(selectedItem);
		
		notesDataSource.deleteNote ( selectedItem );
	}

	public void changeSelectedItemTitle(String newTitle)
	{
		if (selectedItem.getType() == Item.Type.NOTE)
		{
			selectedItem.setTitle(newTitle);

			notesDataSource.updateNote(selectedItem);
		}
		//In case of when item is folder we must update all notes which belong to this folder as well
		else
		{
			String previousTitle = selectedItem.getTitle();
			selectedItem.setTitle(newTitle);
			notesDataSource.updateNote(selectedItem);
			
			notesDataSource.updateNote(selectedItem);

			for (int i = 0; i < allItems.size(); i++)
			{
				if (allItems.get(i).getLocationFolder().equals(previousTitle))
				{
					Item note = allItems.get(i);
					note.setLocationFolder(newTitle);
					
					notesDataSource.updateNote(note);
				}
			}
		}
	}

	public void setSelectedItemPriority (int priority) { selectedItem.setPriority(priority); }

	public void openItem (int index, boolean isNote)
	{
		Item itemToOpen = itemsInSelectedFolder.get(index);
		if (itemToOpen.getType() == Item.Type.NOTE)
		{
			selectedItem = itemToOpen;
			isNote = true;
		}
		else
		{
			currentFolder = itemToOpen;
			isNote = false;
		}
	}

	public void updateItems()
	{

	}

	public void sortItems (PreferencesManager.SortType sortType)
	{
		Item.setSortType(sortType);
		Collections.sort(itemsInSelectedFolder);
	}

	public void updateSelectedNote (String updatedNote)
	{
		selectedItem.setNote(updatedNote);
	}

	public ItemInterface getSelectedItem ()
	{
		return selectedItem;
	}

	public void selectItem (int index)
	{
		selectedItemIndex = index;
		selectedItem =  itemsInSelectedFolder.get(index);
		
		if (selectedItem.getType() == Item.Type.FOLDER)
		{
			itemsInSelectedFolder.removeAll(itemsInSelectedFolder);
			for (Item item : allItems)
				if ( item.getLocationFolder ().equals (currentFolder.getTitle() ) )
					itemsInSelectedFolder.add(item);
		}
	}
	
	//Returns false if next note can't be selected
	public boolean selectNextNote ()
	{
		for (int i = selectedItemIndex + 1; i < itemsInSelectedFolder.size(); i++)
		{
			if (selectedItem.getType() == Item.Type.FOLDER || selectedItem.getIsProtected() == true)
				continue;
			else
			{
				selectedItemIndex = i;
				selectedItem = itemsInSelectedFolder.get(selectedItemIndex);
				
				return true;
			}
		}
		return false;
	}
	
	//Returns false when previous note can't be selected
	public boolean selectPreviousNote ()
	{
		for (int i = selectedItemIndex - 1; i >= 0; i--)
		{
			if (selectedItem.getType() == Item.Type.FOLDER || selectedItem.getIsProtected() == true)
				continue;
			else
			{
				selectedItemIndex = i;
				selectedItem = itemsInSelectedFolder.get(selectedItemIndex);
				return true;
			}
		}
		return false;
	}
	
	public void updateSelectedItem (String title, String note)
	{
		selectedItem.setTitle(title);
		selectedItem.setNote(note);
		
		notesDataSource.updateNote(selectedItem);
	}
}
