package javax.microedition.content;

public class ActionNameMap {
    private final String[] actions;
    private final String[] actionNames;
    private final String locale;

    public ActionNameMap(String[] actions, String[] actionNames, String locale) {
        this.actions = actions != null ? (String[]) actions.clone() : new String[0];
        this.actionNames = actionNames != null ? (String[]) actionNames.clone() : new String[0];
        this.locale = locale;
    }

    public String getActionName(String action) {
        if (action == null) return null;
        for (int i = 0; i < actions.length; i++) {
            if (action.equals(actions[i])) {
                return (i < actionNames.length) ? actionNames[i] : action;
            }
        }
        return null;
    }

    public String[] getActions() {
        return (String[]) actions.clone();
    }

    public String getLocale() {
        return locale;
    }
}

