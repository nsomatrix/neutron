package javax.microedition.sensor;

public interface ChannelInfo {
    public static final int TYPE_DOUBLE = 1;
    public static final int TYPE_INT = 2;

    int getAccuracy();
    int getDataType();
    MeasurementRange[] getMeasurementRanges();
    String getName();
    float getScale();
    Unit getUnit();
}

