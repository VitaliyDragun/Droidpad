package custom_views;

import presenters.ChangePasswordPresenter;
import presenters.ChangePasswordPresenter.IChangePasswordView;
import vitaliy.dragun.droidpad_2nd_edition.R;
import android.annotation.SuppressLint;
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

@SuppressLint("ShowToast")
public class DialogChangePassword extends DialogPreference implements IChangePasswordView
{
	private ChangePasswordPresenter mPresenter;
	
	private View mDialogView;
	
	private EditText mEditTextCurrentPassword;
	private EditText mEditTextNewPassword;
	private EditText mEditTextConfirmNewPassword;
	
	private Toast mToast;
	
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
	
	@Override
	protected void showDialog(Bundle state)
	{
		super.showDialog(state);
		
		mPresenter = new ChangePasswordPresenter(this);
		
		setupDialog();
	}
	
	private void setupDialog ()
	{
		mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.CENTER, 0, -150);
		
		((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		
		mEditTextNewPassword.setEnabled(false);
		mEditTextConfirmNewPassword.setEnabled(false);
	}
	
	@Override
	protected View onCreateDialogView()
	{
		mDialogView = super.onCreateDialogView();
		
		mEditTextCurrentPassword = (EditText)mDialogView.findViewById(R.id.edittext_current_password);
		mEditTextNewPassword = (EditText)mDialogView.findViewById(R.id.edittext_new_password);
		mEditTextNewPassword.setText("");
		mEditTextConfirmNewPassword = (EditText)mDialogView.findViewById(R.id.edittext_confirm_new_password);

		mEditTextCurrentPassword.addTextChangedListener(new TextWatcher()
		{
			@Override public void afterTextChanged(Editable arg0) {}
			@Override public void beforeTextChanged(CharSequence text, int arg1, int arg2, int arg3) {}
			@Override public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) 
			{
				mPresenter.currentPasswordTextChanged (text.toString());
			}
			
		});
		
		mEditTextNewPassword.addTextChangedListener(new TextWatcher(){

			@Override public void afterTextChanged(Editable arg0) {}
			@Override public void beforeTextChanged(CharSequence text, int arg1, int arg2, int arg3) {}
			@Override public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3)
			{
				mPresenter.newPasswordTextChanged (text.toString());
			}
		});
		
		mEditTextConfirmNewPassword.addTextChangedListener(new TextWatcher(){

			@Override public void afterTextChanged(Editable s) {}
			@Override public void beforeTextChanged(CharSequence text, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence text, int start, int before, int count)
			{
				mPresenter.confirmNewPasswordTextChanged(text.toString());
			}
		});
		
		return mDialogView;
	}
	
	public void onClick(DialogInterface dialog, int which){
		
		switch(which)
		{
		case DialogInterface.BUTTON_POSITIVE:
			mPresenter.positiveButtonPressed();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			break;
		}
		
		super.onClick(dialog, which);
	}
	
	public void switchToNewPassword ()
	{
		mEditTextNewPassword.setEnabled(true);
		mEditTextNewPassword.requestFocus();
		mEditTextCurrentPassword.setEnabled(false);
	}
	
	public void switchToConfirmNewPassword ()
	{
		mEditTextConfirmNewPassword.setEnabled(true);
		mEditTextConfirmNewPassword.requestFocus();
		
		mEditTextNewPassword.setEnabled(false);
	}
	
	public void showMessage (String message)
	{
		Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);

		float textSize = 21.0f;
		((TextView)toast.getView().findViewById(android.R.id.message)).
				setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		
		toast.show();
	}
	
	public void enablePositiveButton ()
	{
		((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
	}
	
	public void switchBackToNewPassword ()
	{
		mEditTextNewPassword.setText("");
		mEditTextConfirmNewPassword.setText("");
		
		mEditTextNewPassword.setEnabled(true);
		mEditTextNewPassword.requestFocus();
		
		mEditTextConfirmNewPassword.setEnabled(false);
	}
}