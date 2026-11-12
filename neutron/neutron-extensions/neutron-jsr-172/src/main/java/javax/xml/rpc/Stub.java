package javax.xml.rpc;

import java.util.Iterator;

public interface Stub {
    String USERNAME_PROPERTY = "javax.xml.rpc.security.auth.username";
    String PASSWORD_PROPERTY = "javax.xml.rpc.security.auth.password";
    String ENDPOINT_ADDRESS_PROPERTY = "javax.xml.rpc.service.endpoint.address";
    String SESSION_MAINTAIN_PROPERTY = "javax.xml.rpc.session.maintain";

    void _setProperty(String name, Object value);
    Object _getProperty(String name);
    Iterator _getPropertyNames();
}

