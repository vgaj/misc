package com.github.vgaj.phonehomemonitor.data;

import java.util.*;

public class DataForAddress
{
    private int totalBytes = 0;

    // Number of bytes in minute blocks
    private final Map<Long, Integer> byteCountPerMinute = new HashMap<>();


    public DataForAddress(int totalBytes, long epochMinute)
    {
        addBytes(totalBytes,epochMinute);
    }

    public void addBytes(int count, long epochMinute)
    {
        totalBytes += count;
        byteCountPerMinute.put(epochMinute,
                count + (byteCountPerMinute.containsKey(epochMinute) ? byteCountPerMinute.get(epochMinute) : 0));
    }
    public int getTotalBytes()
    {
        return totalBytes;
    }

    public int getMinuteBlockCount()
    {
        return byteCountPerMinute.keySet().size();
    }

    public List<Map.Entry<Long, Integer>> getPerMinuteData()
    {
        return new ArrayList<>(byteCountPerMinute.entrySet());
    }

    public String getPerMinuteDataForDisplay()
    {
        StringBuilder sb = new StringBuilder();
        byteCountPerMinute.entrySet().forEach(e -> sb.append(e.getKey()).append(':').append(e.getValue()).append("<br/>").append(System.lineSeparator()));
        return sb.toString();
    }

}
