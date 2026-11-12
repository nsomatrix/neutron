package javax.microedition.content;

import java.io.IOException;

public class ContentHandlerException extends IOException {
    public static final int NO_REGISTERED_HANDLER = 1;
    public static final int TYPE_NOT_SUPPORTED = 2;
    public static final int AMBIGUOUS = 3;

    private int reason;

    public ContentHandlerException(String reason, int errorCode) {
        super(reason);
        this.reason = errorCode;
    }

    public int getErrorCode() {
        return reason;
    }
}

