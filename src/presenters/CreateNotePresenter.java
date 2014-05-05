package presenters;

import custom_views.CustomEditText;
import custom_views.CustomEditText.CustomEditTextDelegate;
import interfaces.DialogActionsListener;
import logic.ItemsManager;
import logic.NoteAppearanceManager;
import activities.MainActivity;
import android.graphics.Typeface;

public class CreateNotePresenter implements CustomEditTextDelegate
{
	private INoteView mView;
	private MainActivity mMainView;

	private NoteAppearanceManager mNoteAppearance = new NoteAppearanceManager ();
	private ItemsManager mItemsManager = ItemsManager.getInstance();

	public CreateNotePresenter (INoteView view)
	{
		mView = view;
		mMainView = mView.getParentView();

		mView.init ( mNoteAppearance.getFontType(),
				mNoteAppearance.getFontStyle(),
				mNoteAppearance.getFontColor(),
				mNoteAppearance.getFontSize(),
				mNoteAppearance.getBackgroundColor(),
				mNoteAppearance.isLinesEnabled(),
				mNoteAppearance.isFullscreenMode() );
	}
	
	public void onEditButtonPressed (boolean isOn) {}

	public void onChangeFont ()
	{
		mNoteAppearance.changeFontType ();

		Typeface newFontType = mNoteAppearance.getFontType ();
		int newFontStyle = mNoteAppearance.getFontStyle ();

		mView.setFont (newFontType, newFontStyle);
	}

	public void onChangeFontColor () { mView.setFontColor ( mNoteAppearance.toggleFontColor () ); }

	public void onIncreaseFont () { mView.setFontSize ( mNoteAppearance.increaseFontSize() ); }

	public void onDecreaseFont () { mView.setFontSize ( mNoteAppearance.decreaseFontSize() ); }

	public void onChangeBackgroundColor () { mView.setBackgroundColor( mNoteAppearance.toggleBackgroundColor() ); }

	public void onToggleLines () { mView.drawLines ( mNoteAppearance.toggleLines() ); }

	public void onToggleFullscreen () { mView.setFullscreen ( mNoteAppearance.toggleFullscreenMode() ); }

	public void onDeleteNote ()
	{
		mView.showDeleteDialog(new DialogActionsListener()
		{	
			@Override
			public void confirmAction(String message)
			{
				mView.clearView();
				mMainView.setTabBarVisible(true);
				mMainView.chooseTab(1);
			}
			
			@Override
			public void cancelAction()
			{
				mView.dismissDialog();
			}
		});
	}

	public boolean onCreateNote (String title, String note)
	{
		return mItemsManager.createNote(title, note);
	}
	
	public void onTitleClick () { mView.setEditTitleMode(true); }
	/***
	 * CustomEditText Delegate Method
	 */
	@Override
	public void onBackPressed (CustomEditText sender)
	{
		if ( !mView.isItTitle(sender) )
		{
			if ( !mView.getTitle().equals("") || !mView.getNote().equals(""))
				mItemsManager.createNote(mView.getTitle(), mView.getNote());
			
			mMainView.setTabBarVisible(true);
			mMainView.chooseTab(1);
		}
		else 
			mView.setEditTitleMode(false);
	}

	public interface INoteView
	{
		public void init ( Typeface fontType,
						   int fontStyle,
						   String FontColor,
						   int fontSize,
						   String backgroundColor,
						   boolean enableLines,
						   boolean fullscreenMode );

		public void setFont (Typeface typeFace, int fontStyle);
		public void setFontColor (String color);
		public void setFontSize (int size);
		public void setBackgroundColor (String color);
		public void drawLines (boolean drawLines);
		public void setFullscreen (boolean fullscreen);
		public MainActivity getParentView ();
		public boolean isItTitle (CustomEditText sender);
		public void setEditTitleMode(boolean isOn);
		public String getTitle ();
		public String getNote ();
		public void clearView ();
		public void showDeleteDialog (DialogActionsListener dialogListener);
		public void dismissDialog ();
		public void showTextMessage (String message);
	}
}
