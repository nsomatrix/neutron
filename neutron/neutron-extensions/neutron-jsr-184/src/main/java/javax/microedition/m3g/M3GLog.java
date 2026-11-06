/*
 * Neutron JSR-184 M3G Implementation
 */
package javax.microedition.m3g;

final class M3GLog {
	static final int LOG_DEBUG = 0;
	static final int LOG_INFO = 1;
	static final int LOG_WARNING = 2;
	static final int LOG_ERROR = 3;

	private static final boolean DEBUG = false;

	static void log(int level, String message) {
		if (DEBUG && level >= LOG_WARNING) {
			System.err.println("[M3G] " + message);
		}
	}
}

