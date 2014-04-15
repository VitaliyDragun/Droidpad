package activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import presenters.ItemListPresenter;
import presenters.ItemListPresenter.OnAnimationEndListener;
import structures.Item;
import vitaliy.dragun.droidpad_2nd_edition.Colors;
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

public class ItemListActivity extends ListActivity implements Colors
{
	private ItemListPresenter mPresenter;

	private PresenterListener presenterListener = new PresenterListener ();

	private MenuAnimationHandler menuAnimationHandler;

	private ListView mListItems;

	private TextView mTextViewTitle;

	private InputMethodManager mInputMethodManager;

	private NoteAdapter noteAdapter;
	private int pressedItemIndex = -1;

	public List <Item> items = new ArrayList <Item> ();

	private enum RowSize
	{
		BIG, MEDIUM, SMALL
	};

	private RowSize rowSize = RowSize.MEDIUM;

	private boolean searchMode = false;

	private ViewGroup mSearchBar = null;

	private int slideOutMenuClickedItemIndex = -1;
	private boolean slideOutMenuItemWasClicked = false;

	private boolean innerFolder = false;

	private boolean animationIsInProgress = false;

	private enum EnterPasswordMode
	{
		DELETE_MODE, CHANGE_TITLE_MODE, SHARE_MODE, SET_PRIORITY_MODE, SET_PASSWORD_MODE, DELETE_PASSWORD_MODE
	};

	// Remember scroll positions in root folder when user enters inner folder
	// to recreate it when user leave inner folder
	private int index = -1;
	private int top = -1;

	// Used to lock down slide out menu
	private boolean backButtonIsBlocked = true;

	// Newly added item index in items
	private int scrollToNewItemIndex = -1;

	private TextView listActivityTitle = null;

	// Is used to fix bug when in search mode users presses back button
	// and if the keyboard is not shown the slide out menu appears
	private boolean searchModeWasJustDisabled = false;

	ImageButton exitSearchButton;
	EditText mEditTextSearch;

	private Toast mToast;
	
	private Dialog mDisplayingDialog;
	
	private SlideoutMenuInitializer slideoutMenuInitializer = new SlideoutMenuInitializer();

	private void connectAndSetupViews ()
	{
		mEditTextSearch = (EditText) findViewById ( R.id.search_edittext );
		listActivityTitle = (TextView) findViewById ( R.id.notelist_title );
		mSearchBar = (ViewGroup) findViewById ( R.id.search_bar );
		exitSearchButton = (ImageButton) findViewById ( R.id.exit_search_button );
		mEditTextSearch = (EditText) findViewById ( R.id.search_edittext );
		
		slideoutMenuInitializer.setupSlideoutMenu();

		mListItems = (ListView) findViewById ( android.R.id.list );
		noteAdapter = new NoteAdapter ();
		setListAdapter ( noteAdapter );
	}

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate ( savedInstanceState );

		setContentView ( R.layout.activity_items_list );

		mInputMethodManager = (InputMethodManager) getSystemService ( Context.INPUT_METHOD_SERVICE );
		
		mToast = Toast.makeText ( this, null, Toast.LENGTH_SHORT );//No, I didn't
		mToast.setGravity ( Gravity.CENTER, 0, 0 );
		mToast.setDuration(Toast.LENGTH_LONG);

		connectAndSetupViews ();

		mPresenter = new ItemListPresenter ( presenterListener );

		items = mPresenter.getItems ();

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

	public void showDeleteDialog ()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder ( this );
		builder.setIcon ( R.drawable.icon_pack_delete );

		if (items.get ( pressedItemIndex ).getType() == Item.Type.FOLDER)
			builder.setTitle ( "Delete folder and all its content?" );
		else
			builder.setTitle ( "Delete note?" );

		builder.setPositiveButton ( R.string.dialog_yes, new DialogInterface.OnClickListener ()
		{
			public void onClick ( DialogInterface dialog, int whichButton )
			{
				if (items.get ( pressedItemIndex ).getIsProtected () == true)
					showEnterPasswordDialog ( EnterPasswordMode.DELETE_MODE );
				else
					deleteEntry ();
			}
		} );

		builder.setNegativeButton ( R.string.dialog_cancel, new DialogInterface.OnClickListener ()
		{
			public void onClick ( DialogInterface dialog, int whichButton )
			{
			}
		} );

		AlertDialog dialog = builder.create ();
		dialog.show ();
	}

	public void showTitleDialog ()
	{

	}

	private void showChangePriorityDialog ()
	{
		LayoutInflater inflater = getLayoutInflater ();
		View listView = inflater.inflate ( R.layout.dialog_change_priority, null );

		AlertDialog.Builder builder = new AlertDialog.Builder ( this );
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

	private void showSetPasswordDialog ()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder ( this );

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

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences ( ItemListActivity.this );

				Toast toast;

				if (preferences.getString ( "Password", "1111" ).equals ( password ))
				{
					addAndSortItems ();

					toast = Toast.makeText ( ItemListActivity.this, "Note is now locked".toString (), Toast.LENGTH_SHORT );
					toast.setGravity ( Gravity.CENTER, 0, 0 );
					toast.show ();
				}
				else
				{
					toast = Toast.makeText ( ItemListActivity.this, "Wrong password".toString (), Toast.LENGTH_SHORT );
					toast.setGravity ( Gravity.CENTER, 0, 0 );
					toast.show ();
				}
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

	private void setPassword ()
	{
		Item newNote = items.get ( pressedItemIndex );
		newNote.setIsProtected (true);

		addAndSortItems ();

		Toast toast = Toast.makeText ( ItemListActivity.this, "", Toast.LENGTH_SHORT );
		if (items.get ( pressedItemIndex ).getType () == Item.Type.FOLDER)
			toast.setText ( "Folder is locked".toString () );
		else
			toast.setText ( "Note is locked".toString () );
		toast.setGravity ( Gravity.CENTER, 0, 0 );
		toast.show ();
	}

	private void deletePassword ()
	{
		Item newNote = items.get ( pressedItemIndex );
		newNote.setIsProtected (false);

		addAndSortItems ();

		Toast toast = Toast.makeText ( ItemListActivity.this, "", Toast.LENGTH_SHORT );
		if (items.get ( pressedItemIndex ).getType() == Item.Type.FOLDER)
			toast.setText ( "Folder is unlocked".toString () );
		else
			toast.setText ( "Note is unlocked".toString () );
		toast.setGravity ( Gravity.CENTER, 0, 0 );
		toast.show ();

	}

	private void showEnterPasswordDialog ( final EnterPasswordMode mode )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder ( ItemListActivity.this );

		LayoutInflater inflater = getLayoutInflater ();
		final View dialogContent = inflater.inflate ( R.layout.dialog_enter_password, null );

		builder.setTitle ( "Enter password".toString () );
		builder.setIcon ( R.drawable.lock_48 );
		builder.setView ( dialogContent );
		builder.setPositiveButton ( "OK".toString (), new DialogInterface.OnClickListener ()
		{
			public void onClick ( DialogInterface dialog, int which )
			{

				String password = ((EditText) dialogContent.findViewById ( R.id.enter_password_edittext )).getText ().toString ();

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences ( ItemListActivity.this );

				Toast toast;

				if (preferences.getString ( "Password", "1111" ).equals ( password ))
				{

					switch (mode)
					{

					case DELETE_MODE:
						deleteEntry ();
						break;
					case CHANGE_TITLE_MODE:
						showTitleDialog ();
						break;
					case SHARE_MODE:
						share ();
						break;
					case SET_PRIORITY_MODE:
						showChangePriorityDialog ();
						break;
					case SET_PASSWORD_MODE:
						setPassword ();
						break;
					case DELETE_PASSWORD_MODE:
						deletePassword ();
						break;
					}
				}
				else
				{
					toast = Toast.makeText ( ItemListActivity.this, "Wrong password".toString (), Toast.LENGTH_SHORT );
					toast.setGravity ( Gravity.CENTER, 0, 0 );
					toast.show ();
				}
			}
		} );
		builder.setNegativeButton ( "Cancel".toString (), new DialogInterface.OnClickListener ()
		{

			@Override
			public void onClick ( DialogInterface dialog, int which )
			{

			}
		} );

		// Show created dialog
		AlertDialog enterPasswordDialog = builder.create ();
		enterPasswordDialog.show ();

		mInputMethodManager.toggleSoftInput ( InputMethodManager.SHOW_FORCED, 0 );
	}

	protected void openEntry ()
	{
	}

	private void deleteEntry ()
	{
	}

	private void share ()
	{
		/*
		 * final Intent intent = new Intent(Intent.ACTION_SEND);
		 * intent.setType("text/plain"); intent.putExtra(Intent.EXTRA_TEXT,
		 * items.get(pressedItemIndex) .note);
		 * 
		 * try { startActivity(Intent.createChooser(intent,
		 * "Select an action")); } catch
		 * (android.content.ActivityNotFoundException ex) { // (handle error) }
		 */
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu _menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_note_list, menu);

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
				addAndSortItems ();

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

	private static void addAndSortItems ()
	{
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

	public boolean onSearchRequested ()
	{
		/*
		 * if(!searchMode) enterSearchMode(); else exitSearchMode();
		 */

		return true;
	}

	private void exitSearchMode ()
	{
		mSearchBar.setVisibility ( View.GONE );

		mInputMethodManager.hideSoftInputFromWindow ( mEditTextSearch.getWindowToken (), 0 );

		mEditTextSearch.setText ( "".toString () );

		searchMode = false;

		mListItems.setScrollContainer ( false );
	}

	private class PresenterListener implements ItemListPresenter.IPresenterListener
	{
		@Override
		public void enterSearchMode ()
		{
			mSearchBar.setVisibility ( View.VISIBLE );

			mEditTextSearch.requestFocus ();

			mInputMethodManager.toggleSoftInput ( InputMethodManager.SHOW_FORCED, 0 );

			mListItems.setScrollContainer ( true );

			listActivityTitle.setText ( "Search".toString () );

			searchMode = true;
		}

		@Override
		public void showNewFolderDialog ()
		{
			AlertDialog.Builder builder = new AlertDialog.Builder ( ItemListActivity.this );
			builder.setTitle ( "Add new folder".toString () );

			LayoutInflater inflater = getLayoutInflater ();
			final View dialogView = inflater.inflate ( R.layout.dialog_change_title, null );
			builder.setView ( dialogView );

			builder.setPositiveButton ( "OK".toString (), new DialogInterface.OnClickListener ()
			{
				@Override
				public void onClick ( DialogInterface dialog, int which )
				{
					Item newNote = new Item ();

					EditText editText = (EditText) dialogView.findViewById ( R.id.rename_text_field );

					items.add ( newNote );
					addAndSortItems ();

					hideShowTextInCenter ();
				}
			} );
			builder.setNegativeButton ( "Cancel", new DialogInterface.OnClickListener ()
			{
				@Override
				public void onClick ( DialogInterface dialog, int which )
				{
				}
			} );

			final AlertDialog newFolderDialog = builder.create ();

			final EditText editText = (EditText) dialogView.findViewById ( R.id.rename_text_field );
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

			newFolderDialog.show ();

			newFolderDialog.getButton ( AlertDialog.BUTTON_POSITIVE ).setEnabled ( false );

			mInputMethodManager.toggleSoftInput ( InputMethodManager.SHOW_FORCED, 0 );
		}

		@Override
		public void showChangeTitleDialog (final ItemListPresenter.OnDialogActionListener dialogListener)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder ( ItemListActivity.this );

			builder.setIcon ( R.drawable.change_title_icon_48 );
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
			
			mDisplayingDialog = changeTitleDialog;
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
		public void updateItemList ( List <Item> upToDateItems )
		{
			items.clear ();
			items.addAll ( upToDateItems );
			noteAdapter.notifyDataSetChanged ();
		}

		@Override
		public void showPasswordDialog ()
		{
			AlertDialog.Builder builder = new AlertDialog.Builder ( ItemListActivity.this );

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

			AlertDialog.Builder builder = new AlertDialog.Builder ( ItemListActivity.this );
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
		public void performSlidingAnimation ( ItemListPresenter.OnAnimationEndListener listener )
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
		public void showConfirmDeleteDialog (final ItemListPresenter.OnDialogActionListener dialogListener)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder ( ItemListActivity.this );
			builder.setIcon ( R.drawable.icon_pack_delete );

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
			
			mDisplayingDialog = dialog;
		}

		@Override
		public void dissmissDialog () { mDisplayingDialog.dismiss (); }
	}

	class NoteAdapter extends ArrayAdapter <Item>
	{
		private final int NOTE = 0;
		private final int FOLDER = 1;

		LayoutInflater inflater = getLayoutInflater ();

		NoteAdapter ()
		{
			super ( ItemListActivity.this, R.layout.row_note, items );
		}

		public int getCount ()
		{
			return items.size ();
		}

		public View getView ( int position, View convertView, ViewGroup parentView )
		{
			Item item = items.get ( position );

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

					noteViewHolder.lock = (ImageView) convertView.findViewById ( R.id.keys );
					noteViewHolder.title = (TextView) convertView.findViewById ( R.id.note_title );
					noteViewHolder.date = (TextView) convertView.findViewById ( R.id.date );
					noteViewHolder.note = (ViewGroup) convertView.findViewById ( R.id.note_image_layout );

					convertView.setTag ( noteViewHolder );

					break;

				case FOLDER:

					convertView = inflater.inflate ( R.layout.row_folder, null );

					folderViewHolder.lock = (ImageView) convertView.findViewById ( R.id.keys );
					folderViewHolder.title = (TextView) convertView.findViewById ( R.id.folder_name );
					folderViewHolder.numberOfNotes = (TextView) convertView.findViewById ( R.id.number_of_notes_inside_folder );

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
					if (rowSize == RowSize.BIG)
						noteViewHolder.note.setBackgroundResource ( R.drawable.low_priority_note_icon_72 );
					if (rowSize == RowSize.MEDIUM)
						noteViewHolder.note.setBackgroundResource ( R.drawable.low_priority_note_icon_64 );
					if (rowSize == RowSize.SMALL)
						noteViewHolder.note.setBackgroundResource ( R.drawable.low_priority_note_icon_56 );
					break;

				case Item.PRIORITY_HIGHT:

					noteViewHolder.title.setTextColor ( Color.parseColor ( orangeColor ) );
					noteViewHolder.date.setTextColor ( Color.parseColor ( orangeColor ) );
					if (rowSize == RowSize.BIG)
						noteViewHolder.note.setBackgroundResource ( R.drawable.medium_priority_note_icon_72 );
					if (rowSize == RowSize.MEDIUM)
						noteViewHolder.note.setBackgroundResource ( R.drawable.medium_priority_note_icon_64 );
					if (rowSize == RowSize.SMALL)
						noteViewHolder.note.setBackgroundResource ( R.drawable.medium_priority_note_icon_56 );
					break;

				case Item.PRIORITY_VERY_HIGH:

					noteViewHolder.title.setTextColor ( Color.parseColor ( redColor ) );
					noteViewHolder.date.setTextColor ( Color.parseColor ( redColor ) );
					if (rowSize == RowSize.BIG)
						noteViewHolder.note.setBackgroundResource ( R.drawable.high_priority_note_icon_72 );
					if (rowSize == RowSize.MEDIUM)
						noteViewHolder.note.setBackgroundResource ( R.drawable.high_priority_note_icon_64 );
					if (rowSize == RowSize.SMALL)
						noteViewHolder.note.setBackgroundResource ( R.drawable.high_priority_note_icon_56 );
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

		OnAnimationEndListener mAnimationEndListener;

		View slideoutMenu = findViewById ( R.id.menu );

		public MenuAnimationHandler () { rootView = findViewById ( R.id.notelist_activity ); }

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

		public void performSlidingAnimation ( ItemListPresenter.OnAnimationEndListener listener )
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
			pressedItemIndex = position;

			if (items.get ( position ).getIsProtected() == true)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder ( ItemListActivity.this );

				LayoutInflater inflater = ItemListActivity.this.getLayoutInflater ();
				final View dialogContent = inflater.inflate ( R.layout.dialog_enter_password, null );

				builder.setTitle ( "Enter password".toString () );
				builder.setIcon ( R.drawable.lock_48 );
				builder.setView ( dialogContent );
				builder.setPositiveButton ( "OK".toString (), new DialogInterface.OnClickListener ()
				{
					@Override
					public void onClick ( DialogInterface dialog, int which )
					{
						String password = ((EditText) dialogContent.findViewById ( R.id.enter_password_edittext )).getText ().toString ();
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences ( ItemListActivity.this );
						if (preferences.getString ( "Password", "1111" ).equals ( password ))
							openEntry ();
						else
						{
							Toast toast = Toast.makeText ( ItemListActivity.this, "Wrong password".toString (), Toast.LENGTH_SHORT );
							toast.setGravity ( Gravity.CENTER, 0, 0 );
							toast.show ();
						}
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
			else
				openEntry ();
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

			contextMenu = new Dialog ( ItemListActivity.this, R.style.CustomDialogTheme );
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
			icon.setImageResource(R.drawable.icon_set_delete);

			icon = (ImageView) itemChangeTitle.findViewById(R.id.icon);
			icon.setImageResource(R.drawable.icon_set_change_title);
			
			icon = (ImageView) itemPassword.findViewById(R.id.icon);
			
			boolean isProtected = items.get(itemIndex).getIsProtected() == true;
			if (isProtected)
				icon.setImageResource(R.drawable.icon_set_unlock);
			else
				icon.setImageResource(R.drawable.icon_set_lock);
			
			icon = (ImageView) itemPriority.findViewById(R.id.icon);
			icon.setImageResource(R.drawable.icon_set_priority);
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
			{ public void onClick ( View v ) { mPresenter.onDelete (itemIndex); contextMenu.dismiss(); } } );

			itemChangeTitle.setOnClickListener ( new OnClickListener ()
			{ public void onClick ( View v ) { mPresenter.onChangeTitle ( itemIndex ); contextMenu.dismiss(); } } );

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
			icon.setImageResource(R.drawable.icon_set_send2);
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
	
	private class SlideoutMenuInitializer
	{
		private ViewGroup mSlideoutMenuSearch;
		private ViewGroup mSlideoutMenuNewFolder;
		private ViewGroup mSlideoutMenuSortBydate;
		private ViewGroup mSlideoutMenuSortAlphabetically;
		private ViewGroup mSlideoutMenuSortBytype;
		private ViewGroup mSlideoutMenuViewBigRows;
		private ViewGroup mSlideoutMenuViewMediumRows;
		private ViewGroup mSlideoutMenuViewSmallRows;
		
		private ViewGroup mSortHearder;
		private ViewGroup mViewHeader;
		
		public void setupSlideoutMenu ()
		{
			mSlideoutMenuSearch = (ViewGroup) findViewById ( R.id.item_search );
			mSlideoutMenuNewFolder = (ViewGroup) findViewById ( R.id.item_new_folder );
			mSlideoutMenuSortBydate = (ViewGroup) findViewById ( R.id.item_sort_by_date );
			mSlideoutMenuSortAlphabetically = (ViewGroup) findViewById ( R.id.item_sort_alphabetically );
			mSlideoutMenuSortBytype = (ViewGroup) findViewById ( R.id.item_sort_by_type );
			mSlideoutMenuViewBigRows = (ViewGroup) findViewById ( R.id.item_view_big_rows );
			mSlideoutMenuViewMediumRows = (ViewGroup) findViewById ( R.id.item_view_medium_rows );
			mSlideoutMenuViewSmallRows = (ViewGroup) findViewById ( R.id.item_view_small_rows );
			mSortHearder = (ViewGroup) findViewById(R.id.item_header_sort);
			mViewHeader = (ViewGroup) findViewById(R.id.item_header_view);
			
			setIcons();
			setTexts();
			setActionListeners();
		}
		
		private void setIcons ()
		{
			ImageView cellImage;
			
			cellImage = (ImageView) mSlideoutMenuNewFolder.findViewById(R.id.icon);
			cellImage.setImageResource(R.drawable.search_64);
			
			cellImage = (ImageView) mSlideoutMenuNewFolder.findViewById(R.id.icon);
			cellImage.setImageResource(R.drawable.new_folder_64);
			
			cellImage = (ImageView) mSlideoutMenuSortBydate.findViewById(R.id.icon);
			cellImage.setImageResource(R.drawable.calendar_56);
			
			cellImage = (ImageView) mSlideoutMenuSortAlphabetically.findViewById(R.id.icon);
			cellImage.setImageResource(R.drawable.type_56);
			
			cellImage = (ImageView) mSlideoutMenuSortBytype.findViewById(R.id.icon);
			cellImage.setImageResource(R.drawable.type_56);
			
			cellImage = (ImageView) mSlideoutMenuViewBigRows.findViewById(R.id.icon);
			cellImage.setImageResource(R.drawable.note_48);
			
			cellImage = (ImageView) mSlideoutMenuViewMediumRows.findViewById(R.id.icon);
			cellImage.setImageResource(R.drawable.note_40);
			
			cellImage = (ImageView) mSlideoutMenuViewSmallRows.findViewById(R.id.icon);
			cellImage.setImageResource(R.drawable.note_32);
		}
		
		private void setTexts ()
		{
			TextView cellText;
			
			cellText = (TextView) mSlideoutMenuNewFolder.findViewById(R.id.text);
			cellText.setText("New Folder");
			
			cellText = (TextView) mSlideoutMenuSearch.findViewById(R.id.text);
			cellText.setText("Search");
			
			cellText = (TextView) mSortHearder.findViewById(R.id.text);
			cellText.setText("Sort");
			
			cellText = (TextView) mSlideoutMenuSortAlphabetically.findViewById(R.id.text);
			cellText.setText("Alphabetically");
			
			cellText = (TextView) mSlideoutMenuSortBydate.findViewById(R.id.text);
			cellText.setText("By Date");
			
			cellText = (TextView) mSlideoutMenuSortBytype.findViewById(R.id.text);
			cellText.setText("By Type");
			
			cellText = (TextView) mViewHeader.findViewById(R.id.text);
			cellText.setText("View");
			
			cellText = (TextView) mSlideoutMenuViewBigRows.findViewById(R.id.text);
			cellText.setText("Big Rows");
			
			cellText = (TextView) mSlideoutMenuViewMediumRows.findViewById(R.id.text);
			cellText.setText("Medium Rows");
			
			cellText = (TextView) mSlideoutMenuViewSmallRows.findViewById(R.id.text);
			cellText.setText("Small Rows");
		}
		
		private void setActionListeners ()
		{
			mSlideoutMenuSearch.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onSearch (); } } );

			mSlideoutMenuNewFolder.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onNewFolder (); } } );

			mSlideoutMenuSortBydate.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onSort (); } } );

			mSlideoutMenuSortAlphabetically.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onSort (); } } );

			mSlideoutMenuSortBytype.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onSort (); } } );

			mSlideoutMenuViewBigRows.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onChangeItemSize (); } } );

			mSlideoutMenuViewMediumRows.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onChangeItemSize (); } } );

			mSlideoutMenuViewSmallRows.setOnClickListener ( new OnClickListener ()
			{ @Override public void onClick ( View v ) { mPresenter.onChangeItemSize (); } } );
		}
	}
}