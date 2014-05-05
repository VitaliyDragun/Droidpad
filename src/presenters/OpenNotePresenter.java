package presenters;

import custom_views.CustomEditText;
import interfaces.DialogActionsListener;
import logic.ItemsManager;

public class OpenNotePresenter extends CreateNotePresenter
{
	private ViewNoteView mView;
	
	private ItemsManager mItemsManager;
	
	public OpenNotePresenter(ViewNoteView view) 
	{
		super (view);
		
		mView = view;
		
		mItemsManager = ItemsManager.getInstance();
		
		mView.setTitle (mItemsManager.getSelectedItem().getTitle());
		mView.setNote(mItemsManager.getSelectedItem().getNote());
	}
	
	public void onEditButtonPressed (boolean isOn) 
	{
		((ViewNoteView)mView).setEditableMode(isOn);
		
		if (isOn)
			mView.showTextMessage("Edit mode ON");
		else
			mView.showTextMessage("Edit mode OFF");
	}
	
	public void onDeleteNote ()
	{
		mView.showDeleteDialog(new DialogActionsListener()
		{	
			@Override
			public void confirmAction(String message)
			{
				mItemsManager.deleteSelectedItem();
				mItemsManager.selectNextNote();
				mView.setTitle(mItemsManager.getSelectedItem().getTitle());
				mView.setNote(mItemsManager.getSelectedItem().getNote());
				mView.showTextMessage("Note was deleted");
			}
			
			@Override
			public void cancelAction()
			{
				mView.dismissDialog();
			}
		});
	}
	
	public void onRightSwipe ()
	{
		if (mItemsManager.selectPreviousNote())
		{
			mItemsManager.selectNextNote();
			
			mView.setTitle(mItemsManager.getSelectedItem().getTitle());
			mView.setNote(mItemsManager.getSelectedItem().getNote());
			mView.showNextNoteImage();
		}
		else
			mView.showStopImage();
	}
	
	public void onLeftSwipe ()
	{
		if (mItemsManager.selectPreviousNote())
		{
			mItemsManager.selectPreviousNote();
			
			mView.setTitle(mItemsManager.getSelectedItem().getTitle());
			mView.setNote(mItemsManager.getSelectedItem().getNote());
			mView.showPreviousNoteImage();
		}
		else
			mView.showStopImage();
	}
	
	public void onBackPressed (CustomEditText sender)
	{
		if (sender == null)
		{
			if (isNoteModified())
			{
				mItemsManager.updateSelectedItem(mView.getTitle(), mView.getNote());
				mView.showTextMessage("The note was updated");
			}
			mView.finish ();
			return;
		}
		
		if (mView.isItTitle(sender))
			mView.setEditTitleMode(false);
		else
		{
			if (isNoteModified())
			{
				mItemsManager.updateSelectedItem(mView.getTitle(), mView.getNote());
				mView.showTextMessage("The note was updated");
			}
			mView.finish ();
		}
	}
	
	private boolean isNoteModified ()
	{
		if ( !mView.getTitle().equals(mItemsManager.getSelectedItem().getTitle()) ||
				!mView.getNote().equals(mItemsManager.getSelectedItem().getNote()) )
			return true;
		else
			return false;
	}
	
	public interface ViewNoteView extends INoteView
	{
		public void setEditableMode (boolean isOn);
		public void showPreviousNoteImage ();
		public void showNextNoteImage ();
		public void showStopImage ();
		public void setNote (String note);
		public void setTitle (String title);
		public void finish ();
	}
}
