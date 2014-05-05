package activities;

import vitaliy.dragun.droidpad_2nd_edition.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity
{
	private static final String TAB_CREATE_NOTE = "create note";
	private static final String TAB_ITEM_LIST = "item list";
	private static final String TAB_PREFERENCES = "preferences";
	private static final String TAB_HELP = "help";
	private static final String TAB_EXIT = "exit";

	private TabHost tabHost;

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);

		setContentView (R.layout.activity_main_tabs);

		setupTabs();
	}

	private void setupTabs ()
	{
		tabHost = getTabHost ();
		tabHost.setOnTabChangedListener ( new TabChangedListener() );

		tabHost.getTabWidget ().setDividerDrawable (R.drawable.tabs_divider);

		TabSpec newNoteSpec = tabHost.newTabSpec (TAB_CREATE_NOTE);
		View newNoteView = getLayoutInflater().inflate(R.layout.tab_view, null);
		((ImageView)newNoteView.findViewById(R.id.imageview_icon)).setImageResource(R.drawable.plus_1);
		newNoteSpec.setIndicator(newNoteView);
		Intent newNoteIntent = new Intent(this, CreateNoteActivity.class);
		newNoteSpec.setContent(newNoteIntent);

		TabSpec manageSpec = tabHost.newTabSpec (TAB_ITEM_LIST);
		View noteListView = getLayoutInflater().inflate(R.layout.tab_view, null);
		((ImageView)noteListView.findViewById(R.id.imageview_icon)).setImageResource(R.drawable.pages);
		manageSpec.setIndicator(noteListView);
		Intent manageIntent = new Intent(this, ItemsActivity.class);
		manageSpec.setContent(manageIntent);

		TabSpec preferencesSpec = tabHost.newTabSpec (TAB_PREFERENCES);
		View settingsView = getLayoutInflater().inflate(R.layout.tab_view, null);
		((ImageView)settingsView.findViewById(R.id.imageview_icon)).setImageResource(R.drawable.gear);
		preferencesSpec.setIndicator(settingsView);
		Intent preferencesIntent = new Intent(this, PreferencesActivity.class);
		preferencesSpec.setContent(preferencesIntent);

		TabSpec helpSpec = tabHost.newTabSpec (TAB_HELP);
		View helpView = getLayoutInflater().inflate(R.layout.tab_view, null);
		((ImageView)helpView.findViewById(R.id.imageview_icon)).setImageResource(R.drawable.question_sign);
		helpSpec.setIndicator(helpView);
		Intent helpIntent = new Intent(this, HelpActivity.class);
		helpSpec.setContent(helpIntent);

		TabSpec exitSpec = tabHost.newTabSpec (TAB_EXIT);
		View exitView = getLayoutInflater().inflate(R.layout.tab_view, null);
		((ImageView)exitView.findViewById(R.id.imageview_icon)).setImageResource(R.drawable.power_button_big);
		exitSpec.setIndicator (exitView);
		Intent exitIntent = new Intent (this, ExitActivity.class);
		exitSpec.setContent (exitIntent);

		tabHost.addTab(newNoteSpec);
		tabHost.addTab(manageSpec); 
		tabHost.addTab(preferencesSpec); 
		tabHost.addTab(helpSpec);
		tabHost.addTab(exitSpec);

		tabHost.getTabWidget ().setStripEnabled (true);
	}
	
	public void chooseTab (int tabIndex)
	{
		tabHost.setCurrentTab(tabIndex);
	}
	
	public void setTabBarVisible (boolean toggle)
	{
		if (toggle == true)
			tabHost.getTabWidget ().setVisibility (View.VISIBLE);
		else
			tabHost.getTabWidget ().setVisibility (View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}

	private class TabChangedListener implements OnTabChangeListener
	{
		@Override
		public void onTabChanged(String tabId)
		{
			if ( tabId.equals(TAB_CREATE_NOTE) )
				tabHost.getTabWidget ().setVisibility (View.GONE);
		}

	}
}
