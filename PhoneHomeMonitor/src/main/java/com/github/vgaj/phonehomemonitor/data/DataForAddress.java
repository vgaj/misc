package com.github.vgaj.phonehomemonitor.data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DataForAddress
{
    private int totalBytes = 0;

    // Number of bytes in minute blocks
    private final ConcurrentMap<Long, Integer> byteCountPerMinute = new ConcurrentHashMap<>();

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
        // TODO: Show real local time
        byteCountPerMinute.entrySet()
                .stream()
                .sorted((e1, e2) -> ((Long) e1.getKey()).compareTo(((Long) e2.getKey())))


                .forEach(e -> sb.append("&nbsp;&nbsp;").append(e.getKey()).append(':').append(e.getValue()).append("<br/>").append(System.lineSeparator()));
        return sb.toString();
    }

}
