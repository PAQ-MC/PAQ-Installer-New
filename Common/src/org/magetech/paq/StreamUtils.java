package org.magetech.paq;

import java.io.*;

/**
 * Created by Aleksander on 12.12.13.
 */
public class StreamUtils {
    public static void saveTo(InputStream in, String fileName) throws IOException {
        saveTo(in, new File(fileName));
    }

    public static void saveTo(InputStream in, File file) throws IOException {
        try(OutputStream out = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            while(true) {
                int read = in.read(buffer, 0, 4096);
                if(read <= 0) {
                    out.flush();
                    in.close();
                    out.close();
                    break;
                }

                out.write(buffer, 0, read);
            }
        }
    }

    public static InputStream append(InputStream is, String versionString) throws IOException {
        return new SequenceInputStream(is, new ByteArrayInputStream(versionString.getBytes("utf-8")));
    }
}
