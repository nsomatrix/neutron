package javax.microedition.sensor;

public interface Channel {
    void addCondition(ConditionListener listener, Condition condition);
    ChannelInfo getChannelInfo();
    String getChannelUrl();
    Condition[] getConditions(ConditionListener listener);
    void removeCondition(ConditionListener listener, Condition condition);
    void removeConditions(ConditionListener listener);
}

