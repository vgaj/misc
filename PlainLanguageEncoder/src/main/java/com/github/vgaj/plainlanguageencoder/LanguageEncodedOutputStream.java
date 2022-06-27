package com.github.vgaj.plainlanguageencoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to decode an output stream.  Example usage:
 * <pre>
 *     try (InputStream is = new BufferedInputStream(new FileInputStream(origFilename));
 *         OutputStream os = new LanguageEncodedOutputStream( new BufferedOutputStream(new FileOutputStream(encFilename)))) {
 *         int inputData;
 *         while ((inputData = is.read()) != -1) {
 *             os.write(inputData);
 *         }
 *     }
 * </pre>
 */
public class LanguageEncodedOutputStream extends OutputStream {
    private static final Logger logger = LogManager.getLogger(LanguageEncodedOutputStream.class);
    private OutputStream os;
    final private Map<Byte, String> encodeMap;

    public LanguageEncodedOutputStream(OutputStream outputStream) {
        os = outputStream;
        encodeMap = new EncodeData().getEncodeMap();
    }

    private byte[] bufferToWrite = new byte[3];
    private int bufferPosition = 0;


    @Override
    public void write(int byteToWrite) throws IOException {
        bufferToWrite[bufferPosition++] = (byte) byteToWrite;
        if (bufferPosition == bufferToWrite.length) {
            flushBuffer();
        }
    }

    private void flushBuffer() throws IOException {
        byte[] base64Bytes = Base64.getEncoder().encode(Arrays.copyOfRange(bufferToWrite, 0, bufferPosition));
        for (byte base64Byte : base64Bytes) {
            String languageEncodedString = encodeMap.get(base64Byte);
            if (languageEncodedString != null) {
                os.write(languageEncodedString.getBytes(StandardCharsets.UTF_8));
                os.write(" ".getBytes(StandardCharsets.UTF_8));
                // TODO: add full stop and new paragraph
            } else {
                // TODO: remove logging and change to exceptions
                logger.error("Unable to find encode mapping for " + (char) base64Byte);
            }
        }
        bufferPosition = 0;
    }

    @Override
    public void flush() throws IOException {
        flushBuffer();
        os.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        os.close();
    }
}
