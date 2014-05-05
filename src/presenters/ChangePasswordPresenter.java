package presenters;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import logic.PreferencesManager;

public class ChangePasswordPresenter
{
	PreferencesManager mPreferences;
	
	IChangePasswordView mView;
	
	String mNewPassword;
	
	public ChangePasswordPresenter(IChangePasswordView view)
	{
		mView = view;
		mPreferences = PreferencesManager.getInstance();
	}
	
	public void currentPasswordTextChanged (String newText)
	{
		if(newText.length() == 4)
		{	
			if(mPreferences.checkPasswordValid(newText))
			{
				mView.switchToNewPassword();
				mView.showMessage("Password correct");
			}
			else
				mView.showMessage("Password incorrect");
		}
	}
	
	public void newPasswordTextChanged (String newText)
	{
		if(newText.length() == 4)
		{
			mView.switchToConfirmNewPassword();
			mNewPassword = newText;
		}
	}
	
	public void confirmNewPasswordTextChanged (String newText)
	{
		if(newText.length() == 4)
		{	
			if(newText.equals(mNewPassword))
				mView.enablePositiveButton();
			else
			{
				mView.switchBackToNewPassword();
				mView.showMessage("Passwords don't match");
			}
		}
	}
	
	public void positiveButtonPressed ()
	{
		mPreferences.setPassword(mNewPassword);
		mView.showMessage("Password was changed");
	}
	
	public interface IChangePasswordView
	{
		public void switchToNewPassword ();
		public void switchToConfirmNewPassword ();
		public void showMessage (String message);
		public void enablePositiveButton ();
		public void switchBackToNewPassword ();
	}
}
