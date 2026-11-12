package javax.microedition.content;

public interface ContentHandler {
    String getID();
    String getType(int index);
    int getTypeCount();
    String getSuffix(int index);
    int getSuffixCount();
    String[] getActions();
    String getActionName(String action);
    ActionNameMap getActionNameMap();
    String getAppName();
    String getVersion();
    String getAuthority();
}

