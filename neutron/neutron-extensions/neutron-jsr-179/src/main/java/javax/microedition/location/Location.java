package javax.microedition.location;

public class Location {
    public static final int MTE_SATELLITE = 1;
    public static final int MTE_CELLID = 2;
    public static final int MTE_SHORT_RANGE = 4;
    public static final int MTE_ANGLE_OF_ARRIVAL = 8;
    public static final int MTE_TIME_DIFFERENCE = 16;
    public static final int MTE_TIMEOFARRIVAL = 32;
    public static final int MTE_SHORTRANGE = 64;

    public static final int MTY_TERMINALBASED = 1;
    public static final int MTY_NETWORKBASED = 2;

    private QualifiedCoordinates coords;
    private float speed = 0f;
    private float course = 0f;
    private long timestamp = System.currentTimeMillis();
    private int locationMethod = MTE_SATELLITE | MTY_TERMINALBASED;
    private AddressInfo addressInfo = new AddressInfo();

    public Location(QualifiedCoordinates coords) {
        this.coords = coords;
    }

    public boolean isValid() { return coords != null; }
    public QualifiedCoordinates getQualifiedCoordinates() { return coords; }
    public float getSpeed() { return speed; }
    public float getCourse() { return course; }
    public long getTimestamp() { return timestamp; }
    public int getLocationMethod() { return locationMethod; }
    public AddressInfo getAddressInfo() { return addressInfo; }
}

