package javax.microedition.content;

import java.io.IOException;

public interface ContentHandlerServer extends ContentHandler {
    Invocation getRequest(boolean wait) throws IOException;
    boolean finish(Invocation invocation, int status) throws IllegalArgumentException, IOException;
    void cancel(Invocation invocation) throws IllegalArgumentException, IOException;
    void setListener(RequestListener listener);
}

