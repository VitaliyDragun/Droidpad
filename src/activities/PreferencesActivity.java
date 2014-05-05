package activities;

import vitaliy.dragun.droidpad_2nd_edition.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;

public class PreferencesActivity extends PreferenceActivity
{
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.activity_preferences);
		getListView().setCacheColorHint(0);
		setTheme(R.style.SettingsTheme);
	}
}
