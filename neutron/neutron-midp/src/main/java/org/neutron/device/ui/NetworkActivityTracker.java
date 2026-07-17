package org.neutron.device.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkActivityTracker {

	public interface NetworkActivityListener {
		void onActivityUpdate(long totalRead, long totalWritten, double readSpeed, double writeSpeed, int ping);
	}

	private static long totalBytesRead = 0;
	private static long totalBytesWritten = 0;

	private static double currentReadSpeed = 0.0; // KB/s
	private static double currentWriteSpeed = 0.0; // KB/s

	private static String lastHost = "8.8.8.8";
	private static int lastPort = 53;
	private static int currentPing = -1; // ms

	private static final List listeners = Collections.synchronizedList(new ArrayList());
	private static Timer timer;
	private static volatile boolean pingRunning = false;
	private static boolean pingEnabled = true;

	static {
		startTimer();
	}

	private static synchronized void startTimer() {
		if (timer != null) {
			return;
		}
		timer = new Timer(true); // daemon thread
		timer.scheduleAtFixedRate(new TimerTask() {
			private long lastRead = 0;
			private long lastWritten = 0;
			private long lastTime = System.currentTimeMillis();
			private int tickCount = 0;

			public void run() {
				long now = System.currentTimeMillis();
				long elapsed = now - lastTime;
				if (elapsed <= 0) {
					elapsed = 1000;
				}

				long currentRead;
				long currentWritten;
				synchronized (NetworkActivityTracker.class) {
					currentRead = totalBytesRead;
					currentWritten = totalBytesWritten;
				}

				long readDiff = currentRead - lastRead;
				long writeDiff = currentWritten - lastWritten;

				lastRead = currentRead;
				lastWritten = currentWritten;
				lastTime = now;

				// Calculate speeds in KB/s
				double rSpeed = (readDiff / 1024.0) / (elapsed / 1000.0);
				double wSpeed = (writeDiff / 1024.0) / (elapsed / 1000.0);

				synchronized (NetworkActivityTracker.class) {
					currentReadSpeed = rSpeed;
					currentWriteSpeed = wSpeed;
				}

				int pingVal;
				synchronized (NetworkActivityTracker.class) {
					pingVal = currentPing;
				}

				notifyListeners(currentRead, currentWritten, rSpeed, wSpeed, pingVal);

				tickCount++;
				if (tickCount >= 3) {
					tickCount = 0;
					triggerPingCheck();
				}
			}
		}, 1000, 1000);
	}

	private static void triggerPingCheck() {
		if (!pingEnabled || pingRunning) {
			return;
		}
		pingRunning = true;
		new Thread("NetworkPingChecker") {
			public void run() {
				String host;
				int port;
				synchronized (NetworkActivityTracker.class) {
					host = lastHost;
					port = lastPort;
				}

				long start = System.currentTimeMillis();
				int pingVal = -1;
				java.net.Socket socket = null;
				try {
					socket = new java.net.Socket();
					socket.connect(new java.net.InetSocketAddress(host, port), 2000);
					pingVal = (int) (System.currentTimeMillis() - start);
				} catch (Exception e) {
					if (e instanceof java.net.ConnectException) {
						pingVal = (int) (System.currentTimeMillis() - start);
					} else {
						pingVal = -1;
					}
				} finally {
					if (socket != null) {
						try { socket.close(); } catch (Exception e) {}
					}
				}

				long currentRead;
				long currentWritten;
				double rSpeed;
				double wSpeed;
				synchronized (NetworkActivityTracker.class) {
					currentPing = pingVal;
					currentRead = totalBytesRead;
					currentWritten = totalBytesWritten;
					rSpeed = currentReadSpeed;
					wSpeed = currentWriteSpeed;
				}
				pingRunning = false;

				notifyListeners(currentRead, currentWritten, rSpeed, wSpeed, pingVal);
			}
		}.start();
	}

	public static synchronized void setLastHostAndPort(String host, int port) {
		if (host != null && !host.trim().isEmpty()) {
			lastHost = host;
			lastPort = port;
		}
	}

	public static synchronized void setPingEnabled(boolean enabled) {
		pingEnabled = enabled;
	}

	public static synchronized boolean isPingEnabled() {
		return pingEnabled;
	}

	public static void addListener(NetworkActivityListener listener) {
		listeners.add(listener);
		synchronized (NetworkActivityTracker.class) {
			listener.onActivityUpdate(totalBytesRead, totalBytesWritten, currentReadSpeed, currentWriteSpeed, currentPing);
		}
	}

	public static void removeListener(NetworkActivityListener listener) {
		listeners.remove(listener);
	}

	public static synchronized void trackRead(long bytes) {
		if (bytes > 0) {
			totalBytesRead += bytes;
		}
	}

	public static synchronized void trackWrite(long bytes) {
		if (bytes > 0) {
			totalBytesWritten += bytes;
		}
	}

	public static synchronized long getTotalBytesRead() {
		return totalBytesRead;
	}

	public static synchronized long getTotalBytesWritten() {
		return totalBytesWritten;
	}

	public static synchronized double getCurrentReadSpeed() {
		return currentReadSpeed;
	}

	public static synchronized double getCurrentWriteSpeed() {
		return currentWriteSpeed;
	}

	public static synchronized int getCurrentPing() {
		return currentPing;
	}

	public static synchronized void reset() {
		totalBytesRead = 0;
		totalBytesWritten = 0;
		currentReadSpeed = 0.0;
		currentWriteSpeed = 0.0;
		currentPing = -1;
		notifyListeners(0, 0, 0.0, 0.0, -1);
	}

	private static void notifyListeners(long totalRead, long totalWritten, double readSpeed, double writeSpeed, int ping) {
		synchronized (listeners) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					((NetworkActivityListener) listeners.get(i)).onActivityUpdate(totalRead, totalWritten, readSpeed, writeSpeed, ping);
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	public static class TrackingInputStream extends java.io.InputStream {
		private final java.io.InputStream in;

		public TrackingInputStream(java.io.InputStream in) {
			this.in = in;
		}

		public int read() throws java.io.IOException {
			int b = in.read();
			if (b != -1) {
				trackRead(1);
			}
			return b;
		}

		public int read(byte[] b) throws java.io.IOException {
			int n = in.read(b);
			if (n > 0) {
				trackRead(n);
			}
			return n;
		}

		public int read(byte[] b, int off, int len) throws java.io.IOException {
			int n = in.read(b, off, len);
			if (n > 0) {
				trackRead(n);
			}
			return n;
		}

		public long skip(long n) throws java.io.IOException {
			return in.skip(n);
		}

		public int available() throws java.io.IOException {
			return in.available();
		}

		public void close() throws java.io.IOException {
			in.close();
		}

		public synchronized void mark(int readlimit) {
			in.mark(readlimit);
		}

		public synchronized void reset() throws java.io.IOException {
			in.reset();
		}

		public boolean markSupported() {
			return in.markSupported();
		}
	}

	public static class TrackingOutputStream extends java.io.OutputStream {
		private final java.io.OutputStream out;

		public TrackingOutputStream(java.io.OutputStream out) {
			this.out = out;
		}

		public void write(int b) throws java.io.IOException {
			out.write(b);
			trackWrite(1);
		}

		public void write(byte[] b) throws java.io.IOException {
			out.write(b);
			trackWrite(b.length);
		}

		public void write(byte[] b, int off, int len) throws java.io.IOException {
			out.write(b, off, len);
			trackWrite(len);
		}

		public void flush() throws java.io.IOException {
			out.flush();
		}

		public void close() throws java.io.IOException {
			out.close();
		}
	}
}
