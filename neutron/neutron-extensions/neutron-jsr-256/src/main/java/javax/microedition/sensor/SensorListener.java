package javax.microedition.sensor;

public interface SensorListener {
    void dataReceived(SensorConnection sensor, Data[] data, boolean isBufferOverflow);
    void sensorErrorReceived(SensorConnection sensor, int error, long timestamp);
}

