package activities;

import interfaces.CompletitionHandler;
import interfaces.DialogActionsListener;
import interfaces.DialogWithEditTextListener;
import interfaces.ItemInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import logic.Item;
import logic.PreferencesManager;
import presenters.ItemsPresenter;
import vitaliy.dragun.droidpad.Colors;
import vitaliy.dragun.droidpad_2nd_edition.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemsActivity extends ListActivity implements Colors
{
	private ItemsPresenter mPresenter;

	private PresenterListener presenterListener = new PresenterListener ();

	private MenuAnimationHandler menuAnimationHandler;

	private ListView mListItems;

	private TextView mTextViewTitle;

	private InputMethodManager mInputMethodManager;

	private NoteAdapter noteAdapter;
	private int pressedItemIndex = -1;

	public List <ItemInterface> items = new ArrayList <ItemInterface> ();

	private ViewGroup mSearchBar;

	private boolean innerFolder = false;

	private boolean animationIsInProgress = false;

	// Remember scroll positions in root folder when user enters inner folder
	// to recreate it when user leave inner folder
	private int index = -1;
	private int top = -1;

	// Used to lock down slide out menu
	private boolean backButtonIsBlocked = true;

	// Newly added item index in items
	private int scrollToNewItemIndex = -1;

	private TextView listActivityTitle;

	// Is used to fix bug when in search mode users presses back button
	// and if the keyboard is not shown the slide out menu appears
	private boolean searchModeWasJustDisabled = false;

	ImageButton exitSearchButton;
	EditText mEditTextSearch;

	private Toast mToast;
	
	private Dialog dialogToDismiss;
	
	private SlideoutMenuHandler slideoutMenuHandler = new SlideoutMenuHandler();
	
	private PreferencesManager.RowSize rowSize;

	private void connectAndSetupViews ()
	{
		mEditTextSearch = (EditText) findViewById ( R.id.edittext_search );
		listActivityTitle = (TextView) findViewById ( R.id.text_title );
		mSearchBar = (ViewGroup) findViewById ( R.id.layout_search_bar );
		exitSearchButton = (ImageButton) findViewById ( R.id.button_exit_search );
		mEditTextSearch = (EditText) findViewById ( R.id.edittext_search );
		
		slideoutMenuHandler.setupSlideoutMenu();

		mListItems = (ListView) findViewById ( android.R.id.list );
		noteAdapter = new NoteAdapter ();
		setListAdapter ( noteAdapter );
	}

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );

		setContentView ( R.layout.activity_items );

		mInputMethodManager = (InputMethodManager) getSystemService ( Context.INPUT_METHOD_SERVICE );
		
		mToast = Toast.makeText ( this, null, Toast.LENGTH_SHORT );//No, I didn't
		mToast.setGravity ( Gravity.CENTER, 0, 0 );
		mToast.setDuration(Toast.LENGTH_LONG);

		connectAndSetupViews ();

		mPresenter = new ItemsPresenter ( presenterListener );

		getParent ().getWindow ().setSoftInputMode ( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );

		menuAnimationHandler = new MenuAnimationHandler ();

		mListItems.setOnItemClickListener ( new ItemsListOnItemClickListener () );

		mListItems.setOnItemLongClickListener ( new ItemsListOnItemLongClickListener () );

		exitSearchButton.setOnClickListener ( new View.OnClickListener ()
		{
			@Override
			public void onClick ( View v )
			{
				exitSearchMode ();
			}
		} );

		mEditTextSearch.addTextChangedListener ( new TextWatcher ()
		{
			@Override
			public void afterTextChanged ( Editable s )
			{
			}

			@Override
			public void beforeTextChanged ( CharSequence s, int start, int count, int after )
			{
			}

			@Override
			public void onTextChanged ( CharSequence s, int start, int before, int count )
			{
			}
		} );
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu _menu )
	{
		if (!innerFolder)
			menuAnimationHandler.performSlidingAnimation ( null );

		return false;
	}

	public void onBackPressed ()
	{
		if (!backButtonIsBlocked)
		{
			if (innerFolder)
			{
				mListItems.setSelectionFromTop ( index, top );

				innerFolder = false;

				hideShowTextInCenter ();

				return;
			}
			else if (!searchModeWasJustDisabled)
				menuAnimationHandler.performSlidingAnimation ( null );
			else
				searchModeWasJustDisabled = false;
		}
	}

	private void hideShowTextInCenter ()
	{
		if (items.isEmpty ())
		{
			((TextView) findViewById ( R.id.text_when_folder_is_empty )).setVisibility ( View.VISIBLE );

			if (innerFolder)
				((TextView) findViewById ( R.id.text_when_folder_is_empty )).setText ( "Press plus tab to add a new note in this folder, press back button to go back to main folder" );
			else
				((TextView) findViewById ( R.id.text_when_folder_is_empty )).setText ( "Press plus tab to create a new note, press back button to open menu, press power tab to exit" );
		}
		else
			((TextView) findViewById ( R.id.text_when_folder_is_empty )).setVisibility ( View.GONE );
	}

	public void onResume ()
	{
		super.onResume ();
		
		items = mPresenter.getItems ();

		backButtonIsBlocked = true;
		final Handler handler = new Handler ();
		handler.postDelayed ( new Runnable ()
		{
			@Override
			public void run ()
			{
				backButtonIsBlocked = false;
			}
		}, 250 );

		hideShowTextInCenter ();
	}

	public void onDestroy ()
	{
		super.onDestroy ();
	}

	@Override
	public void onConfigurationChanged ( Configuration newConfig )
	{
		super.onConfigurationChanged ( newConfig );
	}

	private void exitSearchMode ()
	{
		mSearchBar.setVisibility ( View.GONE );

		mInputMethodManager.hideSoftInputFromWindow ( mEditTextSearch.getWindowToken (), 0 );

		mEditTextSearch.setText ( "".toString () );

		mListItems.setScrollContainer ( false );
	}

	private class PresenterListener implements ItemsPresenter.IPresenterListener
	{
		@Override
		public void enterSearchMode ()
		{
			mSearchBar.setVisibility ( View.VISIBLE );

			mEditTextSearch.requestFocus ();

			mInputMethodManager.toggleSoftInput ( InputMethodManager.SHOW_FORCED, 0 );

			mListItems.setScrollContainer ( true );

			listActivityTitle.setText ( "Search".toString () );
		}

		@Override
		public void showNewFolderDialog (final DialogWithEditTextListener dialogListener)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder ( ItemsActivity.this );
			builder.setTitle ( "Add new folder".toString () );

			LayoutInflater inflater = getLayoutInflater ();
			final View dialogView = inflater.inflate ( R.layout.dialog_change_title, null );
			builder.setView ( dialogView );
			
			final EditText editTextFolderName = (EditText) dialogView.findViewById ( R.id.rename_text_field );

			builder.setPositiveButton ( "OK".toString (), new DialogInterface.OnClickListener ()
			{
				@Override
				public void onClick ( DialogInterface dialog, int which )
				{
					dialogListener.confirmAction(editTextFolderName.getText().toString());
				}
			} );
			builder.setNegativeButton ( "Cancel", new DialogInterface.OnClickListener ()
			{
				@Override public void onClick ( DialogInterface dialog, int which ) { dialogListener.cancelAction(); }
			} );

			final AlertDialog newFolderDialog = builder.create ();

			newFolderDialog.show ();
			
			dialogToDismiss = newFolderDialog;
		
			editTextFolderName.addTextChangedListener ( new TextWatcher ()
			{
				@Override public void afterTextChanged ( Editable arg0 ) {}
				@Override public void beforeTextChanged ( CharSequence arg0, int arg1, int arg2, int arg3 ) {}

				@Override
				public void onTextChanged ( CharSequence arg0, int arg1, int arg2, int arg3 )
				{
					if (dialogListener.textChangedAction(editTextFolderName.getText().toString()))
						newFolderDialog.getButton ( AlertDialog.BUTTON_POSITIVE ).setEnabled ( true );
					else
						newFolderDialog.getButton ( AlertDialog.BUTTON_POSITIVE ).setEnabled ( false );
				}
			});

			newFolderDialog.getButton ( AlertDialog.BUTTON_POSITIVE ).setEnabled ( false );

			mInputMethodManager.toggleSoftInput ( InputMethodManager.SHOW_FORCED, 0 );
		}

		@Override
		public void showChangeTitleDialog (final DialogActionsListener dialogListener)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder ( ItemsActivity.this );

			builder.setIcon ( R.drawable.bookmark_small );
			builder.setTitle ( "Change mTextViewTitle".toString () );

			LayoutInflater inflater = getLayoutInflater ();
			final View myView = inflater.inflate ( R.layout.dialog_change_title, null );
			builder.setView ( myView );

			final EditText editText = (EditText) myView.findViewById ( R.id.rename_text_field );
			editText.setText ( items.get ( pressedItemIndex ).getTitle() );

			editText.selectAll ();

			builder.setPositiveButton ( R.string.dialog_ok, new DialogInterface.OnClickListener ()
			{
				@Override
				public void onClick ( DialogInterface dialog, int which )
				{ dialogListener.confirmAction ( editText.getText ().toString () ); }
			} );

			builder.setNegativeButton ( R.string.dialog_cancel, new DialogInterface.OnClickListener ()
			{
				@Override
				public void onClick ( DialogInterface dialog, int which )
				{ dialogListener.cancelAction (); }
			} );

			final AlertDialog changeTitleDialog = builder.create ();

			editText.addTextChangedListener ( new TextWatcher ()
			{
				public void afterTextChanged ( Editable arg0 )
				{
				}

				public void beforeTextChanged ( CharSequence arg0, int arg1, int arg2, int arg3 )
				{
				}

				public void onTextChanged ( CharSequence arg0, int arg1, int arg2, int arg3 )
				{
				}
			} );

			changeTitleDialog.show ();

			changeTitleDialog.getButton ( AlertDialog.BUTTON_POSITIVE ).setEnabled ( true );

			mInputMethodManager.toggleSoftInput ( InputMethodManager.SHOW_FORCED, 0 );
			
			dialogToDismiss = changeTitleDialog;
		}

		@Override
		public void dispatchText ( String text )
		{
			final Intent intent = new Intent ( Intent.ACTION_SEND );
			intent.setType ( "text/plain" );
			intent.putExtra ( Intent.EXTRA_TEXT, text );

			try
			{
				startActivity ( Intent.createChooser ( intent, "Select an action" ) );
			}
			catch (android.content.ActivityNotFoundException ex)
			{
				// (handle error)
			}
		}

		@Override
		public void updateItemList ( List <ItemInterface> upToDateItems )
		{
			items.clear ();
			items.addAll ( upToDateItems );
			noteAdapter.notifyDataSetChanged ();
		}

		@Override
		public void showPasswordDialog ()
		{
			AlertDialog.Builder builder = new AlertDialog.Builder ( ItemsActivity.this );

			LayoutInflater inflater = getLayoutInflater ();
			final View view = inflater.inflate ( R.layout.dialog_enter_password, null );

			builder.setTitle ( "Set password".toString () );
			builder.setView ( view );
			builder.setPositiveButton ( "OK".toString (), new DialogInterface.OnClickListener ()
			{
				@Override
				public void onClick ( DialogInterface dialog, int which )
				{
					String password = ((EditText) view.findViewById ( R.id.enter_password_edittext )).getText ().toString ();

					/*
					 * if (preferences.getString("Password",
					 * "1111").equals(password)) { toast =
					 * Toast.makeText(ItemListActivity.this,
					 * "Note is now locked".toString(), Toast.LENGTH_SHORT);
					 * toast.setGravity(Gravity.CENTER, 0, 0); toast.show(); }
					 * else { toast = Toast.makeText(ItemListActivity.this,
					 * "Wrong password".toString(), Toast.LENGTH_SHORT);
					 * toast.setGravity(Gravity.CENTER, 0, 0); toast.show(); }
					 */
				}
			} );
			builder.setNegativeButton ( "Cancel".toString (), new DialogInterface.OnClickListener ()
			{
				@Override
				public void onClick ( DialogInterface dialog, int which )
				{
				}
			} );

			AlertDialog dialog = builder.create ();
			dialog.show ();

			mInputMethodManager.toggleSoftInput ( InputMethodManager.SHOW_FORCED, 0 );
		}

		@Override
		public void showSetPriorityDialog ()
		{
			LayoutInflater inflater = getLayoutInflater ();
			View listView = inflater.inflate ( R.layout.dialog_change_priority, null );

			AlertDialog.Builder builder = new AlertDialog.Builder ( ItemsActivity.this );
			builder.setView ( listView );
			builder.setNegativeButton ( "Cancel".toString (), new DialogInterface.OnClickListener ()
			{
				@Override
				public void onClick ( DialogInterface dialog, int which )
				{
				}
			} );

			final AlertDialog dialog = builder.create ();
			dialog.show ();

			ViewGroup l = (ViewGroup) listView.findViewById ( R.id.layout_high_priority );

			l.setOnClickListener ( new OnClickListener ()
			{
				public void onClick ( View v )
				{
					dialog.dismiss ();
				}
			} );
		}

		@Override
		public void performSlidingAnimation ( CompletitionHandler listener )
		{
			menuAnimationHandler.performSlidingAnimation ( listener );
		}

		@Override
		public void showMessage ( String message )
		{
			mToast.setText ( message );
			mToast.show ();
		}

		@Override
		public void showConfirmDeleteDialog (final DialogActionsListener dialogListener)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder ( ItemsActivity.this );
			builder.setIcon ( R.drawable.cross_medium );

			if (items.get ( pressedItemIndex ).getType() == Item.Type.FOLDER)
				builder.setTitle ( "Delete folder and all its content?" );
			else
				builder.setTitle ( "Delete note?" );

			builder.setPositiveButton ( R.string.dialog_yes, new DialogInterface.OnClickListener ()
			{
				public void onClick ( DialogInterface dialog, int whichButton )
				{ dialogListener.confirmAction (null); }
			} );

			builder.setNegativeButton ( R.string.dialog_cancel, new DialogInterface.OnClickListener ()
			{
				public void onClick ( DialogInterface dialog, int whichButton )
				{ dialogListener.cancelAction (); }
			} );

			AlertDialog dialog = builder.create ();
			dialog.show ();
			
			dialogToDismiss = dialog;
		}

		@Override
		public void dissmissDialog () { dialogToDismiss.dismiss (); }
		
		@Override
		public void launchNoteActivity ()
		{
		    Intent intent = new Intent(ItemsActivity.this, OpenNoteActivity.class);
		    startActivity(intent);
		}
		
		@Override
		public void markRowSize (PreferencesManager.RowSize rowSize)
		{
			slideoutMenuHandler.markRowSize(rowSize);
			
			ItemsActivity.this.rowSize = rowSize; 
		}
		
		@Override
		public void markSortMode (PreferencesManager.SortType sortType)
		{
			slideoutMenuHandler.markSortType(sortType);
		}
	}

	private class NoteAdapter extends ArrayAdapter <ItemInterface>
	{
		private final int NOTE = 0;
		private final int FOLDER = 1;

		LayoutInflater inflater = getLayoutInflater ();

		NoteAdapter () { super ( ItemsActivity.this, R.layout.row_note, items ); }

		public int getCount () { return items.size (); }

		public View getView ( int position, View convertView, ViewGroup parentView )
		{
			ItemInterface item = items.get ( position );

			int type = getItemViewType ( position );

			NoteViewHolder noteViewHolder = null;
			FolderViewHolder folderViewHolder = null;

			if (convertView == null)
			{
				noteViewHolder = new NoteViewHolder ();
				folderViewHolder = new FolderViewHolder ();

				switch (type)
				{
				case NOTE:

					convertView = inflater.inflate ( R.layout.row_note, null );

					noteViewHolder.lock = (ImageView) convertView.findViewById ( R.id.imageview_lock );
					noteViewHolder.title = (TextView) convertView.findViewById ( R.id.text_note_title );
					noteViewHolder.date = (TextView) convertView.findViewById ( R.id.text_date );
					noteViewHolder.note = (ViewGroup) convertView.findViewById ( R.id.layout_note_icon );

					convertView.setTag ( noteViewHolder );

					break;

				case FOLDER:

					convertView = inflater.inflate ( R.layout.row_folder, null );

					folderViewHolder.lock = (ImageView) convertView.findViewById ( R.id.imageview_lock_icon );
					folderViewHolder.title = (TextView) convertView.findViewById ( R.id.text_folder_name );
					folderViewHolder.numberOfNotes = (TextView) convertView.findViewById ( R.id.text_notes_number );

					convertView.setTag ( folderViewHolder );

					break;
				}
			}

			if (type == FOLDER)
			{
				folderViewHolder = (FolderViewHolder) convertView.getTag ();

				int priority = items.get ( position ).getPriority();
				switch (priority)
				{
				case Item.PRIORITY_NORMAL:
					folderViewHolder.title.setTextColor ( Color.parseColor ( blueColor ) );
					folderViewHolder.numberOfNotes.setTextColor ( Color.parseColor ( blueColor ) );
					break;
				case Item.PRIORITY_HIGHT:
					folderViewHolder.title.setTextColor ( Color.parseColor ( orangeColor ) );
					folderViewHolder.numberOfNotes.setTextColor ( Color.parseColor ( orangeColor ) );
					break;
				case Item.PRIORITY_VERY_HIGH:
					folderViewHolder.title.setTextColor ( Color.parseColor ( redColor ) );
					folderViewHolder.numberOfNotes.setTextColor ( Color.parseColor ( redColor ) );
				}

				folderViewHolder.title.setText ( item.getTitle() + " " );

				if (item.getIsProtected() != true)
					folderViewHolder.lock.setVisibility ( View.INVISIBLE );
				else
					folderViewHolder.lock.setVisibility ( View.VISIBLE );
				
				folderViewHolder.numberOfNotes.setText(Integer.toString(item.getNotesInside()));
			}
			else
			{
				noteViewHolder = (NoteViewHolder) convertView.getTag ();

				int priority = items.get ( position ).getPriority();

				switch (priority)
				{
				case Item.PRIORITY_NORMAL:

					noteViewHolder.title.setTextColor ( Color.parseColor ( blueColor ) );
					noteViewHolder.date.setTextColor ( Color.parseColor ( blueColor ) );
					if (rowSize == PreferencesManager.RowSize.BIG)
						noteViewHolder.note.setBackgroundResource ( R.drawable.note_low_priority_big );
					if (rowSize == PreferencesManager.RowSize.MEDIUM)
						noteViewHolder.note.setBackgroundResource ( R.drawable.note_low_priority_medium );
					if (rowSize == PreferencesManager.RowSize.SMALL)
						noteViewHolder.note.setBackgroundResource ( R.drawable.note_low_priority_small );
					break;

				case Item.PRIORITY_HIGHT:

					noteViewHolder.title.setTextColor ( Color.parseColor ( orangeColor ) );
					noteViewHolder.date.setTextColor ( Color.parseColor ( orangeColor ) );
					if (rowSize == PreferencesManager.RowSize.BIG)
						noteViewHolder.note.setBackgroundResource ( R.drawable.note_medium_priority_big );
					if (rowSize == PreferencesManager.RowSize.MEDIUM)
						noteViewHolder.note.setBackgroundResource ( R.drawable.note_medium_priority_medium );
					if (rowSize == PreferencesManager.RowSize.SMALL)
						noteViewHolder.note.setBackgroundResource ( R.drawable.note_medium_priority_small );
					break;

				case Item.PRIORITY_VERY_HIGH:

					noteViewHolder.title.setTextColor ( Color.parseColor ( redColor ) );
					noteViewHolder.date.setTextColor ( Color.parseColor ( redColor ) );
					if (rowSize == PreferencesManager.RowSize.BIG)
						noteViewHolder.note.setBackgroundResource ( R.drawable.note_hign_priority_big );
					if (rowSize == PreferencesManager.RowSize.MEDIUM)
						noteViewHolder.note.setBackgroundResource ( R.drawable.note_high_priority_medium );
					if (rowSize == PreferencesManager.RowSize.SMALL)
						noteViewHolder.note.setBackgroundResource ( R.drawable.note_high_priority_small );
				}

				noteViewHolder.title.setText ( item.getTitle() + " " );

				String date = item.getDate().substring ( 5, 7 ) + "." + item.getDate().substring ( 8, 10 ) + " " + item.getDate().substring ( 11, 16 ) + " ";

				noteViewHolder.date.setText ( date );

				if (item.getIsProtected() != true)
					noteViewHolder.lock.setVisibility ( View.INVISIBLE );
				else
					noteViewHolder.lock.setVisibility ( View.VISIBLE );
			}

			return convertView;
		}

		public int getViewTypeCount ()
		{
			return 2;
		}

		public int getItemViewType ( int position )
		{
			if (items.get ( position ).getType() == Item.Type.NOTE)
				return NOTE;
			else
				return FOLDER;
		}

		public class NoteViewHolder
		{
			public ImageView lock;
			public TextView title;
			public TextView date;
			public ViewGroup note;
		}

		public class FolderViewHolder
		{
			public ImageView lock;
			public TextView title;
			public TextView numberOfNotes;
			public ViewGroup folder;
		}
	}

	private class MenuAnimationHandler implements AnimationListener
	{
		View rootView;
		boolean slideOutMenuIsOn = false;
		AnimParams animParams = new AnimParams ();

		CompletitionHandler mAnimationEndListener;

		View slideoutMenu = findViewById ( R.id.scrollview_slide_out_menu );

		public MenuAnimationHandler () { rootView = findViewById ( R.id.layout_main ); }

		class AnimParams
		{
			int left, right, top, bottom;

			void init ( int left, int top, int right, int bottom )
			{
				this.left = left;
				this.top = top;
				this.right = right;
				this.bottom = bottom;
			}
		}

		void layoutApp ( boolean menuOut )
		{
			System.out.println ( "layout [" + animParams.left + "," + animParams.top + "," + animParams.right + "," + animParams.bottom + "]" );
			rootView.layout ( animParams.left, animParams.top, animParams.right, animParams.bottom );
			// Now that we've set the app.layout property we can clear the
			// animation, flicker avoided :)
			rootView.clearAnimation ();
		}

		public void performSlidingAnimation ( CompletitionHandler listener )
		{
			mAnimationEndListener = listener;

			if (!animationIsInProgress)
			{
				System.out.println ( "onClick " + new Date () );
				MenuAnimationHandler me = this;
				Animation anim;

				int w = rootView.getMeasuredWidth ();
				int h = rootView.getMeasuredHeight ();
				int left = (int) (rootView.getMeasuredWidth () * 0.8);

				if (!slideOutMenuIsOn)
				{
					anim = new TranslateAnimation ( 0, left, 0, 0 );
					slideoutMenu.setVisibility ( View.VISIBLE );
					animParams.init ( left, 0, left + w, h );
				}
				else
				{
					anim = new TranslateAnimation ( 0, -left, 0, 0 );
					animParams.init ( 0, 0, w, h );
				}

				anim.setDuration ( 500 );
				anim.setAnimationListener ( me );
				// Tell the animation to stay as it ended (we are going to set
				// the
				// app.layout first than remove this property)
				anim.setFillAfter ( true );

				// Only use fillEnabled and fillAfter if we don't call layout
				// ourselves.
				// We need to do the layout ourselves and not use fillEnabled
				// and
				// fillAfter because when the anim is finished
				// although the View appears to have moved, it is actually just
				// a
				// drawing effect and the View hasn't moved.
				// Therefore clicking on the screen where the button appears
				// does
				// not work, but clicking where the View *was* does
				// work.
				// anim.setFillEnabled(true);
				// anim.setFillAfter(true);

				rootView.startAnimation ( anim );
			}
		}
		
		@Override public void onAnimationStart ( Animation animation ) { animationIsInProgress = true; }
		
		@Override public void onAnimationRepeat ( Animation animation ) {}
		
		@Override public void onAnimationEnd ( Animation animation )
		{
			System.out.println ( "onAnimationEnd" );
			slideOutMenuIsOn = !slideOutMenuIsOn;
			if (!slideOutMenuIsOn)
				slideoutMenu.setVisibility ( View.INVISIBLE );
			layoutApp ( slideOutMenuIsOn );

			animationIsInProgress = false;

			if (mAnimationEndListener != null)
				mAnimationEndListener.completeAction ();
		}
	}

	private class ItemsListOnItemClickListener implements OnItemClickListener
	{
		public void onItemClick ( AdapterView <?> parent, View view, int position, long id )
		{
			mPresenter.onItemClick(position);
		}
	}

	private class ItemsListOnItemLongClickListener implements OnItemLongClickListener
	{
		private Dialog contextMenu;
		private View dialogContent;
		
		private ViewGroup itemDelete;
		private ViewGroup itemChangeTitle;
		private ViewGroup itemPassword;
		private ViewGroup itemPriority;
		private ViewGroup itemSend;
		
		private int itemIndex;
		
		public boolean onItemLongClick ( AdapterView <?> parent, View view, int position, long id )
		{
			itemIndex = position;
			
			mPresenter.selectItem(position);

			contextMenu = new Dialog ( ItemsActivity.this, R.style.CustomDialogTheme );
			contextMenu.requestWindowFeature ( Window.FEATURE_NO_TITLE );
			
			Item.Type type = items.get ( position ).getType();
			if (type == Item.Type.NOTE)
			{
				dialogContent = getLayoutInflater ().inflate ( R.layout.dialog_note_context_menu, null );
				
				initNoteMenuItems();
				setNoteMenuIcons();
				setNoteMenuTexts();
				connectNoteMenuListeners();
			}
			else
			{
				dialogContent = getLayoutInflater ().inflate ( R.layout.dialog_folder_context_menu, null );
				
				initFolderMenuItems();
				setFolderMenuIcons();
				setFolderMenuTexts();
				connectFolderMenuListeners();
			}

			contextMenu.setContentView ( dialogContent );
			contextMenu.show ();

			return true;
		}
		
		private void initFolderMenuItems ()
		{
			itemDelete = (ViewGroup) dialogContent.findViewById(R.id.item_delete);
			itemChangeTitle = (ViewGroup) dialogContent.findViewById(R.id.item_change_title);
			itemPassword = (ViewGroup) dialogContent.findViewById(R.id.item_set_delete_password);
			itemPriority = (ViewGroup) dialogContent.findViewById(R.id.item_set_priority);
		}
		
		private void setFolderMenuIcons ()
		{
			ImageView icon;
			
			icon = (ImageView) itemDelete.findViewById(R.id.icon);
			icon.setImageResource(R.drawable.cross_big);

			icon = (ImageView) itemChangeTitle.findViewById(R.id.icon);
			icon.setImageResource(R.drawable.bookmark_big);
			
			icon = (ImageView) itemPassword.findViewById(R.id.icon);
			
			boolean isProtected = items.get(itemIndex).getIsProtected() == true;
			if (isProtected)
				icon.setImageResource(R.drawable.unlocked);
			else
				icon.setImageResource(R.drawable.locked);
			
			icon = (ImageView) itemPriority.findViewById(R.id.icon);
			icon.setImageResource(R.drawable.growing_chart);
		}
		
		private void setFolderMenuTexts ()
		{
			TextView text;
			
			text = (TextView) itemDelete.findViewById(R.id.text);
			text.setText("Delete");
			
			text = (TextView) itemChangeTitle.findViewById(R.id.text);
			text.setText("Change Title");
			
			text = (TextView) itemPassword.findViewById(R.id.text);
			
			boolean isProtected = items.get(itemIndex).getIsProtected() == true;
			if (isProtected)
				text.setText("Delete Password");
			else
				text.setText("Set Password");
			
			text = (TextView) itemPriority.findViewById(R.id.text);
			text.setText("Set Priority");
		}
		
		private void connectFolderMenuListeners ()
		{
			itemDelete.setOnClickListener ( new OnClickListener ()
			{ public void onClick ( View v ) { mPresenter.onDelete (); contextMenu.dismiss(); } } );

			itemChangeTitle.setOnClickListener ( new OnClickListener ()
			{ public void onClick ( View v ) { mPresenter.onChangeTitle (); contextMenu.dismiss(); } } );

			itemPassword.setOnClickListener ( new OnClickListener ()
			{ public void onClick ( View v ) { mPresenter.onPassword (); contextMenu.dismiss(); } } );

			itemPriority.setOnClickListener ( new OnClickListener ()
			{ public void onClick ( View v ) { mPresenter.onChangePriority (); contextMenu.dismiss(); } } );
		}
		
		private void initNoteMenuItems ()
		{
			initFolderMenuItems();
			
			itemSend = (ViewGroup) dialogContent.findViewById(R.id.item_send);
		}
		
		private void setNoteMenuIcons ()
		{
			setFolderMenuIcons();
			
			ImageView icon;
			icon = (ImageView) itemSend.findViewById(R.id.icon);
			icon.setImageResource(R.drawable.letter_opened);
		}
		
		private void setNoteMenuTexts ()
		{
			setFolderMenuTexts();
			
			TextView text;
			text = (TextView) itemSend.findViewById(R.id.text);
			text.setText("Send");
		}
		
		private void connectNoteMenuListeners ()
		{
			connectFolderMenuListeners();
			
			itemSend = (ViewGroup) dialogContent.findViewById ( R.id.item_send );
			itemSend.setOnClickListener ( new OnClickListener (){ public void onClick ( View v )
			{
				String textToShare = ((Item) items.get ( itemIndex )).getNote();
				mPresenter.onSend ( textToShare );
			} });
		}
	}
	
	private class SlideoutMenuHandler
	{
		private ViewGroup itemSearch;
		private ViewGroup itemNewFolder;
		private ViewGroup itemSortByDate;
		private ViewGroup itemSortAlphabetically;
		private ViewGroup itemSortByType;
		private ViewGroup itemBigRows;
		private ViewGroup itemMediumRows;
		private ViewGroup itemSmallRows;
		
		private ViewGroup mSortHearder;
		private ViewGroup mViewHeader;
		
		public void setupSlideoutMenu ()
		{
			itemSearch = (ViewGroup) findViewById ( R.id.item_search );
			itemNewFolder = (ViewGroup) findViewById ( R.id.item_new_folder );
			itemSortByDate = (ViewGroup) findViewById ( R.id.item_sort_by_date );
			itemSortAlphabetically = (ViewGroup) findViewById ( R.id.item_sort_alphabetically );
			itemSortByType = (ViewGroup) findViewById ( R.id.item_sort_by_type );
			itemBigRows = (ViewGroup) findViewById ( R.id.item_view_big_rows );
			itemMediumRows = (ViewGroup) findViewById ( R.id.item_view_medium_rows );
			itemSmallRows = (ViewGroup) findViewById ( R.id.item_view_small_rows );
			mSortHearder = (ViewGroup) findViewById(R.id.item_header_sort);
			mViewHeader = (ViewGroup) findViewById(R.id.item_header_view);
			
			setIcons();
			setTexts();
			setActionListeners();
		}

		public void markSortType (PreferencesManager.SortType sortType)
		{
			ImageView byDatecheckImage = (ImageView) itemSortByDate.findViewById(R.id.imageview_checkmark);
			ImageView byTypecheckImage = (ImageView) itemSortByType.findViewById(R.id.imageview_checkmark);
			ImageView alphabeticallycheckImage = (ImageView) itemSortAlphabetically.findViewById(R.id.imageview_checkmark);
			
			byDatecheckImage.setVisibility(View.INVISIBLE);
			byTypecheckImage.setVisibility(View.INVISIBLE);
			alphabeticallycheckImage.setVisibility(View.INVISIBLE);
			
			switch (sortType)
			{
			case ALPHABATICALLY:
				alphabeticallycheckImage.setVisibility(View.VISIBLE);
				break;
			case BY_DATE:
				byDatecheckImage.setVisibility(View.VISIBLE);
				break;
			case BY_TYPE:
				byTypecheckImage.setVisibility(View.VISIBLE);
			}
		}
		
		public void markRowSize (PreferencesManager.RowSize rowSize)
		{
			ImageView checkImageRowsBig = (ImageView) itemBigRows.findViewById(R.id.imageview_checkmark);
			ImageView checkImageRowsMedium = (ImageView) itemMediumRows.findViewById(R.id.imageview_checkmark);
			ImageView checkImageRowsSmall = (ImageView) itemSmallRows.findViewById(R.id.imageview_checkmark);
			
			checkImageRowsBig.setVisibility(View.INVISIBLE);
			checkImageRowsMedium.setVisibility(View.INVISIBLE);
			checkImageRowsSmall.setVisibility(View.INVISIBLE);
			
			switch (rowSize)
			{
			case BIG:
				checkImageRowsBig.setVisibility(View.VISIBLE);
				break;
			case MEDIUM:
				checkImageRowsMedium.setVisibility(View.VISIBLE);
				break;
			case SMALL:
				checkImageRowsSmall.setVisibility(View.VISIBLE);
			}
		}
		
		private void setIcons ()
		{
			ImageView cellImage;
			
			cellImage = (ImageView) itemNewFolder.findViewById(R.id.imageview_icon);
			cellImage.setImageResource(R.drawable.magnifier_2);
			
			cellImage = (ImageView) itemNewFolder.findViewById(R.id.imageview_icon);
			cellImage.setImageResource(R.drawable.folder_small);
			
			cellImage = (ImageView) itemSortByDate.findViewById(R.id.imageview_icon);
			cellImage.setImageResource(R.drawable.calendar);
			
			cellImage = (ImageView) itemSortAlphabetically.findViewById(R.id.imageview_icon);
			cellImage.setImageResource(R.drawable.chat);
			
			cellImage = (ImageView) itemSortByType.findViewById(R.id.imageview_icon);
			cellImage.setImageResource(R.drawable.chat);
			
			cellImage = (ImageView) itemBigRows.findViewById(R.id.imageview_icon);
			cellImage.setImageResource(R.drawable.note_low_priority_very_small);
			
			cellImage = (ImageView) itemMediumRows.findViewById(R.id.imageview_icon);
			cellImage.setImageResource(R.drawable.note_low_priority_very_very_small);
			
			cellImage = (ImageView) itemSmallRows.findViewById(R.id.imageview_icon);
			cellImage.setImageResource(R.drawable.note_low_priority_very_very_very_small);
		}
		
		private void setTexts ()
		{
			TextView cellText;
			
			cellText = (TextView) itemNewFolder.findViewById(R.id.text_title);
			cellText.setText("New Folder");
			
			cellText = (TextView) itemSearch.findViewById(R.id.text_title);
			cellText.setText("Search");
			
			cellText = (TextView) mSortHearder.findViewById(R.id.text_title);
			cellText.setText("Sort");
			
			cellText = (TextView) itemSortAlphabetically.findViewById(R.id.text_title);
			cellText.setText("Alphabetically");
			
			cellText = (TextView) itemSortByDate.findViewById(R.id.text_title);
			cellText.setText("By Date");
			
			cellText = (TextView) itemSortByType.findViewById(R.id.text_title);
			cellText.setText("By Type");
			
			cellText = (TextView) mViewHeader.findViewById(R.id.text_title);
			cellText.setText("View");
			
			cellText = (TextView) itemBigRows.findViewById(R.id.text_title);
			cellText.setText("Big Rows");
			
			cellText = (TextView) itemMediumRows.findViewById(R.id.text_title);
			cellText.setText("Medium Rows");
			
			cellText = (TextView) itemSmallRows.findViewById(R.id.text_title);
			cellText.setText("Small Rows");
		}
		
		private void setActionListeners ()
		{
			itemSearch.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onSearch (); } } );

			itemNewFolder.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onNewFolder (); } } );

			itemSortByDate.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onSort (PreferencesManager.SortType.BY_DATE); } } );

			itemSortAlphabetically.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onSort (PreferencesManager.SortType.ALPHABATICALLY); } } );

			itemSortByType.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onSort (PreferencesManager.SortType.BY_TYPE); } } );

			itemBigRows.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onChangeItemSize (PreferencesManager.RowSize.BIG); } } );

			itemMediumRows.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onChangeItemSize (PreferencesManager.RowSize.MEDIUM); } } );

			itemSmallRows.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onChangeItemSize (PreferencesManager.RowSize.SMALL); } } );
		}
	}
}