package com.github.vgaj.phonehomemonitor;

import java.util.HashMap;
import java.util.Map;

public class MonitorData
{
    // Stats for each host
    private Map<Host, Integer> data = new HashMap<>();

    // Maximum number of messages to store
    private final int MSG_COUNT = 10;

    // Where the next message will go
    private int msgIndex = 0;

    // The ring buffer of messages
    private String[] messages = new String[MSG_COUNT];

    public void addMessage(String msg)
    {
        messages[msgIndex] = msg;
        msgIndex = getNext(msgIndex);
    }

    private int getNext(int i)
    {
        return (i == (MSG_COUNT - 1) ? 0 : i+1);
    }

    public synchronized String getData()
    {
        StringBuffer sb = new StringBuffer();

        // The data
        sb.append("<h3>Data sent to each host</h3>");
        data.forEach( (k,v) ->
        {
            // TODO: Take lookup outside the synchronized block
            sb.append(k.toHostString() + " (" + v + " bytes)" + "<br/>");
        });

        // The messages
        sb.append("<br/><h3>Last few messages</h3>");
        int i = msgIndex;
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
    public synchronized void addData(Host host, int length)
    {
        // TODO: Needs to be thread safe
        if (data.containsKey(host))
        {
            data.replace(host, length + data.get(host));
        }
        else
        {
            data.put(host, length);
        }
    }

    // TODO: Hourly generate XML report with domain names looked up

}
