package presenters;

import interfaces.DialogActionsListener;
import logic.BackupManager;
import logic.PreferencesManager;

public class CreateBackupPresenter 
{
	private ICreateBackupView mView;
	
	private PreferencesManager mPreferences;
	
	private BackupManager mBackupManager;
	
	public CreateBackupPresenter (ICreateBackupView view)
	{
		mView = view;
		
		mPreferences = PreferencesManager.getInstance();
		mBackupManager = BackupManager.getInstance();
	}
	
	public void createBackupButtonPressed ()
	{
		if (mBackupManager.isStorageAvailable())
			mView.showPasswordDialog(new DialogActionsListener()
			{
				@Override
				public void confirmAction(String message)
				{
					if (mPreferences.checkPasswordValid(message))
					{
						//mBackupManager.createBackup ();
						mView.showMessage("Backup created");
					}
					else
						mView.showMessage("Wrong password");
				}
				
				@Override public void cancelAction() {}
			});
		else
			mView.showMessage("Storage unavailable");
	}
	
	public interface ICreateBackupView
	{
		public void showPasswordDialog (DialogActionsListener dialogListener);
		public void showMessage (String message);
	}
}
