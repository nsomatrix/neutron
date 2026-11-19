package org.neutron.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.tukaani.xz.XZInputStream;

public class SwingLauncher {
    public static void main(String[] args) {
        try {
            URL payloadResource = SwingLauncher.class.getResource("/payload.jar.xz");
            if (payloadResource == null) {
                Main.main(args);
                return;
            }

            File tempDir = new File(System.getProperty("java.io.tmpdir"), ".neutron_swing_cache");
            tempDir.mkdirs();
            File payloadJar = new File(tempDir, "neutron-swing-payload.jar");

            java.net.URLConnection conn = payloadResource.openConnection();
            long resourceLength = conn.getContentLengthLong();
            long resourceModified = conn.getLastModified();
            File stampFile = new File(tempDir, "payload.stamp");
            String currentStamp = resourceLength + ":" + resourceModified;

            boolean cacheValid = false;
            if (payloadJar.exists() && payloadJar.length() > 0 && stampFile.exists()) {
                try (java.io.FileInputStream fis = new java.io.FileInputStream(stampFile)) {
                    byte[] b = new byte[(int) stampFile.length()];
                    int read = fis.read(b);
                    if (read > 0 && currentStamp.equals(new String(b, 0, read).trim())) {
                        cacheValid = true;
                    }
                } catch (Exception ignore) {
                }
            }

            if (!cacheValid) {
                File tempFile = File.createTempFile("neutron-swing-payload-", ".tmp", tempDir);
                try {
                    try (InputStream is = conn.getInputStream();
                         XZInputStream xzIn = new XZInputStream(is);
                         FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[32768];
                        int n;
                        while ((n = xzIn.read(buffer)) != -1) {
                            fos.write(buffer, 0, n);
                        }
                        fos.flush();
                    }

                    try {
                        java.nio.file.Files.move(tempFile.toPath(), payloadJar.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                                java.nio.file.StandardCopyOption.ATOMIC_MOVE);
                    } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                        java.nio.file.Files.move(tempFile.toPath(), payloadJar.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }

                    try (FileOutputStream stampFos = new FileOutputStream(stampFile)) {
                        stampFos.write(currentStamp.getBytes());
                        stampFos.flush();
                    } catch (Exception ignore) {
                    }
                } finally {
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                }
            }

            String existingCp = System.getProperty("java.class.path");
            if (existingCp == null || !existingCp.contains(payloadJar.getAbsolutePath())) {
                System.setProperty("java.class.path", payloadJar.getAbsolutePath() + (existingCp != null && !existingCp.isEmpty() ? File.pathSeparator + existingCp : ""));
            }

            String targetClassName = "org.neutron.app.Main";
            if (args != null && args.length > 0 && ("--headless".equals(args[0]) || "-headless".equals(args[0]))) {
                targetClassName = "org.neutron.app.Headless";
                String[] stripped = new String[args.length - 1];
                System.arraycopy(args, 1, stripped, 0, stripped.length);
                args = stripped;
            }

            URLClassLoader classLoader = new URLClassLoader(new URL[]{payloadJar.toURI().toURL()}, SwingLauncher.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
            Class<?> mainClass = Class.forName(targetClassName, true, classLoader);
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}

