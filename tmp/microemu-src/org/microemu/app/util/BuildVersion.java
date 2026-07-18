/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.microemu.app.util.IOUtils;

public class BuildVersion {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getVersion() {
        InputStream mavenDataInputStream;
        InputStream buildVersionInputStream = BuildVersion.class.getResourceAsStream("/META-INF/microemulator-build.version");
        if (buildVersionInputStream != null) {
            Properties projectProperties = new Properties();
            try {
                projectProperties.load(buildVersionInputStream);
                String version = projectProperties.getProperty("build.version");
                if (version != null) {
                    String buildNumber = projectProperties.getProperty("build.buildNum");
                    if (buildNumber != null) {
                        version = version + "." + buildNumber;
                    }
                    String string = version;
                    return string;
                }
            }
            catch (IOException ignore) {
            }
            finally {
                IOUtils.closeQuietly(buildVersionInputStream);
            }
        }
        if ((mavenDataInputStream = BuildVersion.class.getResourceAsStream("/META-INF/maven/org.microemu/microemu-javase/pom.properties")) != null) {
            Properties projectProperties = new Properties();
            try {
                projectProperties.load(mavenDataInputStream);
                String version = projectProperties.getProperty("version");
                if (version != null) {
                    String string = version;
                    return string;
                }
            }
            catch (IOException iOException) {
            }
            finally {
                IOUtils.closeQuietly(mavenDataInputStream);
            }
        }
        return "n/a";
    }
}

