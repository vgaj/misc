package com.github.vgaj.phonehomemonitor;

import com.github.vgaj.phonehomemonitor.data.MonitorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MonitorService
{
    private MonitorTask monitorTask;
    private Thread monitorThread;

    @Autowired
    private MonitorData monitorData;

    @Autowired
    private AddressLookupTask addressLookupTask;

    @PostConstruct
    public void init()
    {
        // TODO: Can this be turned into a Spring Component?
        monitorTask = new MonitorTask(monitorData);
        monitorThread = new Thread(monitorTask);
        monitorThread.start();
        addressLookupTask.setData(monitorData);
    }

    public String getMsg()
    {
        // TODO: Periodically generate XML report to a file and format it to HTML
        return monitorTask.getMsg();
    }
}
