package custom_views;

import interfaces.DialogActionsListener;
import presenters.CreateBackupPresenter;
import presenters.CreateBackupPresenter.ICreateBackupView;
import vitaliy.dragun.droidpad_2nd_edition.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DialogCreateBackup extends DialogPreference implements ICreateBackupView
{
	private CreateBackupPresenter mPresenter;

	public DialogCreateBackup(Context context, AttributeSet attrs, int defTheme)
	{
		super(context, attrs, defTheme);
		setDialogLayoutResource(R.layout.dialog_backup);
	}
	
	public DialogCreateBackup(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setDialogLayoutResource(R.layout.dialog_backup);
	}
	
	public View onCreateDialogView()
	{
		mPresenter = new CreateBackupPresenter(this);
		return super.onCreateDialogView();
	}
	
	public void onClick(DialogInterface dialog, int which)
	{
		switch(which)
		{
		case DialogInterface.BUTTON_POSITIVE:
			mPresenter.createBackupButtonPressed();
		}
		
		super.onClick(dialog, which);
	}
	
	public void showPasswordDialog(final DialogActionsListener dialogListener)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle("EnterPassword".toString());
		dialogBuilder.setIcon(R.drawable.key);
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		final View view = inflater.inflate(R.layout.dialog_enter_password, null);
		dialogBuilder.setView(view);
		
		dialogBuilder.setPositiveButton("OK".toString(), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String enteredPassword = ((EditText) view.findViewById(R.id.enter_password_edittext)).getText().toString();
				dialogListener.confirmAction(enteredPassword);
			}
		});
		dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) { dialogListener.cancelAction(); }
		});
		
		AlertDialog passwordDialog = dialogBuilder.create();
		passwordDialog.show();

		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}
	
	public void showMessage (String message)
	{
		Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
		
		toast.setGravity(Gravity.CENTER, 0, 0);
		float textSize = 14.0f;
		((TextView)toast.getView().findViewById(android.R.id.message)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);

		toast.show();
	}
}
