package javax.microedition.location;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class LandmarkStore {
    private String name;

    protected LandmarkStore(String name) {
        this.name = name;
    }

    public static LandmarkStore getInstance(String storeName) {
        return new LandmarkStore(storeName != null ? storeName : "default");
    }

    public static String[] listLandmarkStores() throws IOException {
        return new String[] { "default" };
    }

    public Enumeration getLandmarks() throws IOException {
        return new Vector().elements();
    }
}

