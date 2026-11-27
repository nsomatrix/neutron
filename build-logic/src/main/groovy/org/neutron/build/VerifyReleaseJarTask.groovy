package org.neutron.build

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.neutron.build.verify.ReleaseVerifier
import org.tukaani.xz.XZInputStream

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Verifies the final packed standalone release JAR end-to-end:
 * 1) Archive structure, Main-Class manifest, and STORED payload.jar.xz
 * 2) Real `java -jar <releaseJar> --headless --help` launcher execution
 * 3) Forked JVM verification of the extracted obfuscated payload JAR via ReleaseVerifier
 * 4) E2E headless MIDlet execution + offscreen pixel rendering + MRU restart persistence
 * 5) Real GUI JFrame + EDT rendering smoke test when a display server is available (e.g. Xvfb in CI)
 */
abstract class VerifyReleaseJarTask extends DefaultTask {

    private static final long SUBPROCESS_TIMEOUT_SECONDS = 45L

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    abstract RegularFileProperty getReleaseJar()

    @InputFiles
    @PathSensitive(PathSensitivity.NONE)
    abstract ConfigurableFileCollection getSmokeMidletFiles()

    @Input
    abstract Property<String> getExpectedMainClass()

    @Input
    abstract Property<Boolean> getVerifyFlatLaf()

    @TaskAction
    void verify() {
        File jarFile = releaseJar.get().asFile
        File midletFile = smokeMidletFiles.singleFile
        if (!jarFile.exists() || jarFile.length() == 0) {
            throw new GradleException("Release JAR is missing or empty: ${jarFile}")
        }
        if (!midletFile.exists() || midletFile.length() == 0) {
            throw new GradleException("Smoke MIDlet JAR is missing or empty: ${midletFile}")
        }

        File extractedPayloadJar = new File(temporaryDir, 'extracted-payload.jar')
        File sandboxHome = new File(temporaryDir, 'sandbox-home')
        File sandboxTmp = new File(temporaryDir, 'sandbox-tmp')
        sandboxHome.deleteDir()
        sandboxTmp.deleteDir()
        sandboxHome.mkdirs()
        sandboxTmp.mkdirs()

        byte[] buf = new byte[32768]

        // 1. Verify standalone JAR layout, manifest, and decompress payload.jar.xz
        new ZipFile(jarFile).withCloseable { ZipFile zf ->
            ZipEntry manifestEntry = zf.getEntry('META-INF/MANIFEST.MF')
            if (manifestEntry == null) {
                throw new GradleException("Release JAR missing META-INF/MANIFEST.MF: ${jarFile}")
            }
            zf.getInputStream(manifestEntry).withCloseable { InputStream mis ->
                Manifest mf = new Manifest(mis)
                String actualMain = mf.mainAttributes.getValue('Main-Class')
                if (actualMain != expectedMainClass.get()) {
                    throw new GradleException("Expected Main-Class '${expectedMainClass.get()}' but found '${actualMain}'")
                }
            }

            ZipEntry payloadEntry = zf.getEntry('payload.jar.xz')
            if (payloadEntry == null) {
                throw new GradleException("Release JAR missing payload.jar.xz: ${jarFile}")
            }
            if (payloadEntry.method != ZipEntry.STORED) {
                throw new GradleException("payload.jar.xz must be STORED (uncompressed outer entry), got method=${payloadEntry.method}")
            }

            zf.getInputStream(payloadEntry).withCloseable { InputStream rawIn ->
                new XZInputStream(rawIn).withCloseable { XZInputStream xzIn ->
                    extractedPayloadJar.withOutputStream { OutputStream out ->
                        int n
                        while ((n = xzIn.read(buf)) != -1) {
                            out.write(buf, 0, n)
                        }
                    }
                }
            }
        }

        // 2. Verify real standalone launcher execution: java -jar <releaseJar> --headless --help
        runCommand(
            [
                'java', '-Djava.awt.headless=true',
                "-Duser.home=${sandboxHome.absolutePath}",
                "-Djava.io.tmpdir=${sandboxTmp.absolutePath}",
                '-jar', jarFile.absolutePath, '--headless', '--help'
            ],
            "Standalone launcher execution (--headless --help) for ${jarFile.name}"
        )

        // 3. Verify all ProGuard-sensitive subsystems inside the extracted obfuscated payload
        File verifierCodeSource = new File(ReleaseVerifier.protectionDomain.codeSource.location.toURI())
        String cp = "${extractedPayloadJar.absolutePath}${File.pathSeparator}${verifierCodeSource.absolutePath}"
        runCommand(
            [
                'java', '-Djava.awt.headless=true',
                "-Duser.home=${sandboxHome.absolutePath}",
                "-Djava.io.tmpdir=${sandboxTmp.absolutePath}",
                '-cp', cp, ReleaseVerifier.name, verifyFlatLaf.get().toString()
            ],
            "Obfuscated payload runtime verification for ${jarFile.name}"
        )

        // 4. E2E Headless MIDlet execution + offscreen pixel rendering verification
        File dumpedFrame = new File(temporaryDir, 'headless-frame.png')
        dumpedFrame.delete()
        String e2eOut = runCommand(
            [
                'java', '-Djava.awt.headless=true',
                "-Duser.home=${sandboxHome.absolutePath}",
                "-Djava.io.tmpdir=${sandboxTmp.absolutePath}",
                "-Dneutron.headless.dump_frame=${dumpedFrame.absolutePath}",
                '-jar', jarFile.absolutePath,
                '--headless', '--quit', '--rms', 'file', midletFile.absolutePath
            ],
            "E2E headless MIDlet run for ${jarFile.name}"
        )
        assertSmokeOutput(e2eOut, "E2E headless run (${jarFile.name})")
        verifyRenderedFrame(dumpedFrame)

        // 5. Real GUI JFrame + EDT rendering + config2.xml persistence/reload smoke test when display is available
        boolean hasDisplay = ((System.getenv('DISPLAY') ?: '').trim().length() > 0) ||
            ((System.getenv('WAYLAND_DISPLAY') ?: '').trim().length() > 0)
        if (hasDisplay) {
            String guiOut1 = runCommand(
                [
                    'java',
                    "-Duser.home=${sandboxHome.absolutePath}",
                    "-Djava.io.tmpdir=${sandboxTmp.absolutePath}",
                    '-jar', jarFile.absolutePath,
                    '--quit', '--rms', 'memory', midletFile.absolutePath
                ],
                "E2E GUI MIDlet run #1 for ${jarFile.name}"
            )
            assertSmokeOutput(guiOut1, "E2E GUI run #1 (${jarFile.name})")

            File mruConfig = new File(sandboxHome, '.neutron/config2.xml')
            if (!mruConfig.exists()) {
                throw new GradleException("Expected GUI run to write config2.xml at ${mruConfig}")
            }

            // Second GUI launch reloads config2.xml (theme, device config, and MRU list)
            String guiOut2 = runCommand(
                [
                    'java',
                    "-Duser.home=${sandboxHome.absolutePath}",
                    "-Djava.io.tmpdir=${sandboxTmp.absolutePath}",
                    '-jar', jarFile.absolutePath,
                    '--quit', '--rms', 'memory', midletFile.absolutePath
                ],
                "E2E GUI MIDlet restart run #2 (config2.xml reload) for ${jarFile.name}"
            )
            assertSmokeOutput(guiOut2, "E2E GUI restart run #2 (${jarFile.name})")
        } else {
            logger.lifecycle("Skipping E2E GUI check for ${jarFile.name}: no DISPLAY/WAYLAND_DISPLAY set (run under xvfb-run to enable).")
        }

        logger.lifecycle("Verified release artifact cleanly: ${jarFile.name} (${jarFile.length()} bytes)")
    }

    private void assertSmokeOutput(String output, String label) {
        if (!output.contains('SMOKE_MIDLET_PASS')) {
            throw new GradleException("${label} did not print SMOKE_MIDLET_PASS. Output:\n${output}")
        }
        if (output.contains('SMOKE_MIDLET_FAIL') || output.contains('SEVERE: FlatLaf: Failed to parse')) {
            throw new GradleException("${label} reported runtime failure. Output:\n${output}")
        }
    }

    private void verifyRenderedFrame(File frameFile) {
        if (!frameFile.exists() || frameFile.length() == 0) {
            throw new GradleException("Headless frame dump was not produced: ${frameFile}")
        }
        BufferedImage img = ImageIO.read(frameFile)
        if (img == null || img.width <= 0 || img.height <= 0) {
            throw new GradleException("Invalid rendered frame image: ${frameFile}")
        }
        int firstPixel = img.getRGB(0, 0)
        boolean hasVariation = false
        for (int y = 0; y < img.height && !hasVariation; y++) {
            for (int x = 0; x < img.width; x++) {
                if (img.getRGB(x, y) != firstPixel) {
                    hasVariation = true
                    break
                }
            }
        }
        if (!hasVariation) {
            throw new GradleException("Rendered frame ${frameFile} is completely uniform (no visible drawing).")
        }
    }

    private String runCommand(List<?> command, String label) {
        List<String> stringArgs = command.collect { it.toString() }
        Process process = new ProcessBuilder(stringArgs)
            .redirectErrorStream(true)
            .start()
        StringBuilder sb = new StringBuilder()
        Thread reader = new Thread({
            byte[] buffer = new byte[8192]
            int read
            while ((read = process.inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, read, StandardCharsets.UTF_8))
            }
        } as Runnable)
        reader.start()
        boolean finished = process.waitFor(SUBPROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!finished) {
            process.destroyForcibly()
            reader.join(2000L)
            throw new GradleException("${label} timed out after ${SUBPROCESS_TIMEOUT_SECONDS}s:\n${sb}")
        }
        reader.join(5000L)
        String output = sb.toString()
        int exit = process.exitValue()
        if (exit != 0) {
            throw new GradleException("${label} failed with exit code ${exit}:\n${output}")
        }
        return output
    }
}
