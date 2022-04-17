package com.github.vgaj.phonehomemonitor;
import com.github.vgaj.phonehomemonitor.data.MonitorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhoneHomeMonitorController
{
    @Autowired
    MonitorData monitorData;

    @GetMapping("/")
    public String index()
    {
        // TODO: Periodically generate XML report to a file and format it to HTML to avoid running a web app as root
        // TODO: Go through some manager
        return monitorData.getDisplayContent();
    }
}