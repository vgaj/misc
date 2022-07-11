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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

/**
 * This class is used to encode data being written to an OutputStream.
 * The encoding is similar to Base64 but uses common, short english words.
 * Example usage:
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
     * Constructs a new LanguageEncodedOutputStream which adds functionality to a {@link OutputStream}.
     * For example usage see {@link LanguageEncodedOutputStream}
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
     * Encodes the byte and writes it to the OutputStream.
     * Also see {@link OutputStream#write}
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
     * See {@link OutputStream#flush}
     * @throws IOException
     */
    @Override
    public void flush() throws IOException {
        flushBuffer();
        os.flush();
    }

    /**
     * See {@link OutputStream#close}
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
