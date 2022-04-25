package com.github.vgaj.phonehomemonitor.logic;

import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Logic to analyse data for a given address
 */
@Component
public class DataAnalyser
{
    public Map<Integer,Integer> getDataOfSameSize(List<Map.Entry<Long, Integer>> dataForAddress)
    {
        Map<Integer,Integer> results = new HashMap<>();

        Collections.sort(dataForAddress, new Comparator<Map.Entry<Long, Integer>>()
        {
            @Override
            public int compare(Map.Entry<Long, Integer> e1, Map.Entry<Long, Integer> e2) {
                return e1.getValue().compareTo(e2.getValue());
            }
        });

        int lastByteCount = -1;
        for (var e : dataForAddress)
        {
            if (e.getValue() == lastByteCount)
            {
                results.put(lastByteCount, results.getOrDefault(lastByteCount, 1) + 1);
            }
            lastByteCount = e.getValue();
        }
        return results;
    }

    public Map<Integer,List<Integer>> getIntervalsBetweenData(List<Map.Entry<Long, Integer>> dataForAddress)
    {
        // Map interval (minutes) to list to lengths of data at this interval
        Map<Integer,List<Integer>> results = new HashMap<>();

        // TODO: Make a copy of the list.  Not strictly necessary but more correct.
        Collections.sort(dataForAddress, new Comparator<Map.Entry<Long, Integer>>()
        {
            @Override
            public int compare(Map.Entry<Long, Integer> e1, Map.Entry<Long, Integer> e2)
            {
                return e1.getKey().compareTo(e2.getKey());
            }});

        long lastRequest = -1;
        for (var e : dataForAddress)
        {
            if (lastRequest != -1)
            {
                int interval = (int) (e.getKey() - lastRequest);
                results.putIfAbsent(interval, new ArrayList<>());
                results.get(interval).add(e.getValue());
            }
            lastRequest = e.getKey();
        }
        return results;
    }
}
