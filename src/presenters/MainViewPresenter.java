package presenters;

public class MainViewPresenter 
{
    private Listener mRootView;
    
    public MainViewPresenter (Listener rootView)
    {
	mRootView = rootView;
    }
    
    public interface Listener
    {
	
    }	    
}
