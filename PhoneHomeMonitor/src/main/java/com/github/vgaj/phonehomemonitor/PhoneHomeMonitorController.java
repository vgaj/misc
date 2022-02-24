package com.github.vgaj.phonehomemonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhoneHomeMonitorController
{
    @Autowired
    MonitorService svc;

    @GetMapping("/")
    public String index()
    {
        // TODO: Return data and do some very basic HTML formatting
        return svc.getMsg();
    }

}