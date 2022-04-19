package com.github.vgaj.phonehomemonitor.data;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class MonitorData
{
    // TODO: Periodic cleanup of uninteresting data

    // Stats for each host
    private final ConcurrentMap<RemoteAddress, DataForAddress> data = new ConcurrentHashMap<>();

    public void populateHostNames()
    {
        data.keySet().forEach(k -> k.lookupAndGetHostString());
    }

    public List<Map.Entry<Long, Integer>> getPerMinuteData(RemoteAddress address)
    {
        return data.get(address).getPerMinuteData();
    }

    public void addData(RemoteAddress host, int length, long epochMinute)
    {
        // TODO: Requires host is not null
        data.putIfAbsent(host, new DataForAddress());
        data.get(host).addBytes(length, epochMinute);
    }

    public ArrayList<Map.Entry<RemoteAddress, DataForAddress>> getRawData()
    {
        ArrayList<Map.Entry<RemoteAddress, DataForAddress>> entries = new ArrayList<>();
        data.entrySet().forEach(e -> entries.add(e));
        return entries;
    }
}
