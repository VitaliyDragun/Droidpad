package activities;

import vitaliy.dragun.droidpad_2nd_edition.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.preferences);
	setContentView(R.layout.activity_preferences);
	//getListView().setBackgroundColor(Color.BLACK);
	getListView().setCacheColorHint(0);
	setTheme(R.style.SettingsTheme);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.preferences, menu);
	return true;
    }

}
