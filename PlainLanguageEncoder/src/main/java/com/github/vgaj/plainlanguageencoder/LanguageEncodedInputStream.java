package com.github.vgaj.plainlanguageencoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

/**
 * Used to encode an input stream.  Example usage:
 * <pre>
 *     try (InputStream is = new LanguageEncodedInputStream( new BufferedInputStream(new FileInputStream(encodedFilename)));
 *         OutputStream os = new BufferedOutputStream(new FileOutputStream(decodedFilename))) {
 *         int inputData;
 *         while ((inputData = is.read()) != -1) {
 *             os.write(inputData);
 *         }
 *     }
 * </pre>
 */
public class LanguageEncodedInputStream extends InputStream {
    private InputStream is;
    private byte[] decodedBytes;
    private int nextBufferReadIndex = 0;
    private final Map<String, Byte> decodeMap;

    /**
     * Construct a new LanguageEncodedInputStream
     * @param inputStream the InputStream that it is adding functionality to
     */
    public LanguageEncodedInputStream(InputStream inputStream) {
        is = inputStream;
        decodeMap = new EncodeData().getDecodeMap();
    }

    /**
     * Read the next byte from the InputStream
     * @return value read
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        if (!isThereSomethingToRead()) {
            fillBuffer();
        }
        return isThereSomethingToRead() ? Byte.toUnsignedInt(decodedBytes[nextBufferReadIndex++]) : -1;
    }

    private boolean isThereSomethingToRead() {
        return (decodedBytes != null && (nextBufferReadIndex < decodedBytes.length));
    }

    private void fillBuffer() throws IOException {
        byte[] base64EncodedBytes = new byte[4];
        int encodedBytesIndex = 0;
        while (encodedBytesIndex < base64EncodedBytes.length) {
            Optional<String> nextWord = getNextWord();
            if (!nextWord.isPresent()) {
                // End of stream so decode what we have
                break;
            }
            Byte base64Byte = decodeMap.get(nextWord.get());
            // This should never occur.  However, if it does lets just ignore it.
            // This means that random words can be added to the encoded content which just get ignored.
            if (base64Byte != null) {
                base64EncodedBytes[encodedBytesIndex++] = base64Byte;
            }
        }
        decodedBytes =  Base64.getDecoder().decode(Arrays.copyOf(base64EncodedBytes,encodedBytesIndex));
        nextBufferReadIndex = 0;
    }

    private Optional<String> getNextWord() throws IOException {
        StringBuilder wordBuilder = new StringBuilder();
        while (true) {
            int nextChar = is.read();
            if (nextChar == -1) {
                return (wordBuilder.length() > 0) ? Optional.of(wordBuilder.toString().toLowerCase()) : Optional.empty();
            } else if (isALetter(nextChar)) {
                wordBuilder.append((char) nextChar);
            } else if (wordBuilder.length() > 0) {
                // Keep reading until it's not a character and something has been read
                return Optional.of(wordBuilder.toString().toLowerCase());
            }
        }
    }

    private boolean isALetter(int value) {
        return ((value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z'));
    }

    /**
     * Close the InputStream
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        is.close();
    }
}
