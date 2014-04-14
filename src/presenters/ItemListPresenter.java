package presenters;

import java.util.List;

import logic.ItemsManager;
import database.Item;

public class ItemListPresenter
{
	private IPresenterListener mView;
	private ItemsManager mItemsManager = ItemsManager.getInstance ();

	public ItemListPresenter (IPresenterListener view)
	{
		mView = view;
		mView.updateItemList ( mItemsManager.getItems () );
	}
	
	public void selectItem (int index) { mItemsManager.selectNote(index); }

	public List <Item> getItems ()
	{
		return mItemsManager.getItems ();
	}

	public void onSearch ()
	{
		mView.performSlidingAnimation ( new OnAnimationEndListener ()
		{
			public void completeAction ()
			{
				mView.enterSearchMode ();
			}
		} );
	}

	public void onNewFolder ()
	{
		mView.performSlidingAnimation ( new OnAnimationEndListener ()
		{
			public void completeAction ()
			{
				mView.showNewFolderDialog ();
			}
		} );
	}

	public void onSort ()
	{
		mView.performSlidingAnimation ( new OnAnimationEndListener ()
		{
			public void completeAction ()
			{
				mView.updateItemList ( mItemsManager.getItems () );
			}
		} );
	}

	public void onChangeItemSize ()
	{
		mView.performSlidingAnimation ( new OnAnimationEndListener ()
		{
			public void completeAction ()
			{
				mView.updateItemList ( mItemsManager.getItems () );
			}
		} );
	}
	
	public void onDelete (final int itemIndex)
	{
		mView.showConfirmDeleteDialog ( new OnDialogActionListener()
		{
			@Override
			public void confirmAction (String message)
			{
				mItemsManager.deleteItem ( itemIndex );
				mView.updateItemList ( mItemsManager.getItems () );
				mView.showMessage ("Note deleted");
			}
			
			@Override
			public void cancelAction ()
			{
				mView.dissmissDialog ();
			}
		} );
	}

	public void onConfirmDelete ( int itemIndex )
	{

	}

	public void onChangeTitle ( int itemIndex )
	{
		mView.showChangeTitleDialog ( new OnDialogActionListener()
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

	public void onPasswordConfirm ( boolean status, int itemIndex )
	{
		mItemsManager.changeSecureStatus ( status, itemIndex );
		mView.updateItemList ( mItemsManager.getItems () );
	}

	public void onChangePriority ()
	{
		mView.showSetPriorityDialog ();
	}

	public void onSetPriority ( int priority, int itemIndex )
	{
		mItemsManager.setPriority ( priority, itemIndex );
	}

	public interface OnAnimationEndListener
	{
		public void completeAction ();
	}
	
	public interface OnDialogActionListener
	{
		public void confirmAction (String message);
		public void cancelAction ();
	}

	public interface IPresenterListener
	{
		public void enterSearchMode ();

		public void showNewFolderDialog ();

		public void updateItemList ( List <Item> items );

		public void showChangeTitleDialog (OnDialogActionListener dialogListener);

		public void dispatchText ( String text );

		public void showPasswordDialog ();

		public void showSetPriorityDialog ();

		public void performSlidingAnimation ( OnAnimationEndListener listener );
		
		public void showMessage (String message);
		
		public void showConfirmDeleteDialog (OnDialogActionListener dialogListener);
		
		public void dissmissDialog ();
	}
}
