package presenters;

import interfaces.CompletitionHandler;
import interfaces.DialogActionsListener;
import interfaces.ItemInterface;

import java.util.List;

import logic.ItemsManager;
import logic.Preferences;
import structures.Item;

public class ItemListPresenter
{
	private IPresenterListener mView;
	private ItemsManager mItemsManager = ItemsManager.getInstance ();
	private Preferences preferences = new Preferences ();
	
	public ItemListPresenter (IPresenterListener view)
	{
		mView = view;
		mView.updateItemList ( mItemsManager.getItemsToDisplay () );
		mView.setRowSize(preferences.getRowSize());
		mView.setSortType(preferences.getSortType());
	}
	
	public void selectItem (int index) { mItemsManager.selectItem(index); }

	public List <ItemInterface> getItems ()
	{
		return mItemsManager.getItemsToDisplay ();
	}

	public void onSearch ()
	{
		mView.performSlidingAnimation ( new CompletitionHandler ()
		{
			public void completeAction () { mView.enterSearchMode (); }
		} );
	}

	public void onNewFolder ()
	{
		mView.performSlidingAnimation ( new CompletitionHandler ()
		{
			public void completeAction () { mView.showNewFolderDialog (); }
		} );
	}

	public void onSort (final Preferences.SortType sortType)
	{
		mView.performSlidingAnimation ( new CompletitionHandler ()
		{
			public void completeAction ()
			{
				mItemsManager.sortItems(sortType);
				mView.setSortType(sortType);
				mView.updateItemList ( mItemsManager.getItemsToDisplay () );
				preferences.setSortType(sortType);
			}
		} );
	}

	public void onChangeItemSize (final Preferences.RowSize rowSize)
	{
		mView.performSlidingAnimation ( new CompletitionHandler ()
		{
			public void completeAction ()
			{
				mView.setRowSize(rowSize);
				mView.updateItemList ( mItemsManager.getItemsToDisplay () );
				preferences.setRowSize(rowSize);
			}
		} );
	}
	
	public void onDelete ()
	{
		mView.showConfirmDeleteDialog ( new DialogActionsListener()
		{
			@Override
			public void confirmAction (String message)
			{
				mItemsManager.deleteSelectedItem ();
				mView.updateItemList ( mItemsManager.getItemsToDisplay () );
				mView.showMessage ("Note deleted");
			}
			
			@Override
			public void cancelAction ()
			{
				mView.dissmissDialog ();
			}
		} );
	}

	public void onConfirmDelete ()
	{

	}

	public void onChangeTitle ()
	{
		mView.showChangeTitleDialog ( new DialogActionsListener()
		{
			@Override
			public void confirmAction (String message)
			{
				
			}
			
			@Override
			public void cancelAction () { mView.dissmissDialog (); }
		} );
	}

	public void onSend ( String text )
	{
		mView.dispatchText ( text );
	}

	public void onPassword ()
	{
		mView.showPasswordDialog ();
	}

	public void onPasswordConfirm ()
	{
		mItemsManager.changeItemSecurityStatus ();
		mView.updateItemList ( mItemsManager.getItemsToDisplay () );
	}

	public void onChangePriority ()
	{
		mView.showSetPriorityDialog ();
	}

	public void onSetPriority ( int priority)
	{
		mItemsManager.setSelectedItemPriority ( priority );
	}
	
	public void onItemClick (int index)
	{
		/*
		boolean isNote = false;		//just because it asks for initialization
		mItemsManager.openItem(index, isNote);
		
		if (isNote)
			mView.launchNoteActivity();
		else
			mView.updateItemList(mItemsManager.getItems());
			*/
		
		mItemsManager.selectItem(index);
		
		if (mItemsManager.getSelectedItem().getType() == Item.Type.NOTE)
			mView.launchNoteActivity();
		else
			mView.updateItemList(mItemsManager.getItemsToDisplay());
	}

	public interface IPresenterListener
	{
		public void enterSearchMode ();

		public void showNewFolderDialog ();

		public void updateItemList ( List <ItemInterface> items );

		public void showChangeTitleDialog (DialogActionsListener dialogListener);

		public void dispatchText ( String text );

		public void showPasswordDialog ();

		public void showSetPriorityDialog ();

		public void performSlidingAnimation ( CompletitionHandler listener );
		
		public void showMessage (String message);
		
		public void showConfirmDeleteDialog (DialogActionsListener dialogListener);
		
		public void dissmissDialog ();
		
		public void launchNoteActivity ();
		
		public void setRowSize (Preferences.RowSize rowSize);
		
		public void setSortType (Preferences.SortType sortType);
	}
}
