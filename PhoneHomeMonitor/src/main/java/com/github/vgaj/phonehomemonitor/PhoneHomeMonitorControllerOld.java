package com.github.vgaj.phonehomemonitor;
import com.github.vgaj.phonehomemonitor.logic.Presentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhoneHomeMonitorControllerOld
{
    @Autowired
    private Presentation presentation;

    @GetMapping("/old")
    public String index()
    {
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