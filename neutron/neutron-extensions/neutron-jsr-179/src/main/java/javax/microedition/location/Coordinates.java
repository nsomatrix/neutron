package javax.microedition.location;

public class Coordinates {
    public static final int DD_MM_SS = 1;
    public static final int DD_MM = 2;

    private double latitude;
    private double longitude;
    private float altitude;

    public Coordinates(double latitude, double longitude, float altitude) {
        setLatitude(latitude);
        setLongitude(longitude);
        setAltitude(altitude);
    }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) {
        if (Double.isNaN(latitude) || latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException();
        }
        this.latitude = latitude;
    }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) {
        if (Double.isNaN(longitude) || longitude < -180.0 || longitude >= 180.0) {
            throw new IllegalArgumentException();
        }
        this.longitude = longitude;
    }

    public float getAltitude() { return altitude; }
    public void setAltitude(float altitude) { this.altitude = altitude; }

    public float distance(Coordinates to) {
        if (to == null) throw new NullPointerException();
        double lat1 = Math.toRadians(latitude);
        double lon1 = Math.toRadians(longitude);
        double lat2 = Math.toRadians(to.latitude);
        double lon2 = Math.toRadians(to.longitude);
        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (6371000 * c);
    }

    public float azimuthTo(Coordinates to) {
        if (to == null) throw new NullPointerException();
        double lat1 = Math.toRadians(latitude);
        double lon1 = Math.toRadians(longitude);
        double lat2 = Math.toRadians(to.latitude);
        double lon2 = Math.toRadians(to.longitude);
        double dlon = lon2 - lon1;
        double y = Math.sin(dlon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dlon);
        double az = Math.toDegrees(Math.atan2(y, x));
        return (float) ((az + 360) % 360);
    }
}

