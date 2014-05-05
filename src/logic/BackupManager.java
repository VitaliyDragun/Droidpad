package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class BackupManager 
{
	private ItemsManager mItemsManager;
	
	private static BackupManager self;
	
	private BackupManager() {}
	
	public static BackupManager getInstance ()
	{
		if (self == null)
			self = new BackupManager();
		return self;
	}
	
	public boolean isStorageAvailable()
	{
		String storageState = Environment.getExternalStorageState();
		if(storageState.equals(Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	public void createBackup() 
	{
		/*
		// Getting root folder
		File rootDirectory = Environment.getExternalStorageDirectory();

		// Creating main directory for our notes
		File myNotesBackupFolder = new File(rootDirectory.getAbsolutePath() + "/" + "Backup Droidpad");
		myNotesBackupFolder.mkdir();

		// If its not empty delete all the files and directories inside this folder
		if (myNotesBackupFolder.list().length != 0) {
			
			File[] rootFolderFiles = myNotesBackupFolder.listFiles();
			for (int outerIterator = 0; outerIterator < rootFolderFiles.length; outerIterator++) {
				
				if(rootFolderFiles[outerIterator].isFile())
					rootFolderFiles[outerIterator].delete();
				else{
					
					File[] innerFolderFiles = rootFolderFiles[outerIterator].listFiles();
					for(int innerIterator = 0; innerIterator < innerFolderFiles.length; innerIterator++){
						innerFolderFiles[innerIterator].delete();
					}
					
					rootFolderFiles[outerIterator].delete();
				}
			}
		}
		
		// Creating files and directories with the respect of existing notes and
		// directories
		for (int outerCounter = 0; outerCounter < MyApplication.app.getNotes().size(); outerCounter++)
		{
			// if entry belongs to root folder
			if (MyApplication.app.getNotes().get(outerCounter).getFolder()
					.equals(MyApplication.rootFolderTitle)) {

				// if entry is folder
				if (MyApplication.app.getNotes().get(outerCounter)
						.getIsFolder() == 1) {

					// The name of the current folder
					String folderName = MyApplication.app.getNotes()
							.get(outerCounter).getTitle();

					// Creating this folder
					File subFolder = new File(myNotesBackupFolder.getAbsolutePath()
							+ "/"
							+ MyApplication.app.getNotes().get(outerCounter)
									.getTitle());
					subFolder.mkdirs();

					for (int innerCounter = 0; innerCounter < MyApplication.app
							.getNotes().size(); innerCounter++) {

						// If note belongs to current folder then we create
						// respected file in this folder
						if (MyApplication.app.getNotes().get(innerCounter)
								.getFolder().equals(folderName)) {

							File note = new File(subFolder, MyApplication.app
									.getNotes().get(innerCounter).getTitle()
									+ ".txt");
							try {
								FileOutputStream stream = new FileOutputStream(
										note);
								PrintWriter writer = new PrintWriter(stream);

								// Writing data
								writer.println(MyApplication.app.getNotes()
										.get(innerCounter).getNote().toString());
								writer.println();
								writer.println(MyApplication.date);
								writer.println();
								writer.print(MyApplication.app.getNotes()
										.get(innerCounter).getDate().toString());
								/*
								writer.println();
								writer.print(("#$%Priority#$% " + MyApplication.app
										.getNotes().get(innerCounter)
										.getPriority()).toString());
								writer.println();
								writer.print(("#$%ProctectedByPassword#$% " + MyApplication.app
										.getNotes().get(innerCounter)
										.getPasswordProtected()).toString());
								*/
		/*
								writer.flush();
								writer.close();
								stream.close();
							} catch (FileNotFoundException e) {
								Log.i("Create backup", "File not found");
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}

				// entry is a note
				else {

					// Creating file
					File note = new File(myNotesBackupFolder, MyApplication.app
							.getNotes().get(outerCounter).getTitle()
							+ ".txt");
					try {
						FileOutputStream stream = new FileOutputStream(note);
						PrintWriter writer = new PrintWriter(stream);

						// Writing data
						writer.println(MyApplication.app.getNotes()
								.get(outerCounter).getNote().toString());
						writer.println();
						writer.println(MyApplication.date);
						writer.println();
						writer.print(MyApplication.app.getNotes()
								.get(outerCounter).getDate().toString());
						/*
						writer.println();
						writer.print(("#$%Priority#$% " + MyApplication.app
								.getNotes().get(outerCounter).getPriority())
								.toString());
						writer.println();
						writer.print(("#$%ProctectedByPassword#$% " + MyApplication.app
								.getNotes().get(outerCounter)
								.getPasswordProtected()).toString());
						*/
		/*
						writer.flush();
						writer.close();
						stream.close();
					} catch (FileNotFoundException e) {
						Log.i("Create backup", "File not found");
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		//Issue toast in the end
		Toast backupCreatedToast = Toast.makeText(
				getContext(),
				"Backup successfully created ".toString(),
				Toast.LENGTH_SHORT);
		backupCreatedToast.setGravity(Gravity.CENTER,
				0, 0);
		//Set text size
		float textSize = 14.0f;
		((TextView)backupCreatedToast.getView().findViewById(android.R.id.message)).
				setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
		
		
		backupCreatedToast.show();
		
		*/
	}
}
