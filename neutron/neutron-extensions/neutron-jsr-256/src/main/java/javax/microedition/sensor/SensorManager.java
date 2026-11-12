package javax.microedition.sensor;

public class SensorManager {
    private static final SensorInfo ACCELEROMETER_INFO = new MockSensorInfo();

    public static void addSensorListener(SensorListener listener, SensorInfo info) {
    }

    public static void addSensorListener(SensorListener listener, String quantity) {
    }

    public static SensorInfo[] findSensors(String quantity, String contextType) {
        if ("acceleration".equalsIgnoreCase(quantity) || quantity == null) {
            return new SensorInfo[] { ACCELEROMETER_INFO };
        }
        return new SensorInfo[0];
    }

    public static SensorInfo[] findSensors(String url) {
        return new SensorInfo[] { ACCELEROMETER_INFO };
    }

    public static void removeSensorListener(SensorListener listener) {
    }

    private static class MockSensorInfo implements SensorInfo {
        private final ChannelInfo[] channels = new ChannelInfo[] {
            new MockChannelInfo("axis_x"),
            new MockChannelInfo("axis_y"),
            new MockChannelInfo("axis_z")
        };

        @Override
        public ChannelInfo[] getChannelInfos() { return channels; }

        @Override
        public int getConnectionType() { return CONN_EMBEDDED; }

        @Override
        public String getDescription() { return "Neutron 3-Axis Accelerometer"; }

        @Override
        public int getMaxBufferSize() { return 256; }

        @Override
        public String getModel() { return "Virtual-Sensor"; }

        @Override
        public Object getProperty(String name) { return null; }

        @Override
        public String[] getPropertyNames() { return new String[0]; }

        @Override
        public String getQuantity() { return "acceleration"; }

        @Override
        public String getUrl() { return "sensor:acceleration"; }

        @Override
        public boolean isAvailabilityPushSupported() { return false; }

        @Override
        public boolean isAvailable() { return true; }

        @Override
        public boolean isConditionPushSupported() { return false; }
    }

    private static class MockChannelInfo implements ChannelInfo {
        private final String name;
        private final MeasurementRange[] ranges = new MeasurementRange[] {
            new MeasurementRange(-19.6, 19.6, 0.01)
        };

        MockChannelInfo(String name) { this.name = name; }

        @Override
        public int getAccuracy() { return 1; }

        @Override
        public int getDataType() { return TYPE_DOUBLE; }

        @Override
        public MeasurementRange[] getMeasurementRanges() { return ranges; }

        @Override
        public String getName() { return name; }

        @Override
        public float getScale() { return 0; }

        @Override
        public Unit getUnit() { return Unit.getUnit("m/s^2"); }
    }
}

