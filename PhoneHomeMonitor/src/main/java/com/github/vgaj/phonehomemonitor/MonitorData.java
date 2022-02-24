package com.github.vgaj.phonehomemonitor;

public class MonitorData
{
    // Maximum number of messgaes to store
    private final int MSG_COUNT = 50;

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

    public String getData()
    {
        StringBuffer sb = new StringBuffer();
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

    // TODO: Generate HTML report with domain names looked up
}
