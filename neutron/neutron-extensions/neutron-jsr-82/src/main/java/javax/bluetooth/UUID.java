/**
 *  Java docs licensed under the Apache License, Version 2.0
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *
 *
 *  @version $Id$
 */ 

package javax.bluetooth;

/**
 * The <code>UUID</code> class defines universally
 * unique identifiers. These 128-bit unsigned integers are guaranteed
 * to be unique across all time and space. Accordingly, an instance of
 * this class is immutable.
 *
 * The Bluetooth specification provides an algorithm describing how a
 * 16-bit or 32-bit UUID could be promoted to a 128-bit UUID.
 * Accordingly, this class provides an interface that assists
 * applications in creating 16-bit, 32-bit, and 128-bit long UUIDs. The
 * methods supported by this class allow equality testing of two UUID
 * objects.
 *
 * <p>
 *
 * The Bluetooth Assigned Numbers document (<A
 * HREF="http://www.bluetooth.org/assigned-numbers/sdp.htm">
 * http://www.bluetooth.org/assigned-numbers/sdp.htm</A>)
 * defines a large number of UUIDs for protocols and service classes.
 * The table below provides a short list of the most common UUIDs
 * defined in the Bluetooth Assigned Numbers document.
 * <TABLE>
 * <TR><TH>Name</TH><TH>Value</TH><TH>Size</TH></TR>
 * <TR><TD>Base UUID Value (Used in promoting 16-bit and 32-bit UUIDs to
 * 128-bit UUIDs)</TD><TD>0x0000000000001000800000805F9B34FB</TD>
 * <TD>128-bit</TD></TR>
 * <TR><TD>SDP</TD><TD>0x0001</TD><TD>16-bit</TD></TR>
 * <TR><TD>RFCOMM</TD><TD>0x0003</TD><TD>16-bit</TD></TR>
 * <TR><TD>OBEX</TD><TD>0x0008</TD><TD>16-bit</TD></TR>
 * <TR><TD>HTTP</TD><TD>0x000C</TD><TD>16-bit</TD></TR>
 * <TR><TD>L2CAP</TD><TD>0x0100</TD><TD>16-bit</TD></TR>
 * <TR><TD>BNEP</TD><TD>0x000F</TD><TD>16-bit</TD></TR>
 * <TR><TD>Serial Port</TD><TD>0x1101</TD><TD>16-bit</TD></TR>
 * <TR><TD>ServiceDiscoveryServerServiceClassID</TD><TD>0x1000</TD>
 * <TD>16-bit</TD></TR>
 * <TR><TD>BrowseGroupDescriptorServiceClassID</TD><TD>0x1001</TD>
 * <TD>16-bit</TD></TR>
 * <TR><TD>PublicBrowseGroup</TD><TD>0x1002</TD><TD>16-bit</TD></TR>
 * <TR><TD>OBEX Object Push
 * Profile</TD><TD>0x1105</TD><TD>16-bit</TD></TR>
 * <TR><TD>OBEX File Transfer
 * Profile</TD><TD>0x1106</TD><TD>16-bit</TD></TR>
 * <TR><TD>Personal Area Networking User</TD><TD>0x1115</TD>
 * <TD>16-bit</TD></TR>
 * <TR><TD>Network Access Point</TD><TD>0x1116</TD><TD>16-bit</TD></TR>
 * <TR><TD>Group Network</TD><TD>0x1117</TD><TD>16-bit</TD></TR>
 * </TABLE>
 *
 * @version 1.0 February 11, 2002
 *
 */
public class UUID {

	private static final String BASE_UUID_SUFFIX = "00001000800000805F9B34FB";

	private final String data;

	private static void validateHex(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
				throw new NumberFormatException("Invalid hex character: " + c);
			}
		}
	}

	private static String padLeftZeros(String s, int targetLen) {
		if (s.length() >= targetLen) {
			return s;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < targetLen - s.length(); i++) {
			sb.append('0');
		}
		sb.append(s);
		return sb.toString();
	}

	public UUID(long uuidValue) {
		if (uuidValue < 0 || uuidValue > 0xffffffffL) {
			throw new IllegalArgumentException("uuidValue is not in the range [0, 2^32 -1]");
		}
		String hex = Long.toHexString(uuidValue).toUpperCase();
		String hex8 = padLeftZeros(hex, 8);
		this.data = hex8 + BASE_UUID_SUFFIX;
	}

	public UUID(String uuidValue, boolean shortUUID) {
		if (uuidValue == null) {
			throw new NullPointerException("uuidValue is null");
		}
		if (uuidValue.length() == 0) {
			throw new IllegalArgumentException("uuidValue length cannot be zero");
		}
		if (uuidValue.startsWith("0x") || uuidValue.startsWith("0X")) {
			throw new NumberFormatException("Hex prefix 0x is not allowed");
		}
		validateHex(uuidValue);

		if (shortUUID) {
			if (uuidValue.length() > 8) {
				throw new IllegalArgumentException("shortUUID length exceeds 8");
			}
			String hex8 = padLeftZeros(uuidValue.toUpperCase(), 8);
			this.data = hex8 + BASE_UUID_SUFFIX;
		} else {
			if (uuidValue.length() > 32) {
				throw new IllegalArgumentException("128-bit UUID length exceeds 32");
			}
			this.data = padLeftZeros(uuidValue.toUpperCase(), 32);
		}
	}

	public String toString() {
		return data;
	}

	public boolean equals(Object value) {
		if (this == value) {
			return true;
		}
		if (value == null || !(value instanceof UUID)) {
			return false;
		}
		return this.data.equals(((UUID) value).data);
	}

	public int hashCode() {
		return data.hashCode();
	}
}