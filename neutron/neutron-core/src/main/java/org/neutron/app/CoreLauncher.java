package org.neutron.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.tukaani.xz.XZInputStream;

public class CoreLauncher {
    public static void main(String[] args) {
        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), ".neutron_core_cache");
            tempDir.mkdirs();
            File payloadJar = new File(tempDir, "neutron-core-payload.jar");

            InputStream is = CoreLauncher.class.getResourceAsStream("/payload.jar.xz");
            if (is == null) {
                System.err.println("Error: Compressed payload stream /payload.jar.xz not found!");
                System.exit(1);
            }

            File tempFile = File.createTempFile("neutron-payload-", ".tmp", tempDir);
            XZInputStream xzIn = new XZInputStream(is);
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] buffer = new byte[32768];
            int n;
            while ((n = xzIn.read(buffer)) != -1) {
                fos.write(buffer, 0, n);
            }
            fos.close();
            xzIn.close();
            is.close();

            if (payloadJar.exists()) {
                payloadJar.delete();
            }
            tempFile.renameTo(payloadJar);

            URLClassLoader classLoader = new URLClassLoader(new URL[]{payloadJar.toURI().toURL()}, CoreLauncher.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
            Class<?> mainClass = Class.forName("org.neutron.app.Main", true, classLoader);
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}
