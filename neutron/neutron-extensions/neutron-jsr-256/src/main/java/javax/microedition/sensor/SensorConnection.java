package javax.microedition.sensor;

import javax.microedition.io.Connection;
import java.io.IOException;

public interface SensorConnection extends Connection {
    public static final int STATE_CLOSED = 1;
    public static final int STATE_LISTENING = 2;
    public static final int STATE_OPENED = 3;

    Channel getChannel(ChannelInfo channelInfo);
    Data[] getData(int bufferSize) throws IOException;
    Data[] getData(int bufferSize, long bufferingPeriod, boolean isTimestampIncluded, boolean isUncertaintyIncluded, boolean isValidityIncluded) throws IOException;
    Data[] getRoundtripData(int bufferSize) throws IOException;
    Data[] getRoundtripData(int bufferSize, long bufferingPeriod, boolean isTimestampIncluded, boolean isUncertaintyIncluded, boolean isValidityIncluded) throws IOException;
    SensorInfo getSensorInfo();
    int getState();
    void removeSensorListener();
    void setDataListener(SensorListener listener, int bufferSize);
    void setDataListener(SensorListener listener, int bufferSize, long bufferingPeriod, boolean isTimestampIncluded, boolean isUncertaintyIncluded, boolean isValidityIncluded);
}

