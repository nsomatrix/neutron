package org.neutron.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

import java.nio.charset.StandardCharsets
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Packages the ProGuard-shrunk launcher classes (DEFLATED) together with the
 * pre-compressed payload.jar.xz (STORED) and a Main-Class MANIFEST.MF.
 */
@CacheableTask
abstract class PackStandaloneJarTask extends DefaultTask {

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    abstract RegularFileProperty getLauncherJar()

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    abstract RegularFileProperty getPayloadXz()

    @Input
    abstract Property<String> getMainClass()

    @OutputFile
    abstract RegularFileProperty getOutputJar()

    @TaskAction
    void pack() {
        File outFile = outputJar.get().asFile
        outFile.parentFile.mkdirs()

        byte[] buf = new byte[32768]
        String manifestContent = "Manifest-Version: 1.0\r\nMain-Class: ${mainClass.get()}\r\n\r\n"

        new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile))).withCloseable { ZipOutputStream zOut ->
            // Write manifest first
            ZipEntry manifestEntry = new ZipEntry('META-INF/MANIFEST.MF')
            manifestEntry.setMethod(ZipEntry.DEFLATED)
            zOut.putNextEntry(manifestEntry)
            zOut.write(manifestContent.getBytes(StandardCharsets.UTF_8))
            zOut.closeEntry()

            // Copy launcher classes (DEFLATED)
            new ZipFile(launcherJar.get().asFile).withCloseable { ZipFile zIn ->
                zIn.entries().each { ZipEntry e ->
                    if (e.directory || e.name == 'META-INF/MANIFEST.MF') return
                    ZipEntry ne = new ZipEntry(e.name)
                    ne.setMethod(ZipEntry.DEFLATED)
                    zOut.putNextEntry(ne)
                    zIn.getInputStream(e).withCloseable { InputStream is ->
                        int n
                        while ((n = is.read(buf)) != -1) {
                            zOut.write(buf, 0, n)
                        }
                    }
                    zOut.closeEntry()
                }
            }

            // Embed payload.jar.xz uncompressed (STORED)
            File payloadFile = payloadXz.get().asFile
            CRC32 crc = new CRC32()
            payloadFile.withInputStream { InputStream is ->
                int n
                while ((n = is.read(buf)) != -1) {
                    crc.update(buf, 0, n)
                }
            }
            ZipEntry payloadEntry = new ZipEntry('payload.jar.xz')
            payloadEntry.setMethod(ZipEntry.STORED)
            payloadEntry.setSize(payloadFile.length())
            payloadEntry.setCrc(crc.value)
            zOut.putNextEntry(payloadEntry)
            payloadFile.withInputStream { InputStream is ->
                int n
                while ((n = is.read(buf)) != -1) {
                    zOut.write(buf, 0, n)
                }
            }
            zOut.closeEntry()
        }
    }
}

