package com.github.vgaj.phonehomemonitor.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageData
{
    // TODO: Save data and reload on restart

    Logger logger = LoggerFactory.getLogger(this.getClass());

    // Maximum number of messages to store
    private final int MSG_COUNT = 10;

    // Where the next message will go
    private int msgIndex = 0;

    // The ring buffer of messages
    private final String[] messages = new String[MSG_COUNT];

    public void addMessage(String msg)
    {
        logger.info(msg);
        messages[msgIndex] = msg;
        msgIndex = getNext(msgIndex);
    }

    private int getNext(int i)
    {
        return (i == (MSG_COUNT - 1) ? 0 : i+1);
    }

    public List<String> getMessages()
    {
        ArrayList<String> results = new ArrayList<>(MSG_COUNT);
        int i = msgIndex;
        for (int x = 0; x < MSG_COUNT; x++)
        {
            if (messages[i] != null)
            {
                results.add(messages[i]);
            }
            i = getNext(i);
        }
        return results;
    }
}
