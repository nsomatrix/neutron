package javax.microedition.sensor;

public interface SensorInfo {
    public static final int CONN_EMBEDDED = 1;
    public static final int CONN_REMOTE = 2;
    public static final int CONN_WIRED = 3;
    public static final int CONN_SHORT_RANGE_WIRELESS = 4;

    public static final String PROP_IS_INTERRUPT = "isInterrupt";
    public static final String PROP_MAX_BUFFER_SIZE = "maxBufferSize";
    public static final String PROP_VENDOR = "vendor";
    public static final String PROP_VERSION = "version";

    ChannelInfo[] getChannelInfos();
    int getConnectionType();
    String getDescription();
    int getMaxBufferSize();
    String getModel();
    Object getProperty(String name);
    String[] getPropertyNames();
    String getQuantity();
    String getUrl();
    boolean isAvailabilityPushSupported();
    boolean isAvailable();
    boolean isConditionPushSupported();
}

