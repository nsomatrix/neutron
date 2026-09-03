/*
 *  Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 */

package org.neutron.app;

/**
 * Listener interface for monitoring dynamic configuration changes.
 */
public interface ConfigChangeListener {
	/**
	 * Invoked when a configuration setting is updated.
	 *
	 * @param key   The name of the configuration property changed.
	 * @param value The new value of the property.
	 */
	void onConfigChanged(String key, Object value);
}
