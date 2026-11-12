package javax.microedition.sensor;

public class MeasurementRange {
    private final double smallestValue;
    private final double largestValue;
    private final double resolution;

    public MeasurementRange(double smallestValue, double largestValue, double resolution) {
        this.smallestValue = smallestValue;
        this.largestValue = largestValue;
        this.resolution = resolution;
    }

    public double getSmallestValue() { return smallestValue; }
    public double getLargestValue() { return largestValue; }
    public double getResolution() { return resolution; }
}

