package com.github.vgaj.phonehomemonitor.data;

import com.github.vgaj.phonehomemonitor.logic.DataAnalyser;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MonitorData
{
    // TODO: Periodic cleanup of uninteresting data

    // Stats for each host
    private final Map<RemoteAddress, DataForAddress> data = new HashMap<>();

    // Maximum number of messages to store
    private final int MSG_COUNT = 10;

    // Where the next message will go
    private int msgIndex = 0;

    // The ring buffer of messages
    private final String[] messages = new String[MSG_COUNT];

    public void addMessage(String msg)
    {
        messages[msgIndex] = msg;
        msgIndex = getNext(msgIndex);
    }

    private int getNext(int i)
    {
        return (i == (MSG_COUNT - 1) ? 0 : i+1);
    }

    public void populateHostNames()
    {
        // TODO: Is this thread safe
        data.keySet().forEach(k -> k.lookupAndGetHostString());
    }

    public List<Map.Entry<Long, Integer>> getPerMinuteData(RemoteAddress address)
    {
        return data.get(address).getPerMinuteData();
    }

    // TODO: refactor out
    public synchronized String getDisplayContent()
    {
        StringBuffer sb = new StringBuffer();

        // The data
        sb.append("<h3>Data sent to each host</h3>");
        ArrayList<Map.Entry<RemoteAddress, DataForAddress>> entries = new ArrayList<>();
        data.entrySet().forEach(e -> entries.add(e));
        Collections.sort(entries, new Comparator<Map.Entry<RemoteAddress, DataForAddress>>() {
            @Override
            public int compare(Map.Entry<RemoteAddress, DataForAddress> e1, Map.Entry<RemoteAddress, DataForAddress> e2)
            {
                if (e1.getKey() == null || e2.getKey() == null)
                {
                    return 0;
                }
                else
                {
                    return e1.getKey().compareTo(e2.getKey());
                }
            }
        });

        entries.forEach( e ->
        {
            // TODO: Maybe top 10 by total bytes and by frequency (ordered)
            sb.append(e.getKey().getHostString() + " (" + e.getValue().getTotalBytes() + " total bytes, " + e.getValue().getMinuteBlockCount() + " times)" + "<br/>");

            Map<Integer,Integer> requestsOfSameSize = new DataAnalyser(this).getRequestsOfSameSize(e.getKey());
            if (requestsOfSameSize.size() > 0)
            {
                requestsOfSameSize.entrySet().forEach(e1 -> sb.append("&nbsp;&nbsp;").append(e1.getKey()).append(" bytes ").append(e1.getValue()).append(" times<br/>"));
                //sb.append(v.getPerMinuteDataForDisplay());
            }
        });

        // The messages
        sb.append("<br/><h3>Last few messages</h3>");
        int i = msgIndex;
        // TODO: Correct order
        for (int x = 0; x < MSG_COUNT; x++)
        {
            if (messages[i] != null)
            {
                sb.append(messages[i] + "<br/>");
            }
            i = getNext(i);
        }

        return sb.toString();
    }

    public synchronized void addData(RemoteAddress host, int length, long epochMinute)
    {
        // TODO: Needs to be thread safe
        if (data.containsKey(host))
        {
            data.get(host).addBytes(length, epochMinute);
        }
        else
        {
            data.put(host, new DataForAddress(length, epochMinute));
        }
    }
}
