package database;

public class Item
{
    //public enum Priority {NORMAL, HIGH, VERY_HIGH};
    
    public static final int PRIORITY_NORMAL = 0;
    public static final int PRIORITY_HIGHT = 1;
    public static final int PRIORITY_VERY_HIGH = 2;

    public enum Type {NOTE, FOLDER};

    public Type type;
    public long id;
    public String date;
    public String title;
    public int isTitleAdded = -1;
    public int priority = PRIORITY_NORMAL;
    public int isProtected = -1;
    public String locationFolder;
}
