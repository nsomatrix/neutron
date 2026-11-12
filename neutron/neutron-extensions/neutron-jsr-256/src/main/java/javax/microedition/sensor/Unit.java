package javax.microedition.sensor;

public class Unit {
    private final String symbol;

    private Unit(String symbol) {
        this.symbol = symbol;
    }

    public static Unit getUnit(String symbol) {
        return new Unit(symbol != null ? symbol : "");
    }

    @Override
    public String toString() {
        return symbol;
    }
}

