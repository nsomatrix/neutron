package javax.microedition.sensor;

public interface Data {
    ChannelInfo getChannelInfo();
    double[] getDoubleValues();
    int[] getIntValues();
    Object[] getObjectValues();
    long getTimestamp(int index);
    float getUncertainty(int index);
    boolean isValid(int index);
}

