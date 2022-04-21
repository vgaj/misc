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

    public Optional<Long> minimumIntervalBetweenData(List<Map.Entry<Long, Integer>> dataForAddress)
    {
        // TODO: Make a copy of the list.  Not strictly necessary but more correct.
        Collections.sort(dataForAddress, new Comparator<Map.Entry<Long, Integer>>()
        {
            @Override
            public int compare(Map.Entry<Long, Integer> e1, Map.Entry<Long, Integer> e2) {
                return e1.getKey().compareTo(e2.getKey());
            }
        });
        Optional<Long> minimumInterval = Optional.empty();
        long lastRequest = -1;

        for (var e : dataForAddress)
        {
            if (lastRequest != -1)
            {
                long interval = e.getKey() - lastRequest;
                // TODO: Keep track of number of times at this minimum
                // TODO: Maybe work out the number of times at each frequency
                // TODO: Calculate average (or most common interval)
                if (minimumInterval.isEmpty() || interval < minimumInterval.get())
                {
                    minimumInterval = Optional.of(interval);
                }
            }
            lastRequest = e.getKey();
        }
        return minimumInterval;
    }
}
