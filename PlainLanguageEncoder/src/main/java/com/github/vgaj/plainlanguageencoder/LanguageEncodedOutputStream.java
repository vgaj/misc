package com.github.vgaj.plainlanguageencoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

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
    private OutputStream os;
    final private Map<Byte, String> encodeMap;

    /**
     * Construct a new LanguageEncodedOutputStream
     * @param outputStream the OutputStream that it is adding functionality to
     */
    public LanguageEncodedOutputStream(OutputStream outputStream) {
        os = outputStream;
        encodeMap = new EncodeData().getEncodeMap();
    }

    private byte[] bufferToWrite = new byte[3];
    private int bufferPosition = 0;
    private long wordNumber = 0;
    private long sentenceNumber = 0;
    private boolean spaceBeforeNextWord = false;
    private boolean startingNewSentence = true;

    /**
     * Write the byte to the stream
     * @param byteToWrite value to write
     * @throws IOException
     */
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
                if (spaceBeforeNextWord) {
                    os.write(" ".getBytes(StandardCharsets.UTF_8));
                }
                spaceBeforeNextWord = true;
                if (startingNewSentence) {
                    languageEncodedString = languageEncodedString.substring(0,1).toUpperCase() + languageEncodedString.substring(1);
                    startingNewSentence = false;
                }
                if (languageEncodedString.equals("i")) {
                    languageEncodedString = "I";
                }
                os.write(languageEncodedString.getBytes(StandardCharsets.UTF_8));
                boolean isEndOfSentence = false;
                wordNumber++;
                if ((wordNumber % 10) == 0) {
                    os.write(".".getBytes(StandardCharsets.UTF_8));
                    isEndOfSentence = true;
                    sentenceNumber++;
                    startingNewSentence = true;
                }
                if (isEndOfSentence && (sentenceNumber % 10 == 0)) {
                    os.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                    os.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                    spaceBeforeNextWord = false;
                }
            } else {
                throw new IOException("Unable to find encode mapping for " + (char) base64Byte);
            }
        }
        bufferPosition = 0;
    }

    /**
     * Flush this OutputStream
     * @throws IOException
     */
    @Override
    public void flush() throws IOException {
        flushBuffer();
        os.flush();
    }

    /**
     * Close this OutputStream
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        flush();
        os.write(".".getBytes(StandardCharsets.UTF_8));
        flush();
        os.close();
    }
}
