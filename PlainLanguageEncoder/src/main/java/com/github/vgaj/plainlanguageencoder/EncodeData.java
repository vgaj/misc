package com.github.vgaj.plainlanguageencoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the map used by LanguageEncodedInputStream and LanguageEncodedOutputStream
 * to encode and decode data.
 */
public class EncodeData {

    /**
     * Map for encoding
     * @return
     */
    public Map<Byte, String> getEncodeMap() {
        if (encodeMap == null) {
            initialiseMaps();
        }
        return encodeMap;
    }

    /**
     * Map for decoding
     * @return
     */
    public Map<String, Byte> getDecodeMap() {
        if (decodeMap == null) {
            initialiseMaps();
        }
        return decodeMap;
    }

    private Map<Byte, String> encodeMap;
    private Map<String, Byte> decodeMap;

    private void initialiseMaps() {
        int indexIntoEncodeData = 0;
        HashMap<Byte, String> tempEncodeMap = new HashMap<>();

        for (byte value = 'A'; value <= 'Z'; value++) {
            tempEncodeMap.put(value, encodeData[indexIntoEncodeData++]);
        }
        for (byte value = 'a'; value <= 'z'; value++) {
            tempEncodeMap.put(value, encodeData[indexIntoEncodeData++]);
        }
        for (byte value = '0'; value <= '9'; value++) {
            tempEncodeMap.put(value, encodeData[indexIntoEncodeData++]);
        }
        tempEncodeMap.put((byte) '+', encodeData[indexIntoEncodeData++]);
        tempEncodeMap.put((byte) '/', encodeData[indexIntoEncodeData++]);
        tempEncodeMap.put((byte) '=', encodeData[indexIntoEncodeData++]);
        encodeMap = Collections.unmodifiableMap(tempEncodeMap);


        // Make decode map
        HashMap<String, Byte> tempDecodeMap = new HashMap<>();
        encodeMap.forEach((a, s) -> tempDecodeMap.put(s, a));
        decodeMap = Collections.unmodifiableMap(tempDecodeMap);

    }

    // Note this raw dataset has more data than we need
    private final String[] encodeData =
            {
                    "a",
                    "i",
                    "be",
                    "of",
                    "to",
                    "in",
                    "it",
                    "do",
                    "he",
                    "on",
                    "we",
                    "at",
                    "go",
                    "or",
                    "by",
                    "my",
                    "as",
                    "if",
                    "me",
                    "so",
                    "up",
                    "us",
                    "oh",
                    "the",
                    "and",
                    "you",
                    "but",
                    "say",
                    "his",
                    "get",
                    "she",
                    "can",
                    "all",
                    "who",
                    "see",
                    "her",
                    "out",
                    "one",
                    "him",
                    "how",
                    "now",
                    "our",
                    "way",
                    "two",
                    "use",
                    "man",
                    "day",
                    "new",
                    "any",
                    "why",
                    "try",
                    "let",
                    "too",
                    "may",
                    "ask",
                    "put",
                    "big",
                    "own",
                    "old",
                    "yes",
                    "its",
                    "off",
                    "few",
                    "run",
                    "guy",
                    "lot",
                    "job",
                    "bad",
                    "pay",
                    "far",
                    "kid",
                    "yet",
                    "hey",
                    "end",
                    "sit",
                    "car",
                    "ago",
                    "set",
                    "win",
                    "boy",
                    "add",
                    "die",
                    "buy",
                    "off",
                    "low",
            };
}
