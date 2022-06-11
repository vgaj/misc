import com.github.vgaj.plainlanguageencoder.EncodeData;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
