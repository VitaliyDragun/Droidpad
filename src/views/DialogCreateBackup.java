package views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import vitaliy.dragun.droidpad_2nd_edition.R;
import activities.MainActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DialogCreateBackup extends DialogPreference{

	public DialogCreateBackup(Context context, AttributeSet attrs, int defTheme){
		super(context, attrs, defTheme);
		setDialogLayoutResource(R.layout.dialog_backup);
	}
	public DialogCreateBackup(Context context, AttributeSet attrs){
		super(context, attrs);
		setDialogLayoutResource(R.layout.dialog_backup);
	}
	
	public View onCreateDialogView(){
		View root = super.onCreateDialogView();
		return root;
	}
	
	public void onClick(DialogInterface dialog, int which){
		switch(which){
		case DialogInterface.BUTTON_POSITIVE:
			if(storageIsAvailable())
				showPasswordDialog();
			else{
				
				//Show warning toast
				Toast toast = Toast.makeText(getContext(), "Storage unavailable", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				//Set text size
				float textSize = 14.0f;
				((TextView)toast.getView().findViewById(android.R.id.message)).
						setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
				
				toast.show();
			}
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			break;
		}
		
		super.onClick(dialog, which);
	}
	
	private boolean storageIsAvailable()
	{
		
		String storageState = Environment.getExternalStorageState();
		
		if(storageState.equals(Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	private void showPasswordDialog(){
		
		// Display the dialog where user has to enter the password in order to
		// proceed
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				getContext());
		dialogBuilder.setTitle("EnterPassword".toString());
		dialogBuilder.setIcon(R.drawable.key);
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		final View view = inflater.inflate(R.layout.dialog_enter_password, null);
		dialogBuilder.setView(view);
		
		dialogBuilder.setPositiveButton("OK".toString(), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

						String enteredPassword = ((EditText) view
								.findViewById(R.id.enter_password_edittext))
								.getText().toString();
						SharedPreferences preferences = PreferenceManager
								.getDefaultSharedPreferences(getContext());

						// Check if the password is correct
						if (preferences.getString("Password", "1111").equals(
								enteredPassword)){

							// Backup files
							backupFiles();
						}

						// Else issue info toast
						else {
							Toast incorrectPasswordToast = Toast.makeText(
									getContext(),
									"Incorrect password".toString(),
									Toast.LENGTH_SHORT);
							incorrectPasswordToast.setGravity(Gravity.CENTER,
									0, 0);
							//Set text size
							float textSize = 14.0f;
							((TextView)incorrectPasswordToast.getView().findViewById(android.R.id.message)).
									setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
							
							incorrectPasswordToast.show();
						}
			}
			
		});
		dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		AlertDialog passwordDialog = dialogBuilder.create();
		passwordDialog.show();
		
		//Show keyboard
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}
	
	private void backupFiles() {
		
		
	}
}
