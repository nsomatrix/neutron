package org.neutron.device.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NetworkSniffer {
	public enum Type { HTTP, SOCKET }

	public static class NetworkEvent {
		public Type type;
		public Date timestamp;
		public String urlOrHost;
		public String methodOrPort;
		public String status;

		public NetworkEvent(Type type, String urlOrHost, String methodOrPort, String status) {
			this.type = type;
			this.timestamp = new Date();
			this.urlOrHost = urlOrHost;
			this.methodOrPort = methodOrPort;
			this.status = status;
		}
	}

	public interface NetworkSnifferListener {
		void onNetworkEvent(NetworkEvent event);
		void onCleared();
	}

	private static final int MAX_EVENTS = 200;
	private static final List events = Collections.synchronizedList(new ArrayList());
	private static final List listeners = Collections.synchronizedList(new ArrayList());
	private static boolean enabled = true;

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		NetworkSniffer.enabled = enabled;
	}

	public static void addListener(NetworkSnifferListener listener) {
		listeners.add(listener);
	}

	public static void removeListener(NetworkSnifferListener listener) {
		listeners.remove(listener);
	}

	public static void logEvent(Type type, String urlOrHost, String methodOrPort, String status) {
		if (!enabled) {
			return;
		}
		NetworkEvent event = new NetworkEvent(type, urlOrHost, methodOrPort, status);
		synchronized (events) {
			events.add(event);
			if (events.size() > MAX_EVENTS) {
				events.remove(0);
			}
		}
		synchronized (listeners) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					((NetworkSnifferListener) listeners.get(i)).onNetworkEvent(event);
				} catch (Exception e) {
				}
			}
		}
	}

	public static List getEvents() {
		synchronized (events) {
			return new ArrayList(events);
		}
	}

	public static void clear() {
		events.clear();
		synchronized (listeners) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					((NetworkSnifferListener) listeners.get(i)).onCleared();
				} catch (Exception e) {
				}
			}
		}
	}
}
