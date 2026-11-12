package javax.wireless.messaging;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

public class MessagePart {
    private byte[] content;
    private String mimeType;
    private String contentId;
    private String contentLocation;
    private String enc;

    public MessagePart(byte[] content, String mimeType, String contentId, String contentLocation, String enc) throws SizeExceededException {
        this.content = content != null ? (byte[]) content.clone() : new byte[0];
        this.mimeType = mimeType;
        this.contentId = contentId;
        this.contentLocation = contentLocation;
        this.enc = enc;
    }

    public MessagePart(InputStream is, String mimeType, String contentId, String contentLocation, String enc) throws IOException, SizeExceededException {
        this.mimeType = mimeType;
        this.contentId = contentId;
        this.contentLocation = contentLocation;
        this.enc = enc;
        if (is != null) {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            this.content = baos.toByteArray();
        } else {
            this.content = new byte[0];
        }
    }

    public byte[] getContent() {
        return content != null ? (byte[]) content.clone() : new byte[0];
    }

    public InputStream getContentAsStream() {
        return new ByteArrayInputStream(getContent());
    }

    public String getContentID() {
        return contentId;
    }

    public String getContentLocation() {
        return contentLocation;
    }

    public String getEncoding() {
        return enc;
    }

    public String getMIMEType() {
        return mimeType;
    }

    public int getLength() {
        return content != null ? content.length : 0;
    }
}

