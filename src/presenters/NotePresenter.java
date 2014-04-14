package presenters;

import logic.ItemsManager;
import logic.NoteAppearance;
import android.graphics.Typeface;

public class NotePresenter
{
    private INoteView mView;
    
    private NoteAppearance mNoteAppearance = new NoteAppearance ();
    private ItemsManager mItemsManager = ItemsManager.getInstance();
    
    public NotePresenter (INoteView view)
    {
	mView = view;
	
	mView.init ( mNoteAppearance.getFontType(),
		mNoteAppearance.getFontStyle(),
		mNoteAppearance.getFontColor(),
		mNoteAppearance.getFontSize(),
		mNoteAppearance.getBackgroundColor(),
		mNoteAppearance.isLinesEnabled(),
		mNoteAppearance.isFullscreenMode() );
    }
    
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
    
    public void onDeleteNote () {}
    
    public boolean onCreateNote (String title, String note)
    {
	return mItemsManager.createNote(title, note);
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
    }
}
