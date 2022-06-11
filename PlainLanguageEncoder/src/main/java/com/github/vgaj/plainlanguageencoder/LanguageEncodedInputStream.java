package com.github.vgaj.plainlanguageencoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger logger = LogManager.getLogger(LanguageEncodedInputStream.class);
    private InputStream is;
    private byte[] decodedBytes;
    private int nextBufferReadIndex = 0;
    private final Map<String, Byte> decodeMap;

    public LanguageEncodedInputStream(InputStream inputStream) {
        is = inputStream;
        decodeMap = new EncodeData().getDecodeMap();
    }

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
            if (base64Byte == null) {
                // This should never occur.  However if it does lets just ignore it.
                // This means that random words can be added to the encoded content which just get ignored.
                logger.error("Unable to find decode mapping for " + nextWord.get());
            } else {
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
                return (wordBuilder.length() > 0) ? Optional.of(wordBuilder.toString()) : Optional.empty();
            } else if (isALetter(nextChar)) {
                wordBuilder.append((char) nextChar);
            } else if (wordBuilder.length() > 0) {
                // Keep reading until it's not a character and something has been read
                return Optional.of(wordBuilder.toString());
            }
        }
    }

    private boolean isALetter(int value) {
        return ((value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z'));
    }

    @Override
    public void close() throws IOException {
        is.close();
    }
}
