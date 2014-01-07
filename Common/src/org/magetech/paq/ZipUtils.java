package org.magetech.paq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Aleksander on 14.12.13.
 */
public class ZipUtils {
    public static void unzip(String zipFile, String unpackDir) throws IOException {
        unzip(zipFile, unpackDir, true);
    }

    public static void unzip(String zipFile, String unpackDir, boolean cleanDir) throws IOException {
        File dir = new File(unpackDir);
        if(dir.exists() && dir.isDirectory()) {
            if(cleanDir)
                org.apache.commons.io.FileUtils.cleanDirectory(dir);
        }
        else if(dir.exists()) {
            dir.delete();
            dir.mkdirs();
        } else if(!dir.exists()) {
            dir.mkdirs();
        }

        try(ZipFile zip = new ZipFile(zipFile)) {
            for(Enumeration<? extends  ZipEntry> e = zip.entries(); e.hasMoreElements(); ) {
                unzipEntry(zip, (ZipEntry)e.nextElement(), dir);
            }
        }
    }

    private static void unzipEntry(ZipFile zipFile, ZipEntry entry, File outputDir) throws IOException {
        if(entry.isDirectory()) {
            new File(outputDir, entry.getName()).mkdirs();
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if(!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        try (InputStream is = zipFile.getInputStream(entry)) {
            StreamUtils.saveTo(is, outputFile);
        }
    }
}
