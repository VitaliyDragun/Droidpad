package activities;

import presenters.NotePresenter;
import presenters.NotePresenter.INoteView;
import views.CustomEditText;
import views.CustomEditText.CustomEditTextDelegate;
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
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class NoteActivity extends Activity implements Colors
{
    private NotePresenter mPresenter;

    private CustomEditText addTitle;
    private TextView title;
    private CustomEditText noteText;
    
    private ViewGroup topBar;
    private ViewGroup buttomBar;

    private ImageButton titleDoneButton;
    private ImageButton fontButton;
    private ImageButton fontColorButton;
    private ImageButton deleteButton;
    private ImageButton linesButton;
    private ImageButton plusButton;
    private ImageButton minusButton;
    private ImageButton backgroundButton;
    private ImageButton doneButton;

    private ViewGroup rootLayout;

    private GestureDetector gesture;

    private Toast toast;

    private CustomEditTextDelegate mEditTextNoteDelegate = new EditTextNoteDelegate();

    private CustomEditTextDelegate mEditTextTitleDelegate = new EditTextTitleDelegate();

    private GestureHandler gestureHandler = new GestureHandler ();

    private PresenterListener presenterListener = new PresenterListener ();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_note);

	connectAndSetupViews();

	mPresenter = new NotePresenter (presenterListener);
    }

    @SuppressLint("ShowToast") @SuppressWarnings("deprecation")
    private void connectAndSetupViews ()
    {
	rootLayout = (ViewGroup)findViewById(R.id.activity_root);
	topBar = (ViewGroup) findViewById(R.id.title_bar);
	buttomBar = (ViewGroup) findViewById(R.id.tool_bar);
	doneButton= (ImageButton)findViewById(R.id.title_done_button);
	noteText = (CustomEditText)findViewById(R.id.new_note_edittext);
	title = (TextView)findViewById(R.id.title);
	addTitle = (CustomEditText)findViewById(R.id.add_title);
	fontButton = (ImageButton)findViewById(R.id.font_button);
	fontColorButton = (ImageButton)findViewById(R.id.font_color_button);
	deleteButton = (ImageButton)findViewById(R.id.clear_button);
	linesButton = (ImageButton)findViewById(R.id.lines_button);
	plusButton = (ImageButton)findViewById(R.id.plus_button);
	minusButton = (ImageButton)findViewById(R.id.minus_button);
	backgroundButton = (ImageButton)findViewById(R.id.background_button);

	doneButton.setOnClickListener(new View.OnClickListener()
	{ public void onClick(View v) { titleEditingIsDone(); } });

	fontButton.setOnClickListener(new View.OnClickListener() 
	{ public void onClick(View v) { mPresenter.onChangeFont(); } });

	fontColorButton.setOnClickListener(new View.OnClickListener()
	{ public void onClick(View v) { mPresenter.onChangeFontColor(); } });

	linesButton.setOnClickListener(new View.OnClickListener()
	{ public void onClick(View v) { mPresenter.onToggleLines(); } });

	plusButton.setOnClickListener(new View.OnClickListener()
	{ public void onClick(View v) { mPresenter.onIncreaseFont(); } });

	minusButton.setOnClickListener(new View.OnClickListener()
	{ public void onClick(View v) { mPresenter.onDecreaseFont(); } });

	backgroundButton.setOnClickListener(new View.OnClickListener()
	{ public void onClick(View v) { mPresenter.onChangeBackgroundColor(); } });

	deleteButton.setOnClickListener(new OnDeleteButtonListener() );

	noteText.setDelegate (mEditTextNoteDelegate);

	toast = Toast.makeText( this  , "" , Toast.LENGTH_SHORT );

	rootLayout.setBackgroundColor(Color.parseColor("#bebebe"));

	gesture = new GestureDetector (gestureHandler);

	addTitle.setDelegate (mEditTextTitleDelegate);

	//Make sure gestures work
	noteText.setOnTouchListener(new View.OnTouchListener()
	{ public boolean onTouch(View v, MotionEvent event) { return gesture.onTouchEvent(event); } });

	noteText.setBackgroundColor(getResources().getColor(android.R.color.transparent));

	getParent().getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void onResume()
    {
	super.onResume();

	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
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

	titleEditingIsDone();
    }
    
    public void onTitleClick(View v)
    {
	title.setVisibility(View.GONE);

	addTitle = (CustomEditText)findViewById(R.id.add_title);
	addTitle.setVisibility(View.VISIBLE);
	addTitle.setFocusableInTouchMode(true);
	addTitle.setFocusable(true);
	addTitle.requestFocus();

	deleteButton.setVisibility(View.GONE);

	noteText.setEnabled(false);

	doneButton.setVisibility(View.VISIBLE);
    }

    private void titleEditingIsDone()
    {
	addTitle.setVisibility(View.INVISIBLE);

	doneButton.setVisibility(View.INVISIBLE);

	title.setVisibility(View.VISIBLE);
	if(!addTitle.getText().toString().isEmpty())
	    title.setText(addTitle.getText().toString());
	else
	    title.setText("tap to add title".toString());

	deleteButton.setVisibility(View.VISIBLE);

	noteText.setEnabled(true);
    }
    
    private class OnDeleteButtonListener implements OnClickListener
    {
	@Override
	public void onClick(View v)
	{
	    final EditText noteText = (EditText)NoteActivity.this.findViewById(R.id.new_note_edittext);
		final TextView title = (TextView)NoteActivity.this.findViewById(R.id.title);
		final EditText titleEditText = (EditText)NoteActivity.this.findViewById(R.id.add_title);

		AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
		builder.setIcon(R.drawable.icon_pack_delete);
		builder.setTitle("Delete note?").setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{	
		    public void onClick(DialogInterface dialog, int whichButton){

			noteText.setText("".toString());

			titleEditText.setText("".toString());

			title.setText("tap to add title".toString());

			InputMethodManager imm = (InputMethodManager) NoteActivity.this
				.getSystemService(Service.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(noteText.getWindowToken(), 0);
		    }

		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener(){

		    public void onClick(DialogInterface dialog, int whichButton){}

		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
    }

    private class EditTextNoteDelegate implements CustomEditTextDelegate
    {
	@Override
	public void backPressed() 
	{
	    if ( mPresenter.onCreateNote ( addTitle.getText().toString(), noteText.getText().toString() ) )
	    {
		toast.setText("Saved...");
		toast.show();
	    }
	}
    }

    private class EditTextTitleDelegate implements CustomEditTextDelegate
    {
	@Override
	public void backPressed()
	{
	    titleEditingIsDone();
	}	
    }

    private class GestureHandler implements OnGestureListener
    {
	public boolean onDown(MotionEvent e) { return false; }

	public boolean onFling(MotionEvent start, MotionEvent finish, float velocityX, float velocityY)
	{

	    final ViewConfiguration vc = ViewConfiguration.get (NoteActivity.this);
	    final int minSwipeDistance = vc.getScaledTouchSlop ();

	    //Diagonal swipe downwards detected
	    //Second condition ensures that users does not dodge a lot
	    if(finish.getRawY() - start.getRawY() > 3*minSwipeDistance && finish.getRawX() - start.getRawX() > 3*minSwipeDistance){

		mPresenter.onToggleFullscreen();
	    }

	    //Diagonal swipe upwards detected
	    //Second condition ensures that users does not dodge a lot
	    if(start.getRawY() - finish.getRawY() > 3*minSwipeDistance && start.getRawX() - finish.getRawX() > 3*minSwipeDistance){

		mPresenter.onToggleFullscreen();
	    }

	    //Must return false, that prevents EditText context dialog to appear

	    return false;
	}

	public void onLongPress(MotionEvent e) {}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

	public void onShowPress(MotionEvent e) {}

	public boolean onSingleTapUp(MotionEvent e) { return false; }
    }

    private class PresenterListener implements INoteView
    {
	@Override
	public void setFont (Typeface typeFace, int fontStyle) { noteText.setTypeface (typeFace, fontStyle); }

	@Override
	public void setFontColor (String color) { noteText.setTextColor ( Color.parseColor (color) ); }

	@Override
	public void setFontSize (int size) { noteText.setTextSize (TypedValue.COMPLEX_UNIT_PX, size); }

	@Override
	public void setBackgroundColor (String color) { rootLayout.setBackgroundColor ( Color.parseColor(color) ); }

	@Override
	public void drawLines (boolean drawLines) 
	{
	    noteText.enableLines (drawLines);
	    noteText.invalidate();
	}

	@Override
	public void setFullscreen (boolean fullscreen)
	{
	    if (fullscreen)
	    {
		topBar.setVisibility(View.VISIBLE);
		buttomBar.setVisibility(View.VISIBLE);

		toast.setText("fullscreen mode: off");
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, -150);
		toast.show();
	    }
	    else
	    {
		topBar.setVisibility(View.GONE);
		buttomBar.setVisibility(View.GONE);

		toast.setText("fullscreen mode: on");
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, -150);
		toast.show();
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
	    noteText.setTypeface ( fontType, fontStyle );
	    noteText.setTextSize ( TypedValue.COMPLEX_UNIT_PX, fontSize );
	    noteText.setTextColor ( Color.parseColor (fontColor) );

	    rootLayout.setBackgroundColor(Color.parseColor(backgroundColor));
	   
	    noteText.enableLines (enableLines);
	    noteText.invalidate();

	    if (fullscreenMode)
	    {
		topBar.setVisibility(View.VISIBLE);
		buttomBar.setVisibility(View.VISIBLE);
	    }
	    else
	    {
		topBar.setVisibility(View.GONE);
		buttomBar.setVisibility(View.GONE);
	    }
	}
    }
}
