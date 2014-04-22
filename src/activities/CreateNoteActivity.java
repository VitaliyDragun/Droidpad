package activities;

import interfaces.DialogActionsListener;
import presenters.NotePresenter;
import presenters.NotePresenter.INoteView;
import views.CustomEditText;
import vitaliy.dragun.droidpad_2nd_edition.Colors;
import vitaliy.dragun.droidpad_2nd_edition.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNoteActivity extends Activity implements Colors
{
	private NotePresenter mPresenter;

	protected CustomEditText mCustomEditTextAddTitle;
	protected TextView mTextTitle;
	protected CustomEditText mCustomEditTextNote;

	protected ViewGroup mViewGroupTopBar;
	protected ViewGroup mViewGroupButtomBar;

	private ImageButton mImageButtonFont;
	private ImageButton mImageButtonFontColor;
	protected ImageButton mImageButtonDelete;
	private ImageButton mImageButtonLines;
	private ImageButton mImageButtonPlus;
	private ImageButton mImageButtonMinus;
	private ImageButton mImageButtonBackground;
	protected ImageButton mImageButtonDone;

	private ViewGroup mViewGroupRootLayout;

	private Toast mToast;
	
	private AlertDialog dialogToDismiss;

	private PresenterListener mPresenterListener;

	protected NotePresenter getPresenter ()
	{
		return new NotePresenter (getPresenterListener());
	}
	
	protected PresenterListener getPresenterListener ()
	{
		mPresenterListener = new PresenterListener();
		return mPresenterListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_note);
		
		connectViews();
		setActionListeners();
		setupViews();
		
		mPresenter = getPresenter();
		
		mCustomEditTextNote.setDelegate (mPresenter);
		mCustomEditTextAddTitle.setDelegate (mPresenter);
	}
	
	protected void connectViews ()
	{
		mViewGroupRootLayout = (ViewGroup)findViewById(R.id.activity_root);
		mViewGroupTopBar = (ViewGroup) findViewById(R.id.create_note_title_bar);
		mViewGroupButtomBar = (ViewGroup) findViewById(R.id.tool_bar);
		mImageButtonDone= (ImageButton)findViewById(R.id.title_done_button);
		mCustomEditTextNote = (CustomEditText)findViewById(R.id.new_note_edittext);
		mTextTitle = (TextView)findViewById(R.id.title);
		mCustomEditTextAddTitle = (CustomEditText)findViewById(R.id.add_title);
		mImageButtonFont = (ImageButton)findViewById(R.id.font_button);
		mImageButtonFontColor = (ImageButton)findViewById(R.id.font_color_button);
		mImageButtonDelete = (ImageButton)findViewById(R.id.clear_button);
		mImageButtonLines = (ImageButton)findViewById(R.id.lines_button);
		mImageButtonPlus = (ImageButton)findViewById(R.id.plus_button);
		mImageButtonMinus = (ImageButton)findViewById(R.id.minus_button);
		mImageButtonBackground = (ImageButton)findViewById(R.id.background_button);
	}
	
	protected void setActionListeners ()
	{
		mImageButtonDone.setOnClickListener(new View.OnClickListener()
		{ public void onClick(View v) { mPresenterListener.setEditTitleMode (false); } });

		mImageButtonFont.setOnClickListener(new View.OnClickListener() 
		{ public void onClick(View v) { mPresenter.onChangeFont(); } });

		mImageButtonFontColor.setOnClickListener(new View.OnClickListener()
		{ public void onClick(View v) { mPresenter.onChangeFontColor(); } });

		mImageButtonLines.setOnClickListener(new View.OnClickListener()
		{ public void onClick(View v) { mPresenter.onToggleLines(); } });

		mImageButtonPlus.setOnClickListener(new View.OnClickListener()
		{ public void onClick(View v) { mPresenter.onIncreaseFont(); } });

		mImageButtonMinus.setOnClickListener(new View.OnClickListener()
		{ public void onClick(View v) { mPresenter.onDecreaseFont(); } });

		mImageButtonBackground.setOnClickListener(new View.OnClickListener()
		{ public void onClick(View v) { mPresenter.onChangeBackgroundColor(); } });
		
		mTextTitle.setOnClickListener(new View.OnClickListener()
		{ public void onClick(View v) { mPresenter.onTitleClick (); } });

		mImageButtonDelete.setOnClickListener(new View.OnClickListener()
		{ public void onClick(View v) {mPresenter.onDeleteNote(); } });
	}
	
	protected void setupViews ()
	{
		mToast = Toast.makeText( this  , "" , Toast.LENGTH_SHORT );

		mViewGroupRootLayout.setBackgroundColor(Color.parseColor("#bebebe"));
		
		mCustomEditTextAddTitle.enableLines(false);

		mCustomEditTextNote.setBackgroundColor(getResources().getColor(android.R.color.transparent));

		//getParent().getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	
	protected void setupActivity ()
	{
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}
	
	public void onResume()
	{
		super.onResume();
		
		setupActivity();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		mPresenter.onToggleFullscreen();

		return false;
	}

	public void onBackPressed(){}

	public void onPause()
	{
		super.onPause();

		mPresenterListener.setEditTitleMode(false);
	}


	protected class PresenterListener implements INoteView
	{
		@Override
		public void setFont (Typeface typeFace, int fontStyle) { mCustomEditTextNote.setTypeface (typeFace, fontStyle); }

		@Override
		public void setFontColor (String color) { mCustomEditTextNote.setTextColor ( Color.parseColor (color) ); }

		@Override
		public void setFontSize (int size) { mCustomEditTextNote.setTextSize (TypedValue.COMPLEX_UNIT_PX, size); }

		@Override
		public void setBackgroundColor (String color) { mViewGroupRootLayout.setBackgroundColor ( Color.parseColor(color) ); }

		@Override
		public void drawLines (boolean drawLines) 
		{
			mCustomEditTextNote.enableLines (drawLines);
			mCustomEditTextNote.invalidate();
		}

		@Override
		public void setFullscreen (boolean fullscreen)
		{
			if (fullscreen)
			{
				mViewGroupTopBar.setVisibility(View.VISIBLE);
				mViewGroupButtomBar.setVisibility(View.VISIBLE);

				mToast.setText("fullscreen mode: off");
				mToast.setDuration(Toast.LENGTH_SHORT);
				mToast.setGravity(Gravity.CENTER, 0, -150);
				mToast.show();
			}
			else
			{
				mViewGroupTopBar.setVisibility(View.GONE);
				mViewGroupButtomBar.setVisibility(View.GONE);

				mToast.setText("fullscreen mode: on");
				mToast.setDuration(Toast.LENGTH_SHORT);
				mToast.setGravity(Gravity.CENTER, 0, -150);
				mToast.show();
			}
		}

		@Override
		public void init ( Typeface fontType,
				int fontStyle,
				String fontColor,
				int fontSize,
				String backgroundColor,
				boolean enableLines,
				boolean fullscreenMode )
		{
			mCustomEditTextNote.setTypeface ( fontType, fontStyle );
			mCustomEditTextNote.setTextSize ( TypedValue.COMPLEX_UNIT_PX, fontSize );
			mCustomEditTextNote.setTextColor ( Color.parseColor (fontColor) );

			mViewGroupRootLayout.setBackgroundColor(Color.parseColor(backgroundColor));

			mCustomEditTextNote.enableLines (enableLines);
			mCustomEditTextNote.invalidate();

			if (fullscreenMode)
			{
				mViewGroupTopBar.setVisibility(View.VISIBLE);
				mViewGroupButtomBar.setVisibility(View.VISIBLE);
			}
			else
			{
				mViewGroupTopBar.setVisibility(View.GONE);
				mViewGroupButtomBar.setVisibility(View.GONE);
			}
		}
		
		@Override
		public MainActivity getParentView () { return (MainActivity) CreateNoteActivity.this.getParent(); }
		
		@Override
		public boolean isItTitle (CustomEditText viewToCheck)
		{
			if (viewToCheck == mCustomEditTextAddTitle)
				return true;
			else
				return false;
		}
		
		public void setEditTitleMode(boolean isOn)
		{
			if (isOn)
			{
				mTextTitle.setVisibility(View.GONE);

				mCustomEditTextAddTitle.setVisibility(View.VISIBLE);
				mCustomEditTextAddTitle.setFocusableInTouchMode(true);
				mCustomEditTextAddTitle.setFocusable(true);
				mCustomEditTextAddTitle.requestFocus();

				mImageButtonDelete.setVisibility(View.GONE);

				mCustomEditTextNote.setEnabled(false);

				mImageButtonDone.setVisibility(View.VISIBLE);
			}
			else
			{
				mCustomEditTextAddTitle.setVisibility(View.INVISIBLE);

				mImageButtonDone.setVisibility(View.INVISIBLE);

				mTextTitle.setVisibility(View.VISIBLE);
				if(!mCustomEditTextAddTitle.getText().toString().isEmpty())
					mTextTitle.setText(mCustomEditTextAddTitle.getText().toString());
				else
					mTextTitle.setText("tap to add title".toString());

				mImageButtonDelete.setVisibility(View.VISIBLE);

				mCustomEditTextNote.setEnabled(true);
			}
		}
		
		public String getTitle ()
		{
			return "Some title";
		}
		
		public String getNote ()
		{
			return "Note";
		}
		
		public void clearView ()
		{
			mCustomEditTextNote.setText("".toString());
			mCustomEditTextAddTitle.setText("".toString());
			mTextTitle.setText("tap to add title".toString());
			
			InputMethodManager imm = (InputMethodManager) CreateNoteActivity.this
					.getSystemService(Service.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mCustomEditTextNote.getWindowToken(), 0);
		}
		
		public void showDeleteDialog (final DialogActionsListener dialogListener)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
			builder.setIcon(R.drawable.icon_pack_delete);
			builder.setTitle("Delete note?").setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{ public void onClick(DialogInterface dialog, int whichButton) { dialogListener.confirmAction(null); } });

			builder.setNegativeButton("No", new DialogInterface.OnClickListener()
			{ public void onClick(DialogInterface dialog, int whichButton) {dialogListener.cancelAction(); } });

			AlertDialog dialog = builder.create();
			dialog.show();
			
			dialogToDismiss = dialog;
		}
		
		public void dismissDialog () { dialogToDismiss.dismiss();}
		
		public void showTextMessage (String message)
		{
			mToast.setText(message);
			mToast.setDuration(Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
			mToast.show();
		}
	}
}
