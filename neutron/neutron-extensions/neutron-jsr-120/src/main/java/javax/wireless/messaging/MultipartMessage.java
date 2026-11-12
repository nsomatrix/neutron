package javax.wireless.messaging;

import java.util.Date;

public interface MultipartMessage extends Message {
    boolean addAddress(String type, String address);
    void addMessagePart(MessagePart part) throws SizeExceededException;
    String getAddress();
    String[] getAddresses(String type);
    Date getTimestamp();
    MessagePart getMessagePart(String contentID);
    MessagePart[] getMessageParts();
    String getSubject();
    boolean removeAddress(String type, String address);
    void removeAddresses();
    void removeAddresses(String type);
    boolean removeMessagePart(MessagePart part);
    boolean removeMessagePartId(String contentID);
    boolean removeMessagePartLocation(String contentLocation);
    void setAddress(String addr);
    void setSubject(String subject);
    String getHeader(String headerField);
    void setHeader(String headerField, String headerValue);
}

