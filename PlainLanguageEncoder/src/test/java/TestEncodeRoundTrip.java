import com.github.vgaj.plainlanguageencoder.LanguageEncodedInputStream;
import com.github.vgaj.plainlanguageencoder.LanguageEncodedOutputStream;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
