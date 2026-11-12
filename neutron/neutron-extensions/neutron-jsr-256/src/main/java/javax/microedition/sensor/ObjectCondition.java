package javax.microedition.sensor;

public class ObjectCondition implements Condition {
    private final Object target;

    public ObjectCondition(Object target) {
        this.target = target;
    }

    public Object getTarget() { return target; }

    @Override
    public boolean isMet(double value) {
        return false;
    }

    @Override
    public boolean isMet(Object value) {
        return target != null && target.equals(value);
    }
}

