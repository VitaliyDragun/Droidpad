package logic;

import vitaliy.dragun.droidpad_2nd_edition.Colors;
import vitaliy.dragun.droidpad_2nd_edition.MyApplication;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

public class NoteAppearance implements Colors
{
    private static final String FULLSCREEN_MODE = "FullScreen mode";
    private static final String FONT_TYPE = "font type";
    private static final String FONT_STYLE = "font style";
    private static final String FONT_COLOR = "Font color";
    private static final String TEXT_SIZE = "Text size";
    private static final String BACKGROUN_COLOR = "Background color";
    
    private static final String FONT_TYPE_DEFAULT = "default";
    private static final String FONT_TYPE_MONOSPACE = "monospace";
    private static final String FONT_TYPE_SERIF = "serif";
    
    private Typeface fontType;
    private int fontStyle;
    private String fontColor;
    private int fontSize;
    private String backgroundColor;
    private boolean linesEnabled;
    private boolean fullscreenMode;

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( MyApplication.getAppContext() );
    Editor editor = preferences.edit();

    public NoteAppearance()
    {
	fullscreenMode = preferences.getBoolean(FULLSCREEN_MODE, false);
	
	if ( preferences.getString (FONT_TYPE, FONT_TYPE_DEFAULT).equals ( FONT_TYPE_DEFAULT ) )
	    fontType = Typeface.DEFAULT;
	else if ( preferences.getString (FONT_TYPE, FONT_TYPE_DEFAULT).equals ( FONT_TYPE_MONOSPACE ) )
	    fontType = Typeface.MONOSPACE;
	else if ( preferences.getString (FONT_TYPE, FONT_TYPE_DEFAULT).equals ( FONT_TYPE_SERIF ) )
	    fontType = Typeface.SERIF;

	fontStyle = preferences.getInt(FONT_STYLE, Typeface.NORMAL);

	fontColor = preferences.getString(FONT_COLOR, blackColor);

	fontSize = (int) preferences.getFloat(TEXT_SIZE, 35.0f);

	backgroundColor = preferences.getString(BACKGROUN_COLOR, greyColor);
    }

    private static NoteAppearance instance;

    public static NoteAppearance getInstance()
    {
	if(instance == null)
	{
	    instance = new NoteAppearance();
	}

	return instance;
    }

    public final Typeface getFontType()
    {
	return fontType;
    }

    public int getFontStyle()
    {
	return fontStyle;
    }

    public String getFontColor()
    {
	return fontColor;
    }

    public int getFontSize()
    {
	return fontSize;
    }

    public String getBackgroundColor()
    {
	return backgroundColor;
    }

    public boolean isLinesEnabled() {
	return linesEnabled;
    }

    public boolean isFullscreenMode() {
	return fullscreenMode;
    }

    public void changeFontType()
    {
	if(fontType == Typeface.DEFAULT && fontStyle == Typeface.NORMAL)
	    fontStyle = Typeface.ITALIC;
	else if(fontType == Typeface.DEFAULT && fontStyle == Typeface.ITALIC)
	    fontStyle = Typeface.BOLD;
	else if(fontType == Typeface.DEFAULT && fontStyle == Typeface.BOLD)
	{
	    fontType = Typeface.MONOSPACE;
	    fontStyle = Typeface.NORMAL;
	}
	else if(fontType == Typeface.MONOSPACE && fontStyle == Typeface.NORMAL)
	    fontStyle = Typeface.ITALIC;
	else if(fontType == Typeface.MONOSPACE && fontStyle == Typeface.ITALIC)
	    fontStyle = Typeface.BOLD;
	else if(fontType == Typeface.MONOSPACE && fontStyle == Typeface.BOLD)
	{
	    fontType = Typeface.SERIF;
	    fontStyle = Typeface.NORMAL;
	}
	else if(fontType == Typeface.SERIF && fontStyle == Typeface.NORMAL)
	    fontStyle = Typeface.ITALIC;
	else if(fontType == Typeface.SERIF && fontStyle == Typeface.ITALIC)
	    fontStyle = Typeface.BOLD;
	else if(fontType == Typeface.SERIF && fontStyle == Typeface.BOLD)
	{
	    fontType = Typeface.DEFAULT;
	    fontStyle = Typeface.NORMAL;
	}

	editor.putString("Font name", fontType.toString());
	editor.putInt("Font style", fontStyle);

	editor.commit();;
    }

    public String toggleFontColor()
    {
	if (fontColor.equals(blackColor))
	    fontColor = blueColor;
	else if (fontColor.equals(blueColor))
	    fontColor = magentaColor;
	else if (fontColor.equals(magentaColor))
	    fontColor = whiteColor;
	else
	    fontColor = blackColor;

	editor.putString("Font color", fontColor);

	editor.commit();
	
	return fontColor;
    }

    public int increaseFontSize()
    {
	fontSize += 2;

	editor.putFloat("Text size", fontSize);
	editor.commit();
	
	return fontSize;
    }

    public int decreaseFontSize()
    {
	fontSize -= 2;

	editor.putFloat("Text size", fontSize);
	editor.commit();
	
	return fontSize;
    }

    public String toggleBackgroundColor()
    {
	if (backgroundColor.equals(greyColor))
	    backgroundColor = blackColor;
	else if (backgroundColor.equals(yellowColor))
	    backgroundColor = whiteColor;
	else if (backgroundColor.equals(whiteColor))
	    backgroundColor = greyColor;
	else if(backgroundColor.equals(blackColor))
	    backgroundColor = yellowColor;

	editor.putString("Background color", backgroundColor);
	editor.commit();
	
	return backgroundColor;
    }

    public boolean toggleLines()
    {
	if(linesEnabled)
	{
	    linesEnabled = false;

	    editor.putBoolean("Draw lines", false);
	}
	else
	{
	    linesEnabled = true;

	    editor.putBoolean("Draw lines", true);
	}

	editor.commit();
	
	return linesEnabled;
    }

    public boolean toggleFullscreenMode()
    {
	if(fullscreenMode)
	{
	    fullscreenMode = false;

	    editor.putBoolean("FullScreen mode", false);
	}
	else
	{
	    fullscreenMode = true;

	    editor.putBoolean("FullScreen mode", true);
	}

	editor.commit();
	
	return fullscreenMode;
    }
}
