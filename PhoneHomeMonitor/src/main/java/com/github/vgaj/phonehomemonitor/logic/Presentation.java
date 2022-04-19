package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.DataForAddress;
import com.github.vgaj.phonehomemonitor.data.MessageData;
import com.github.vgaj.phonehomemonitor.data.MonitorData;
import com.github.vgaj.phonehomemonitor.data.RemoteAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

@Component
public class Presentation
{
    @Autowired
    private MonitorData monitorData;

    @Autowired
    private MessageData messageData;

    @Autowired
    private DataAnalyser dataAnalyser;

    public String getDisplayContent()
    {
        StringBuffer sb = new StringBuffer();

        // The data
        sb.append("<h3>Data sent to each host</h3>");
        ArrayList<Map.Entry<RemoteAddress, DataForAddress>> entries = monitorData.getRawData();
        Collections.sort(entries, new Comparator<Map.Entry<RemoteAddress, DataForAddress>>() {
            @Override
            public int compare(Map.Entry<RemoteAddress, DataForAddress> e1, Map.Entry<RemoteAddress, DataForAddress> e2)
            {
                if (e1.getKey() == null || e1.getKey().getReverseHostname() == null || e2.getKey() == null || e2.getKey().getReverseHostname() == null)
                {
                    return 0;
                }
                else
                {
                    return e1.getKey().getReverseHostname().compareTo(e2.getKey().getReverseHostname());
                }
            }
        });

        entries.forEach( e ->
        {
            // TODO: Maybe top 10 by total bytes and by frequency (ordered)
            sb.append(e.getKey().getHostString() + " (" + e.getValue().getTotalBytes() + " total bytes, " + e.getValue().getMinuteBlockCount() + " times)" + "<br/>");

            Map<Integer,Integer> requestsOfSameSize = dataAnalyser.getRequestsOfSameSize(e.getKey());
            if (requestsOfSameSize.size() > 0)
            {
                requestsOfSameSize.entrySet().forEach(e1 -> sb.append("&nbsp;&nbsp;").append(e1.getKey()).append(" bytes ").append(e1.getValue()).append(" times<br/>"));
                //sb.append(v.getPerMinuteDataForDisplay());
            }
        });

        // The messages
        sb.append("<br/><h3>Last few messages</h3>");
        messageData.getMessages().stream().forEach(m -> sb.append(m).append("<br/>"));
        return sb.toString();
    }
}
