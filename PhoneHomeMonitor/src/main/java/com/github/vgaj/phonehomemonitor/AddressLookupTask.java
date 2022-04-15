package com.github.vgaj.phonehomemonitor;

import com.github.vgaj.phonehomemonitor.data.MonitorData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AddressLookupTask
{
    private Optional<MonitorData> data = Optional.empty();

    public void setData(MonitorData data)
    {
        this.data = Optional.of(data);
    }

    @Scheduled(fixedDelay = 60000)
    public void populateHostNames()
    {
        if (data.isPresent())
        {
            data.get().populateHostNames();
        }
    }
}
