package activities;

import presenters.ViewNotePresenter;
import views.CustomEditText;
import vitaliy.dragun.droidpad_2nd_edition.R;
import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewNoteActivity extends CreateNoteActivity
{	
	private ImageButton mImageButtonEditViewSwitch;
	
	private PresenterListener mPresenterListener;
	private ViewNotePresenter mPresenter;
	
	private GestureDetector mGestureDetector;
	private GestureHandler mGestureHandler = new GestureHandler ();
	
	protected ViewNotePresenter getPresenter ()
	{
		mPresenter = new ViewNotePresenter(getPresenterListener());
		return mPresenter;
	}
	
	protected  PresenterListener getPresenterListener ()
	{
		mPresenterListener = new PresenterListener();
		return mPresenterListener;
	}
	
	protected void connectViews ()
	{
		super.connectViews();
		
		mImageButtonEditViewSwitch = (ImageButton) findViewById (R.id.edit_button);
	}
	
	protected void setActionListeners ()
	{
		super.setActionListeners();
		
		mImageButtonEditViewSwitch.setOnClickListener (new View.OnClickListener()
		{ 
			private boolean isEditable = false;
			public void onClick (View v)
			{
				mPresenter.onEditButtonPressed (!isEditable);
				isEditable = isEditable ? false : true;
			}
		});
	}
	
	protected void setupViews ()
	{
		super.setupViews();
		
		mViewGroupTopBar.setVisibility (View.GONE);
		
		mViewGroupTopBar = (ViewGroup) findViewById (R.id.open_note_title_bar);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT,
																			  RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule (RelativeLayout.BELOW, mViewGroupTopBar.getId());
		params.addRule (RelativeLayout.ABOVE, mViewGroupButtomBar.getId());
		
		mCustomEditTextNote.setLayoutParams (params);
		
		mCustomEditTextAddTitle = (CustomEditText) findViewById(R.id.open_note_add_title);
		mCustomEditTextAddTitle.enableLines(false);
		
		mImageButtonDelete = (ImageButton) findViewById(R.id.open_note_clear_button);
		mImageButtonDelete.setOnClickListener(new View.OnClickListener()
		{ @Override public void onClick(View v) { mPresenter.onDeleteNote(); } });
		
		mTextTitle = (TextView) findViewById(R.id.open_note_title);
		
		mTextTitle.setOnClickListener(new View.OnClickListener()
		{ public void onClick(View v) { mPresenter.onTitleClick (); } });
		
		mImageButtonDone = (ImageButton) findViewById(R.id.open_note_title_done_button);
		mImageButtonDone.setOnClickListener(new View.OnClickListener()
		{ @Override public void onClick(View v) { mPresenterListener.setEditTitleMode(false); } });
		
		mGestureDetector = new GestureDetector (mGestureHandler);
		
		//Make sure gestures work
		mCustomEditTextNote.setOnTouchListener(new View.OnTouchListener()
		{ public boolean onTouch(View v, MotionEvent event) { return mGestureDetector.onTouchEvent(event); } });
	}
	
	protected void setupPresenter ()
	{
		mPresenterListener = new PresenterListener();
		mPresenter = new ViewNotePresenter ((ViewNoteActivity.PresenterListener) mPresenterListener);
	}
	
	protected void setupActivity ()
	{
		mCustomEditTextNote.setFocusableInTouchMode(false);
	}
	
	protected class PresenterListener extends CreateNoteActivity.PresenterListener implements ViewNotePresenter.ViewNoteView
	{
		public void setEditableMode (boolean isOn)
		{
			if (isOn)
			{
				mImageButtonEditViewSwitch.setBackgroundColor(Color.parseColor(orangeColor));
				mCustomEditTextNote.setFocusableInTouchMode(true);
				mCustomEditTextNote.setFocusable(true);
			}
			else
			{
				mImageButtonEditViewSwitch.setBackgroundColor(Color.parseColor(blueColor2));
				mCustomEditTextNote.setFocusableInTouchMode(false);
				mCustomEditTextNote.setFocusable(false);
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
			}
		}
		
		public void setEditTitleMode (boolean isOn)
		{
			if (isOn)
			{
				super.setEditTitleMode(isOn);
				mImageButtonEditViewSwitch.setVisibility(View.GONE);
			}
			else
			{
				super.setEditTitleMode(isOn);
				mImageButtonEditViewSwitch.setVisibility(View.VISIBLE);
			}
		}
		
		public void showPreviousNoteImage ()
		{
			
		}
		public void showNextNoteImage ()
		{
			
		}
		public void showStopImage ()
		{
			
		}
		
		public void setNote (String note) { mCustomEditTextNote.setText(note); }
		
		public void setTitle (String title)
		{
			mCustomEditTextAddTitle.setText (title);
			mTextTitle.setText (title);
		}
	}
	
	private class GestureHandler implements OnGestureListener
	{
		public boolean onDown(MotionEvent e) { return false; }

		public boolean onFling(MotionEvent start, MotionEvent finish, float velocityX, float velocityY)
		{

			final ViewConfiguration vc = ViewConfiguration.get (ViewNoteActivity.this);
			final int minSwipeDistance = vc.getScaledTouchSlop ();
			
			ViewNotePresenter presenter = (ViewNotePresenter) mPresenter;

			//Previous Note
			//Second condition ensures that users does not dodge a lot
			if (start.getRawX() - finish.getRawX() < -minSwipeDistance
					&& Math.abs(start.getRawY() - finish.getRawY()) < 3 * minSwipeDistance)
			{
				presenter.onLeftSwipe();
			}
			//Next Note
			else if (start.getRawX() - finish.getRawX() > minSwipeDistance
					&& Math.abs(start.getRawY() - finish.getRawY()) < 3 * minSwipeDistance)
			{
				presenter.onRightSwipe();
			}

			//Must return false, that prevents EditText context dialog to appear
			return false;
		}

		public void onLongPress(MotionEvent e) {}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

		public void onShowPress(MotionEvent e) {}

		public boolean onSingleTapUp(MotionEvent e) { return false; }
	}
}
