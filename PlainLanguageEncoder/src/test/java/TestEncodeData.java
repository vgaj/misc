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
import com.github.vgaj.plainlanguageencoder.EncodeData;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Tests the validity of the map used for encoding
 */
public class TestEncodeData
{
    private Map<Byte, String> encodeMap;
    private Map<String, Byte> decodeMap;
    public TestEncodeData() {
        encodeMap  = new EncodeData().getEncodeMap();
        decodeMap  = new EncodeData().getDecodeMap();
    }

    @Test
    public void checkForDuplicatesInEncodeMap() {
        Collection<String> wordsInEncodeMap = new EncodeData().getEncodeMap().values();

        // Print out any duplicates
        wordsInEncodeMap.stream().collect(Collectors.groupingBy( Function.identity(), Collectors.counting()))
                .entrySet().stream().filter(stringLongEntry -> stringLongEntry.getValue() > 1)
                .forEach(System.out::println);

        assert wordsInEncodeMap.size() == wordsInEncodeMap.stream().distinct().count();
    }

    @Test
    public void checkSizeOfEncodeMap() {
        assert encodeMap.keySet().size() == 65;
        assert encodeMap.values().size() == 65;
    }

    @Test
    public void checkSizeOfDecodeMap() {
        assert decodeMap.keySet().size() == 65;
        assert decodeMap.values().size() == 65;
    }

    @Test
    public void checkEncodeMapHasAllBase64Codes() {
        for (byte b : getAllBase64Codes()) {
            assert encodeMap.containsKey(b);
        }
    }

    @Test
    public void checkDecodeMapHasAllBase64Codes() {
        for (byte b : getAllBase64Codes()) {
            assert decodeMap.containsValue(b);
        }
    }

    private byte[] getAllBase64Codes() {
        byte[] results = new byte[65];
        int i = 0;
        for (byte value = 'A'; value <= 'Z'; value++) {
            results[i++] = value;
        }
        for (byte value = 'a'; value <= 'z'; value++) {
            results[i++] = value;
        }
        for (byte value = '0'; value <= '9'; value++) {
            results[i++] = value;
        }
        results[i++] = (byte) '+';
        results[i++] = (byte) '/';
        results[i++] = (byte) '=';
        return results;
    }
}
