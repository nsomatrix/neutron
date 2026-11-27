package org.neutron.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.tukaani.xz.LZMA2Options
import org.tukaani.xz.XZOutputStream

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Sorts JAR entries deterministically (resources before classes) as STORED
 * and compresses the resulting archive with maximum-ratio XZ (LZMA2).
 */
@CacheableTask
abstract class CompressXzPayloadTask extends DefaultTask {

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    abstract RegularFileProperty getInputJar()

    @OutputFile
    abstract RegularFileProperty getOutputXz()

    @TaskAction
    void compress() {
        File inFile = inputJar.get().asFile
        File outFile = outputXz.get().asFile
        outFile.parentFile.mkdirs()

        File sortedZipFile = new File(temporaryDir, 'payload-sorted.jar')
        byte[] buf = new byte[32768]

        new ZipFile(inFile).withCloseable { ZipFile zipIn ->
            List<ZipEntry> entries = zipIn.entries().collect().findAll { !it.directory }
            entries.sort { ZipEntry a, ZipEntry b ->
                boolean aClass = a.name.endsWith('.class')
                boolean bClass = b.name.endsWith('.class')
                if (aClass != bClass) return aClass ? 1 : -1
                return a.name.compareTo(b.name)
            }

            new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(sortedZipFile))).withCloseable { ZipOutputStream zipOut ->
                zipOut.setMethod(ZipOutputStream.STORED)
                for (ZipEntry entry : entries) {
                    ZipEntry newEntry = new ZipEntry(entry.name)
                    newEntry.setMethod(ZipEntry.STORED)
                    newEntry.setSize(entry.size)
                    newEntry.setCrc(entry.crc)
                    zipOut.putNextEntry(newEntry)
                    zipIn.getInputStream(entry).withCloseable { InputStream is ->
                        int n
                        while ((n = is.read(buf)) != -1) {
                            zipOut.write(buf, 0, n)
                        }
                    }
                    zipOut.closeEntry()
                }
            }
        }

        LZMA2Options lzmaOpts = new LZMA2Options(9)
        lzmaOpts.setDictSize(16 * 1024 * 1024)
        lzmaOpts.setNiceLen(273)
        lzmaOpts.setMatchFinder(LZMA2Options.MF_BT4)
        lzmaOpts.setMode(LZMA2Options.MODE_NORMAL)

        sortedZipFile.withInputStream { InputStream is ->
            outFile.withOutputStream { OutputStream os ->
                new XZOutputStream(os, lzmaOpts).withCloseable { XZOutputStream xzOut ->
                    int n
                    while ((n = is.read(buf)) != -1) {
                        xzOut.write(buf, 0, n)
                    }
                    xzOut.finish()
                }
            }
        }
        sortedZipFile.delete()
    }
}

