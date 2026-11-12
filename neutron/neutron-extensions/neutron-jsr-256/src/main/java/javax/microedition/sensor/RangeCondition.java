package javax.microedition.sensor;

public class RangeCondition implements Condition {
    public static final String OP_INSIDE = "INSIDE";
    public static final String OP_OUTSIDE = "OUTSIDE";

    private final double lowerLimit;
    private final double upperLimit;
    private final String operator;

    public RangeCondition(double lowerLimit, double upperLimit, String operator) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.operator = operator;
    }

    public double getLowerLimit() { return lowerLimit; }
    public double getUpperLimit() { return upperLimit; }
    public String getOperator() { return operator; }

    @Override
    public boolean isMet(double value) {
        boolean inside = (value >= lowerLimit && value <= upperLimit);
        if (OP_INSIDE.equals(operator)) return inside;
        if (OP_OUTSIDE.equals(operator)) return !inside;
        return false;
    }

    @Override
    public boolean isMet(Object value) {
        if (value instanceof Number) {
            return isMet(((Number) value).doubleValue());
        }
        return false;
    }
}

