package com.github.vgaj.phonehomemonitor;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MonitorService
{
    private MonitorTask monitorTask;
    private Thread monitorThread;

    @PostConstruct
    public void init()
    {
        monitorTask = new MonitorTask();
        monitorThread = new Thread(monitorTask);
        monitorThread.start();
    }

    public String getMsg()
    {
        return monitorTask.getMsg();
    }
}
