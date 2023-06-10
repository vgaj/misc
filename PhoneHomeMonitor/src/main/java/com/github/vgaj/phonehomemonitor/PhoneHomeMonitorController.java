package com.github.vgaj.phonehomemonitor;

import com.github.vgaj.phonehomemonitor.display.DisplayDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PhoneHomeMonitorController
{
    @Autowired
    private DisplayDataGenerator displayDataGenerator;

    @GetMapping("/")
    //public String index(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
    public String index(Model model)
    {
        // TODO: Avoid running as root - (1) Periodically generate XML report to a file and format it to HTML
        // TODO: Avoid running as root - (2) Have a separate web and query the service via a domain socket
        model.addAttribute("content", displayDataGenerator.getDisplayContent());
        return "index";
    }

    @GetMapping("/data")
    public String data(@RequestParam(name="address", required=false, defaultValue="") String address, Model model)
    {
        List<String> data = null;
        try
        {
            data = displayDataGenerator.getData(InetAddress.getByName(address));
        } catch (Throwable t)
        {
            // TODO log
            data = new ArrayList<>();
        }
        model.addAttribute("content", data);
        return "data";
    }

}