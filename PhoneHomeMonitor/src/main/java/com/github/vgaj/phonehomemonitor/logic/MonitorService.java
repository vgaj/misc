package com.github.vgaj.phonehomemonitor.logic;

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
        // TODO: Add save and reload on restart
        // TODO: Can this be turned into a Spring Component?
        monitorTask = new MonitorTask(monitorData);
        monitorThread = new Thread(monitorTask);
        monitorThread.start();
        addressLookupTask.setData(monitorData);
    }
}
