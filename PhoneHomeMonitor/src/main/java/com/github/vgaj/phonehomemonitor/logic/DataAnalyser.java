package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.MonitorData;
import com.github.vgaj.phonehomemonitor.data.RemoteAddress;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Logic to analyse data for a given address
 */
@Component
public class DataAnalyser
{
    private MonitorData monitorData;
    public DataAnalyser(MonitorData monitorData)
    {
        this.monitorData = monitorData;
    }

    public Map<Integer,Integer> getRequestsOfSameSize(RemoteAddress address)
    {
        Map<Integer,Integer> results = new HashMap<>();

        List<Map.Entry<Long, Integer>> dataForAddress = monitorData.getPerMinuteData(address);
        Collections.sort(dataForAddress, new Comparator<Map.Entry<Long, Integer>>() {
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
}
