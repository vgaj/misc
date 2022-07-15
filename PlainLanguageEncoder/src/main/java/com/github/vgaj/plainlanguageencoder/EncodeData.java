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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the map used by LanguageEncodedInputStream and LanguageEncodedOutputStream
 * to encode and decode data.  It is programmatically generated.
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

        // Create a mapping for each Base64 character
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

    // Raw dataset of words that will be used in the encoding.
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
                    "few",
                    "run",
                    "guy",
                    "lot",
            };
}
