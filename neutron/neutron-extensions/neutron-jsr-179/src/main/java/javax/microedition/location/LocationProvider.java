package javax.microedition.location;

public abstract class LocationProvider {
    public static final int AVAILABLE = 1;
    public static final int TEMPORARILY_UNAVAILABLE = 2;
    public static final int OUT_OF_SERVICE = 3;

    protected LocationProvider() {}

    public static LocationProvider getInstance(Criteria criteria) throws LocationException {
        return new MockLocationProvider();
    }

    public static Location getLastKnownLocation() {
        return new Location(new QualifiedCoordinates(51.5074, -0.1278, 15f, 1f, 1f));
    }

    public abstract int getState();
    public abstract Location getLocation(int timeout) throws LocationException, InterruptedException;
    public abstract void setLocationListener(LocationListener listener, int interval, int timeout, int maxAge);
    public abstract void reset();

    private static class MockLocationProvider extends LocationProvider {
        private int state = AVAILABLE;
        private LocationListener listener;

        @Override
        public int getState() {
            return state;
        }

        @Override
        public Location getLocation(int timeout) throws LocationException, InterruptedException {
            return new Location(new QualifiedCoordinates(51.5074, -0.1278, 15f, 5f, 5f));
        }

        @Override
        public void setLocationListener(LocationListener listener, int interval, int timeout, int maxAge) {
            this.listener = listener;
            if (listener != null) {
                listener.locationUpdated(this, new Location(new QualifiedCoordinates(51.5074, -0.1278, 15f, 5f, 5f)));
            }
        }

        @Override
        public void reset() {
            this.state = AVAILABLE;
        }
    }
}

