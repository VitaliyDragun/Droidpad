package presenters;

import interfaces.CompletitionHandler;
import interfaces.DialogActionsListener;
import interfaces.DialogWithEditTextListener;
import interfaces.ItemInterface;

import java.util.List;

import logic.Item;
import logic.ItemsManager;
import logic.PreferencesManager;

public class ItemsPresenter
{
	private IPresenterListener mView;
	private ItemsManager mItemsManager;
	private PreferencesManager mPreferencesManager;
	
	public ItemsPresenter (IPresenterListener view)
	{
		mView = view;
		
		mItemsManager = ItemsManager.getInstance ();
		mPreferencesManager = PreferencesManager.getInstance();
		
		mView.updateItemList ( mItemsManager.getItemsInSelectedFolder () );
		mView.markRowSize(mPreferencesManager.getRowSize());
		mView.markSortMode(mPreferencesManager.getSortType());
	}
	
	public void selectItem (int index) { mItemsManager.selectItem(index); }

	public List <ItemInterface> getItems ()
	{
		return mItemsManager.getItemsInSelectedFolder ();
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
			public void completeAction ()
			{
				mView.showNewFolderDialog (new DialogWithEditTextListener()
				{

					@Override
					public void confirmAction(String message)
					{
						mItemsManager.createFolder(message);
						
						mView.updateItemList(mItemsManager.getItemsInSelectedFolder());
					}

					@Override
					public void cancelAction()
					{
						mView.dissmissDialog();
					}

					@Override
					public boolean textChangedAction(String text)
					{
						if (mItemsManager.isTitleUnique(text))
							return true;
						else
							return false;
					}
				}); 
			}
		} );
	}

	public void onSort (final PreferencesManager.SortType sortType)
	{
		mView.performSlidingAnimation ( new CompletitionHandler ()
		{
			public void completeAction ()
			{
				mItemsManager.sortItems(sortType);
				mView.markSortMode(sortType);
				mView.updateItemList ( mItemsManager.getItemsInSelectedFolder () );
				mPreferencesManager.setSortType(sortType);
			}
		} );
	}

	public void onChangeItemSize (final PreferencesManager.RowSize rowSize)
	{
		mView.performSlidingAnimation ( new CompletitionHandler ()
		{
			public void completeAction ()
			{
				mView.markRowSize(rowSize);
				mView.updateItemList ( mItemsManager.getItemsInSelectedFolder () );
				mPreferencesManager.setRowSize(rowSize);
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
				mView.updateItemList ( mItemsManager.getItemsInSelectedFolder () );
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
		mView.updateItemList ( mItemsManager.getItemsInSelectedFolder () );
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
		mItemsManager.selectItem(index);
		
		if (mItemsManager.getSelectedItem().getType() == Item.Type.NOTE)
			mView.launchNoteActivity();
		else
			mView.updateItemList(mItemsManager.getItemsInSelectedFolder());
	}

	public interface IPresenterListener
	{
		public void enterSearchMode ();

		public void showNewFolderDialog (final DialogWithEditTextListener dialogListener);

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
		
		public void markRowSize (PreferencesManager.RowSize rowSize);
		
		public void markSortMode (PreferencesManager.SortType sortType);
	}
}
