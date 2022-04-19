package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.MonitorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Background task to lookup hostnames associated with addresses
 */
@Component
public class AddressLookupTask
{
    @Autowired
    private MonitorData monitorData;

    @Scheduled(fixedDelay = 60000)
    public void populateHostNames()
    {
        monitorData.populateHostNames();
    }
}
