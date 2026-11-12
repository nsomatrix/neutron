package javax.microedition.sensor;

public interface ConditionListener {
    void conditionMet(SensorConnection sensor, Data data, Condition condition);
}

