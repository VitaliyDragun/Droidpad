package activities;

import vitaliy.dragun.droidpad_2nd_edition.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class ExitActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	//Menu call if disabled
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_exit, menu);
		return true;
	}
	
	public void onResume(){
		super.onResume();
		
		//Check whether to show confirm dialog
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		if(preferences.getBoolean("ask_before_exit", true)){
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			//Inflate dialog view
			LayoutInflater inflater = getLayoutInflater();
			View dialogView = inflater.inflate(R.layout.dialog_ask_before_exit, null);
			
			//Set listener on check box to know if it is on, if so, mark that preferences where updated
			CheckBox checkBox = (CheckBox)dialogView.findViewById(R.id.do_not_ask_again_checkbox);
			checkBox.setOnCheckedChangeListener(
					new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ExitActivity.this);
					Editor editor = preferences.edit();
					
					if(buttonView.isChecked()){
						
						//Remember the settings
						editor.putBoolean("ask_before_exit", false);
						editor.commit();
					}
					else
					{	
						//Remember the settings
						editor.putBoolean("ask_before_exit", true);
						editor.commit();
					}
				}
			});
			
			builder.setTitle("Are you sure?".toString());
			builder.setIcon(R.drawable.exit_icon_48);
			builder.setView(dialogView);
			builder.setPositiveButton("Yes".toString(), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					

					
					//Quit the application
					getParent().finish();
				}
			});
			builder.setNegativeButton("No".toString(), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			
			//Show dialog
			AlertDialog dialog = builder.create();
			//Make sure user can't dismiss it by pressing back button
			dialog.setCancelable(false); 
			dialog.show();
		}
		
		//If false just quit the application
		else{

			
			getParent().finish();
		}
	}

}