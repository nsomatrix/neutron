package javax.microedition.location;

public class AddressInfo {
    public static final int EXTENSION = 1;
    public static final int STREET = 2;
    public static final int POSTAL_CODE = 3;
    public static final int CITY = 4;
    public static final int COUNTY = 5;
    public static final int STATE = 6;
    public static final int COUNTRY = 7;
    public static final int COUNTRY_CODE = 8;
    public static final int DISTRICT = 9;
    public static final int BUILDING_NAME = 10;
    public static final int BUILDING_ZONE = 11;
    public static final int BUILDING_NUMBER = 12;
    public static final int BUILDING_FLOOR = 13;
    public static final int BUILDING_ROOM = 14;
    public static final int CROSSING = 15;
    public static final int URL = 16;
    public static final int PHONE_NUMBER = 17;

    private String[] fields = new String[18];

    public AddressInfo() {}

    public String getField(int field) {
        if (field >= 0 && field < fields.length) return fields[field];
        return null;
    }

    public void setField(int field, String value) {
        if (field >= 0 && field < fields.length) fields[field] = value;
    }
}

