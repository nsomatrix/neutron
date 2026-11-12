package javax.microedition.content;

public class Invocation {
    public static final int INIT = 1;
    public static final int WAITING = 2;
    public static final int ACTIVE = 3;
    public static final int OK = 4;
    public static final int CANCELLED = 5;
    public static final int ERROR = 6;
    public static final int HOLD = 7;

    private String url;
    private String type;
    private String action;
    private String id;
    private int status = INIT;
    private byte[] data;
    private boolean responseRequired;

    public Invocation() {}

    public Invocation(String url) {
        this.url = url;
    }

    public Invocation(String url, String type) {
        this.url = url;
        this.type = type;
    }

    public Invocation(String url, String type, String id) {
        this.url = url;
        this.type = type;
        this.id = id;
    }

    public Invocation(String url, String type, String id, boolean responseRequired, String action) {
        this.url = url;
        this.type = type;
        this.id = id;
        this.responseRequired = responseRequired;
        this.action = action;
    }

    public String getURL() { return url; }
    public void setURL(String url) { this.url = url; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getID() { return id; }
    public void setID(String id) { this.id = id; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public byte[] getData() { return data != null ? (byte[]) data.clone() : null; }
    public void setData(byte[] data) { this.data = data != null ? (byte[]) data.clone() : null; }

    public boolean getResponseRequired() { return responseRequired; }
    public void setResponseRequired(boolean responseRequired) { this.responseRequired = responseRequired; }
}

