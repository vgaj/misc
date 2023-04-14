package com.github.vgaj.phonehomemonitor;
import com.github.vgaj.phonehomemonitor.logic.Presentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhoneHomeMonitorController
{
    @Autowired
    private Presentation presentation;

    @GetMapping("/")
    public String index()
    {
        // TODO: Avoid running as root - (1) Periodically generate XML report to a file and format it to HTML
        // TODO: Avoid running as root - (2) Have a separate web and query the service via a domain socket
        try
        {
            return presentation.getDisplayContent();
        }
        catch (Exception e)
        {
            return e.toString();
        }
    }
}