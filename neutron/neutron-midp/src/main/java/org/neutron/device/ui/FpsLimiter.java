package org.neutron.device.ui;

public class FpsLimiter {
	private long lastFrameTime = 0;

	public synchronized void limit(int maxFps) {
		if (maxFps <= 0) {
			return;
		}

		long targetFrameTimeNs = 1000000000L / maxFps;
		if (lastFrameTime == 0) {
			lastFrameTime = System.nanoTime();
			return;
		}

		long now = System.nanoTime();
		long elapsedNs = now - lastFrameTime;
		long remainingNs = targetFrameTimeNs - elapsedNs;

		if (remainingNs > 0) {
			long sleepMs = remainingNs / 1000000L;
			int sleepNs = (int) (remainingNs % 1000000L);
			try {
				Thread.sleep(sleepMs, sleepNs);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			lastFrameTime = System.nanoTime();
		} else {
			lastFrameTime = now;
		}
	}
}
