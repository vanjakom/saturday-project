package com.mungolab.dynamic.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class IOUtils {
    public static byte[] toByteArray(InputStream inputStream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            return buffer.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
