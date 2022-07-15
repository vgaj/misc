/*
 * MIT License

 * Copyright (c) 2022 Viru Gajanayake

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.vgaj.plainlanguageencoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

/**
 * This class is used to decode data from an InputStream encoded by {@link LanguageEncodedOutputStream}
 * The encoding is similar to Base64 but uses short words.
 * Example usage:
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

    /**
     * The underlying InputStream that encoded data is being read from
     */
    private InputStream is;

    /**
     * The bytes that were most recently Base64 decoded
     */
    private byte[] decodedBytes;

    /**
     * The current position in decodedBytes
     */
    private int nextBufferReadIndex = 0;

    /**
     * Lookup that is used for decoding
     */
    private final Map<String, Byte> decodeMap;

    /**
     * Constructs a new LanguageEncodedInputStream which adds functionality to a {@link InputStream}.
     * The encoding is similar to Base64 but uses common, short english words.
     * For example usage see {@link LanguageEncodedInputStream}
     * @param inputStream the InputStream that it is adding functionality to
     */
    public LanguageEncodedInputStream(InputStream inputStream) {
        is = inputStream;
        decodeMap = new EncodeData().getDecodeMap();
    }

    /**
     * Decodes the next byte from the InputStream and returns it.
     * Also see {@link InputStream#read}
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

    /**
     * Helper that tells if there is decoded data in the buffer to return
     */
    private boolean isThereSomethingToRead() {
        return (decodedBytes != null && (nextBufferReadIndex < decodedBytes.length));
    }

    /**
     * Decodes a buffer of 3 bytes.
     * First words are read and decoded into the Base64 characters to fill the 4 byte buffer.
     * The result is then Base64 decoded into data ready for the client.
     */
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
            // This should never occur.  However, if it does let's just ignore it.
            // This means that random words can be added to the encoded content which just get ignored.
            if (base64Byte != null) {
                base64EncodedBytes[encodedBytesIndex++] = base64Byte;
            }
        }
        decodedBytes =  Base64.getDecoder().decode(Arrays.copyOf(base64EncodedBytes,encodedBytesIndex));
        nextBufferReadIndex = 0;
    }

    /**
     * Helper to read the next full word from the underlying InputStream
     */
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

    /**
     * Helper that tells if a character was read from the InputStream
     */
    private boolean isALetter(int value) {
        return ((value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z'));
    }

    /**
     * See {@link InputStream#close()}
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        is.close();
    }
}
