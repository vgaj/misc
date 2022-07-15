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
import com.github.vgaj.plainlanguageencoder.LanguageEncodedInputStream;
import com.github.vgaj.plainlanguageencoder.LanguageEncodedOutputStream;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Round trip tests where data is encoded and decoded and compared
 */
public class TestEncodeRoundTrip
{
    @Test
    public void roundTripEmpty() throws IOException {
        byte[] data = new byte[0];
        roundTripTest(data);
    }
    @Test
    public void roundTripOneByte() throws IOException {
        byte[] data = {-1};
        roundTripTest(data);
    }
    @Test
    public void roundTripTwoByte() throws IOException {
        byte[] data = {-1, 0};
        roundTripTest(data);
    }
    @Test
    public void roundTripThreeByte() throws IOException {
        byte[] data = {-1, 0, 1};
        roundTripTest(data);
    }
    @Test
    public void roundTripFourByte() throws IOException {
        byte[] data = {-1, 0, 1, 2};
        roundTripTest(data);
    }
    @Test
    public void roundTripFiveByte() throws IOException {
        byte[] data = {-1, 0, 1, 2, 3};
        roundTripTest(data);
    }
    @Test
    public void roundTripBrownFox() throws IOException {
        String data = "The quick brown fox jumps over the lazy dog";
        roundTripTest(data.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void roundTripAllBytes() throws IOException {
        byte[] data = new byte[256];
        int i = 0;
        for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
            data[i++] = b;
        }
        roundTripTest(data);
    }

    private void roundTripTest(byte[] dataToRoundTrip) throws IOException {
        ByteArrayInputStream rawDataInputStream = new ByteArrayInputStream(dataToRoundTrip);
        ByteArrayOutputStream encodedOutputSteam = new ByteArrayOutputStream();
        try (InputStream is = new BufferedInputStream(rawDataInputStream);
             OutputStream os = new LanguageEncodedOutputStream( new BufferedOutputStream( encodedOutputSteam))) {
            int inputData;
            while ((inputData = is.read()) != -1) {
                os.write(inputData);
            }
        }
        ByteArrayInputStream encodedInputStream = new ByteArrayInputStream(encodedOutputSteam.toByteArray());
        ByteArrayOutputStream roundTripOutputSteam = new ByteArrayOutputStream();
        try (InputStream is = new LanguageEncodedInputStream( new BufferedInputStream( encodedInputStream));
             OutputStream os = new BufferedOutputStream( roundTripOutputSteam)) {
            int inputData;
            while ((inputData = is.read()) != -1) {
                os.write(inputData);
            }
        }
        assert dataToRoundTrip.length == roundTripOutputSteam.toByteArray().length;
        assert Arrays.equals(dataToRoundTrip, roundTripOutputSteam.toByteArray());
    }

}
