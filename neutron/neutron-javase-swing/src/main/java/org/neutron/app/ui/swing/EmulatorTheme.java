/**
 *  Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 */

package org.neutron.app.ui.swing;

public enum EmulatorTheme {
	DARK("Dark", "com.formdev.flatlaf.FlatDarkLaf", "FlatLaf Dark"),
	LIGHT("Light", "com.formdev.flatlaf.FlatLightLaf", "FlatLaf Light"),
	INTELLIJ("IntelliJ", "com.formdev.flatlaf.FlatIntelliJLaf", "FlatLaf IntelliJ"),
	DRACULA("Dracula", "com.formdev.flatlaf.FlatDarculaLaf", "FlatLaf Dracula", "FlatLaf Darcula"),
	MAC_DARK("MacDark", "com.formdev.flatlaf.themes.FlatMacDarkLaf", "FlatLaf macOS Dark", "Mac Dark"),
	MAC_LIGHT("MacLight", "com.formdev.flatlaf.themes.FlatMacLightLaf", "FlatLaf macOS Light", "Mac Light"),
	SYSTEM("System", null, "System Look and Feel");

	private final String displayName;
	private final String lafClassName;
	private final String[] legacyAliases;

	EmulatorTheme(String displayName, String lafClassName, String... legacyAliases) {
		this.displayName = displayName;
		this.lafClassName = lafClassName;
		this.legacyAliases = legacyAliases;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getLafClassName() {
		return lafClassName;
	}

	public static EmulatorTheme fromString(String name) {
		if (name == null || name.trim().isEmpty()) {
			return MAC_LIGHT;
		}
		name = name.trim();
		for (EmulatorTheme theme : values()) {
			if (theme.displayName.equalsIgnoreCase(name)) {
				return theme;
			}
			for (String alias : theme.legacyAliases) {
				if (alias.equalsIgnoreCase(name)) {
					return theme;
				}
			}
		}
		return MAC_LIGHT;
	}
}
