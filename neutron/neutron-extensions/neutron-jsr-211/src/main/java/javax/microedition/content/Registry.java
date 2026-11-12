package javax.microedition.content;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

public class Registry {
    private static ResponseListener responseListener;
    private static RequestListener requestListener;

    public static Registry getRegistry(String classname) {
        return new Registry();
    }

    public static boolean invoke(Invocation invocation) throws IllegalArgumentException, IOException, ContentHandlerException {
        if (invocation == null) throw new IllegalArgumentException();
        invocation.setStatus(Invocation.OK);

        if (invocation.getURL() != null) {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(invocation.getURL()));
                }
            } catch (Throwable ignored) {
            }
        }
        return true;
    }

    public static boolean invoke(Invocation invocation, Invocation previous) throws IllegalArgumentException, IOException, ContentHandlerException {
        return invoke(invocation);
    }

    public static Invocation getResponse(boolean wait) {
        return null;
    }

    public static void setResponseListener(ResponseListener listener) {
        responseListener = listener;
    }

    public static void setRequestListener(String classname, RequestListener listener) {
        requestListener = listener;
    }

    public static ContentHandler forID(String id, boolean exact) {
        return null;
    }

    public static ContentHandler[] forType(String type) {
        return new ContentHandler[0];
    }

    public static ContentHandler[] forURL(String url) {
        return new ContentHandler[0];
    }

    public static ContentHandler[] forAction(String action) {
        return new ContentHandler[0];
    }

    public static ContentHandler[] getHandlers() {
        return new ContentHandler[0];
    }
}

