package javax.microedition.location;

public class Orientation {
    private float azimuth;
    private float pitch;
    private float roll;
    private boolean isMagnetic;

    public Orientation(float azimuth, boolean isMagnetic, float pitch, float roll) {
        this.azimuth = azimuth;
        this.isMagnetic = isMagnetic;
        this.pitch = pitch;
        this.roll = roll;
    }

    public float getCompassAzimuth() { return azimuth; }
    public boolean isMagnetic() { return isMagnetic; }
    public float getPitch() { return pitch; }
    public float getRoll() { return roll; }

    public static Orientation getOrientation() throws LocationException {
        return new Orientation(0f, true, 0f, 0f);
    }
}

