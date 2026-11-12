package javax.microedition.sensor;

public class LimitCondition implements Condition {
    public static final String OP_EQUALS = "EQ";
    public static final String OP_GREATER_THAN = "GT";
    public static final String OP_GREATER_THAN_OR_EQUALS = "GE";
    public static final String OP_LESS_THAN = "LT";
    public static final String OP_LESS_THAN_OR_EQUALS = "LE";

    private final double limit;
    private final String operator;

    public LimitCondition(double limit, String operator) {
        this.limit = limit;
        this.operator = operator;
    }

    public double getLimit() { return limit; }
    public String getOperator() { return operator; }

    @Override
    public boolean isMet(double value) {
        if (OP_EQUALS.equals(operator)) return value == limit;
        if (OP_GREATER_THAN.equals(operator)) return value > limit;
        if (OP_GREATER_THAN_OR_EQUALS.equals(operator)) return value >= limit;
        if (OP_LESS_THAN.equals(operator)) return value < limit;
        if (OP_LESS_THAN_OR_EQUALS.equals(operator)) return value <= limit;
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

