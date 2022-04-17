package com.github.vgaj.phonehomemonitor.data;

import com.github.vgaj.phonehomemonitor.logic.DataAnalyser;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // TODO: Maybe sort by reverse domain name
        data.forEach( (k,v) ->
        {
            // TODO: Maybe to 10 by total bytes and by frequency (ordered)
            sb.append(k.getHostString() + " (" + v.getTotalBytes() + " total bytes, " + v.getMinuteBlockCount() + " times)" + "<br/>");

            Map<Integer,Integer> requestsOfSameSize = new DataAnalyser(this).getRequestsOfSameSize(k);
            if (requestsOfSameSize.size() > 0)
            {
                requestsOfSameSize.entrySet().forEach(e -> sb.append("&nbsp;&nbsp;").append(e.getKey()).append(" bytes ").append(e.getValue()).append(" times<br/>"));
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

    // TODO: Add raw data and create lookup by address
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

    // TODO: Hourly generate XML report with domain names looked up

}
