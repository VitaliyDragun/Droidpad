package views;

import vitaliy.dragun.droidpad_2nd_edition.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DialogChangePassword extends DialogPreference
{
	private EditText currentPassword = null;
	private EditText newPassword = null;
	private EditText confirmNewPassword = null;
	
	private Toast toast;
	
	private final long requiredPasswordLength = 4;
	
	public DialogChangePassword(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		setDialogLayoutResource(R.layout.dialog_change_password);
	}
	
	public DialogChangePassword(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		setDialogLayoutResource(R.layout.dialog_change_password);
	}
	
	protected void showDialog(Bundle state)
	{
		super.showDialog(state);
		
		//Initialize toast object
		toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, -150);
		
		//Disable positive button
		((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		
		//Disable these fields
		newPassword.setEnabled(false);
		confirmNewPassword.setEnabled(false);
	}
	
	protected View onCreateDialogView()
	{
		View root = super.onCreateDialogView();
		
		currentPassword = (EditText)root.findViewById(R.id.current_password_edittext);
		newPassword = (EditText)root.findViewById(R.id.new_password_edittext);
		newPassword.setText("");
		confirmNewPassword = (EditText)root.findViewById(R.id.confirm_newpassword_edittext);
		
		//Setting TextWatchers for all three editTexts
		currentPassword.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
				
				//If password is of required length we check it
				if(currentPassword.getText().toString().length() == requiredPasswordLength){
					
					//If it's correct, enable next field and disable this field
					if(currentPassword.getText().toString().equals(preferences.getString("Password", "1111"))){
						newPassword.setEnabled(true);
						newPassword.requestFocus();
						currentPassword.setEnabled(false);
						
						//Show notification toast
						toast.setText("Correct password");
						
						//Set text size
						float textSize = 21.0f;
						((TextView)toast.getView().findViewById(android.R.id.message)).
								setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
						
						toast.show();
					}
					//Else raising explanation toast
					else{
						
						toast.setText("Wrong password");
						
						//Set text size
						float textSize = 21.0f;
						((TextView)toast.getView().findViewById(android.R.id.message)).
								setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
						
						toast.show();
					}
				}
				
			}
			
		});
		
		newPassword.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				int length = newPassword.getText().toString().length();
				if(length == requiredPasswordLength){
					
					//ConfirmNewPassword field becomes active
					confirmNewPassword.setEnabled(true);
					confirmNewPassword.requestFocus();
					
					newPassword.setEnabled(false);
				}
			}
		});
		
		confirmNewPassword.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				//If password is of required length
				if(confirmNewPassword.getText().toString().length() == requiredPasswordLength){
					
					//Check if both fields contain the same password
					if(confirmNewPassword.getText().toString().equals(newPassword.getText().toString()))
						
						//If so enable dialog positive button
						((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
					else{
						
						//Clear text in both new and confirm new password fields
						newPassword.setText("");
						confirmNewPassword.setText("");
						
						newPassword.setEnabled(true);
						newPassword.requestFocus();
						
						confirmNewPassword.setEnabled(false);
						
						//Raise the warning toast
						Toast toast = Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, -150);
						//Set text size
						float textSize = 21.0f;
						((TextView)toast.getView().findViewById(android.R.id.message)).
								setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
						
						toast.show();
					}
				}
			}
			
		});
		
		return root;
	}
	
	public void onClick(DialogInterface dialog, int which){
		
		switch(which){
		
		//Remember new password
		case DialogInterface.BUTTON_POSITIVE:
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			Editor editor = preferences.edit();
			editor.putString("Password", newPassword.getText().toString());
			editor.commit();
			
			//Raise notification toast
			Toast toast = Toast.makeText(getContext(), "Password was changed", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			//Set text size
			float textSize = 21.0f;
			((TextView)toast.getView().findViewById(android.R.id.message)).
					setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			
			toast.show();
			break;
			
		case DialogInterface.BUTTON_NEGATIVE:
			break;
		}
		
		super.onClick(dialog, which);
	}
}