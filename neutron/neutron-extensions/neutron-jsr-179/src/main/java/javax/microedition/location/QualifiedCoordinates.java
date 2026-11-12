package javax.microedition.location;

public class QualifiedCoordinates extends Coordinates {
    private float horizontalAccuracy;
    private float verticalAccuracy;

    public QualifiedCoordinates(double latitude, double longitude, float altitude, float horizontalAccuracy, float verticalAccuracy) {
        super(latitude, longitude, altitude);
        this.horizontalAccuracy = horizontalAccuracy;
        this.verticalAccuracy = verticalAccuracy;
    }

    public float getHorizontalAccuracy() { return horizontalAccuracy; }
    public void setHorizontalAccuracy(float horizontalAccuracy) { this.horizontalAccuracy = horizontalAccuracy; }

    public float getVerticalAccuracy() { return verticalAccuracy; }
    public void setVerticalAccuracy(float verticalAccuracy) { this.verticalAccuracy = verticalAccuracy; }
}

