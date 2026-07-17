package org.neutron.device.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkActivityTracker {

	public interface NetworkActivityListener {
		void onActivityUpdate(long totalRead, long totalWritten, double readSpeed, double writeSpeed);
	}

	private static long totalBytesRead = 0;
	private static long totalBytesWritten = 0;

	private static double currentReadSpeed = 0.0; // KB/s
	private static double currentWriteSpeed = 0.0; // KB/s

	private static final List listeners = Collections.synchronizedList(new ArrayList());
	private static Timer timer;

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

				notifyListeners(currentRead, currentWritten, rSpeed, wSpeed);
			}
		}, 1000, 1000);
	}

	public static void addListener(NetworkActivityListener listener) {
		listeners.add(listener);
		synchronized (NetworkActivityTracker.class) {
			listener.onActivityUpdate(totalBytesRead, totalBytesWritten, currentReadSpeed, currentWriteSpeed);
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

	public static synchronized void reset() {
		totalBytesRead = 0;
		totalBytesWritten = 0;
		currentReadSpeed = 0.0;
		currentWriteSpeed = 0.0;
		notifyListeners(0, 0, 0.0, 0.0);
	}

	private static void notifyListeners(long totalRead, long totalWritten, double readSpeed, double writeSpeed) {
		synchronized (listeners) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					((NetworkActivityListener) listeners.get(i)).onActivityUpdate(totalRead, totalWritten, readSpeed, writeSpeed);
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
