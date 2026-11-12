package javax.microedition.sensor;

public interface Condition {
    boolean isMet(double value);
    boolean isMet(Object value);
}

