package javax.microedition.location;

public class Criteria {
    public static final int NO_REQUIREMENT = 0;
    public static final int POWER_USAGE_LOW = 1;
    public static final int POWER_USAGE_MEDIUM = 2;
    public static final int POWER_USAGE_HIGH = 3;

    private int horizontalAccuracy = NO_REQUIREMENT;
    private int verticalAccuracy = NO_REQUIREMENT;
    private int preferredPowerConsumption = NO_REQUIREMENT;
    private boolean altitudeRequired = false;
    private boolean speedAndCourseRequired = false;
    private boolean costAllowed = true;

    public Criteria() {}

    public void setHorizontalAccuracy(int accuracy) { this.horizontalAccuracy = accuracy; }
    public int getHorizontalAccuracy() { return horizontalAccuracy; }

    public void setVerticalAccuracy(int accuracy) { this.verticalAccuracy = accuracy; }
    public int getVerticalAccuracy() { return verticalAccuracy; }

    public void setPreferredPowerConsumption(int level) { this.preferredPowerConsumption = level; }
    public int getPreferredPowerConsumption() { return preferredPowerConsumption; }

    public void setAltitudeRequired(boolean altitudeRequired) { this.altitudeRequired = altitudeRequired; }
    public boolean isAltitudeRequired() { return altitudeRequired; }

    public void setSpeedAndCourseRequired(boolean speedAndCourseRequired) { this.speedAndCourseRequired = speedAndCourseRequired; }
    public boolean isSpeedAndCourseRequired() { return speedAndCourseRequired; }

    public void setCostAllowed(boolean costAllowed) { this.costAllowed = costAllowed; }
    public boolean isCostAllowed() { return costAllowed; }
}

