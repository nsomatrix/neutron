package javax.xml.rpc;

import javax.xml.namespace.QName;

public interface FaultDetailHandler {
    Object handleFault(Object faultDetail, QName faultName);
}

