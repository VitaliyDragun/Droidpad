package activities;

import java.util.ArrayList;

import vitaliy.dragun.droidpad_2nd_edition.R;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class HelpActivity extends Activity {

	private ExpandableListView mExpandableList;
	
	Toast toast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		//Initialize the toast
		toast = Toast.makeText(this, "", Toast.LENGTH_LONG);

		mExpandableList = (ExpandableListView)findViewById(R.id.help_list);
		 
        ArrayList<String> arrayParents = new ArrayList<String>();
        //ArrayList<String> arrayChildren = new ArrayList<String>();
 
        arrayParents.add("Instructions ");
        arrayParents.add("Version Log ");
        arrayParents.add("About ");

        //sets the adapter that provides data to the list.
        mExpandableList.setAdapter(new MyCustomAdapter(HelpActivity.this,arrayParents));
	}
	
	public void onResume(){
		super.onResume();

		
		//Expand "about" row in expandable list
		mExpandableList.expandGroup(2);
	}

	// Menu call if disabled
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_help, menu);
		return true;
	}
	
	//Show notification toast
	public void onBackPressed(){
		
		toast.setText("Press the power tab on the bar below to exit".toString());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public class MyCustomAdapter extends BaseExpandableListAdapter {
		 
		private int lastExpandedGroupPosition; 
	    private LayoutInflater inflater;
	    private ArrayList<String> mParent;
	 
	    public MyCustomAdapter(Context context, ArrayList<String> parent){
	        mParent = parent;
	        inflater = LayoutInflater.from(context);
	    }
	 
	 
	    @Override
	    //counts the number of group/parent items so the list knows how many times calls getGroupView() method
	    public int getGroupCount() {
	       // return mParent.size();
	    	return 3;
	    }
	 
	    @Override
	    //counts the number of children items so the list knows how many times calls getChildView() method
	    public int getChildrenCount(int i) {
	       //return mParent.get(i).getArrayChildren().size();
	    	return 1;
	    }
	 
	    @Override
	    //gets the title of each parent/group
	    public Object getGroup(int i) {
	        return mParent.get(i);//.getTitle();
	    }
	 
	    @Override
	    //gets the name of each item
	    public Object getChild(int i, int i1) {
	        return mParent.get(i);//.getArrayChildren().get(i1);
	    }
	 
	    @Override
	    public long getGroupId(int i) {
	        return i;
	    }
	 
	    @Override
	    public long getChildId(int i, int i1) {
	        return i1;
	    }
	 
	    @Override
	    public boolean hasStableIds() {
	        return true;
	    }
	 
	    @Override
	    //in this method you must set the text to see the parent/group on the list
	    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
	 
	        view = inflater.inflate(R.layout.expandable_list_parent_item, viewGroup,false);
	 
	        TextView textView = (TextView) view.findViewById(R.id.text_row_title);
	        //"i" is the position of the parent/group in the list
	        textView.setText(getGroup(i).toString());
	 
	        //return the entire view
	        return view;
	    }
	 
	    @Override
	    //in this method you must set the text to see the children on the list
	    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
	    	
	    	if(i == 0){
	    		view = inflater.inflate(R.layout.expandable_list_item_instructions, viewGroup,false);
	    		return view;
	    	}
	    	
	    	if(i == 1){
	    		view = inflater.inflate(R.layout.expandable_list_item_version_log, viewGroup, false);
	    		return view;
	    	}
	    	
	    	if(i == 2){
	    		view = inflater.inflate(R.layout.expandable_list_item_about, viewGroup, false);
	    		return view;
	    	}

	    	return null;
	    }
	    
	    @Override
	    public void onGroupExpanded(int groupPosition){
	        //collapse the old expanded group, if not the same
	        //as new group to expand
	        if(groupPosition != lastExpandedGroupPosition){
	        	mExpandableList.collapseGroup(lastExpandedGroupPosition);
	        }

	        super.onGroupExpanded(groupPosition);           
	        lastExpandedGroupPosition = groupPosition;
	    }
	 
	    @Override
	    public boolean isChildSelectable(int i, int i1) {
	        return true;
	    }
	 
	    @Override
	    public void registerDataSetObserver(DataSetObserver observer) {
	        /* used to make the notifyDataSetChanged() method work */
	        super.registerDataSetObserver(observer);
	    }
	}

}