package views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class CustomEditText extends EditText
{
    private Rect mRect;
    private Paint mPaint;

    private boolean mDrawLines = true;
    
    private CustomEditTextDelegate mDelegate;

    public CustomEditText(Context context) { super (context); }

    public CustomEditText(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	mRect = new Rect();
	mPaint = new Paint();
	mPaint.setStyle(Paint.Style.STROKE);
	mPaint.setColor(Color.parseColor("#000000"));
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) 
	    mDelegate.backPressed();

	return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
	if (mDrawLines == true)
	{
	    int lineHeight = this.getLineHeight();
	    int lineCount = this.getLineCount() + 20;
	    int height = lineHeight * lineCount;
	    int curHeight = 0;
	    Rect r = mRect;
	    Paint paint = mPaint;

	    int baseline = getLineBounds(0, r);
	    for (curHeight = baseline + 1; curHeight < height; curHeight += getLineHeight())
		canvas.drawLine(r.left, curHeight, r.right, curHeight, paint);
	}
	
	super.onDraw(canvas);
    }
    
    public void setDelegate (CustomEditTextDelegate delegate) { mDelegate = delegate; }

    public void enableLines (boolean drawLines) { mDrawLines = drawLines; }
    
    public interface CustomEditTextDelegate
    {
	public void backPressed ();
    }
}
