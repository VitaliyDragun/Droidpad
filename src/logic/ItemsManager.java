package logic;

import interfaces.ItemInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import structures.Item;
import structures.Item.Type;
import vitaliy.dragun.droidpad_2nd_edition.MyApplication;
import database.ItemsDatabase;

public class ItemsManager
{
	private final String MAIN_FOLDER = "My Notes";
	
	public static String rootFolderTitle = "My Notes";
	public static String backupFolderTitle = "My Notes BACKUP";
	public static String selectedFolder = rootFolderTitle;

	private List<Item> allItems = new ArrayList<Item>();
	private List<Item> itemsToDisplay = new ArrayList<Item>();
	
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
		notesDataSource = new ItemsDatabase( MyApplication.getAppContext() );
		
		notesDataSource.open();
		
		allItems.addAll( notesDataSource.getAllNotes() );

		itemsToDisplay = allItems;
	}
	
	public List<ItemInterface> getItemsToDisplay ()
	{
		List <ItemInterface> copy = new ArrayList<ItemInterface> ();
		
		for (int i = 0; i < itemsToDisplay.size(); i++)
			copy.add(itemsToDisplay.get(i));
		
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

	public void changeItemSecurityStatus()
	{
		selectedItem.setIsProtected( selectedItem.getIsProtected() ? false : true );
	}

	public void createFolder( String title )
	{
		Item newFolder = new Item();

		newFolder.setTitle(title);
		newFolder.setType(Type.FOLDER);
		newFolder.setLocationFolder(currentFolder.getTitle());
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

	public void deleteSelectedItem()
	{
		itemsToDisplay.remove(selectedItem);
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
		Item itemToOpen = itemsToDisplay.get(index);
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

	public void sortItems (Preferences.SortType sortType)
	{
		Item.setSortType(sortType);
		Collections.sort(itemsToDisplay);
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
		selectedItem =  itemsToDisplay.get(index);
		
		if (selectedItem.getType() == Item.Type.FOLDER)
		{
			itemsToDisplay.removeAll(itemsToDisplay);
			for (Item item : allItems)
				if ( item.getLocationFolder ().equals (currentFolder.getTitle() ) )
					itemsToDisplay.add(item);
		}
	}
	
	public boolean selectNextNote ()
	{
		for (int i = selectedItemIndex + 1; i < itemsToDisplay.size(); i++)
		{
			if (selectedItem.getType() == Item.Type.FOLDER || selectedItem.getIsProtected() == true)
				continue;
			else
			{
				selectedItemIndex = i;
				selectedItem = itemsToDisplay.get(selectedItemIndex);
				return true;
			}
		}
		return false;
	}
	
	public boolean selectPreviousNote ()
	{
		for (int i = selectedItemIndex - 1; i >= 0; i--)
		{
			if (selectedItem.getType() == Item.Type.FOLDER || selectedItem.getIsProtected() == true)
				continue;
			else
			{
				selectedItemIndex = i;
				selectedItem = itemsToDisplay.get(selectedItemIndex);
				return true;
			}
		}
		return false;
	}
}
