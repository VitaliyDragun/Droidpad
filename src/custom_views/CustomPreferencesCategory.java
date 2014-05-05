package custom_views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CustomPreferencesCategory extends PreferenceCategory {

	public CustomPreferencesCategory(Context context) { super(context); }

	public CustomPreferencesCategory(Context context, AttributeSet attrs) { super(context, attrs); }

	public CustomPreferencesCategory(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

	/**
	 * We catch the view after its creation, and before the activity will use it, in order to make our changes
	 * @param parent
	 * @return
	 */
	
	@Override
	protected View onCreateView(ViewGroup parent) 
	{
		// And it's just a TextView!
		TextView categoryTitle =  (TextView)super.onCreateView(parent);
		categoryTitle.setBackgroundColor(Color.parseColor("#002545"));
		categoryTitle.setTextColor(Color.WHITE);

		//Convert dip pixels into normal
		Resources r = getContext().getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, r.getDisplayMetrics());

		categoryTitle.setPadding((int)px, 0, 0, 0);

		return categoryTitle;
	}
}
