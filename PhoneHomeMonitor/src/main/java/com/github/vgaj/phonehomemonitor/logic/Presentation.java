package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.DataForAddress;
import com.github.vgaj.phonehomemonitor.data.MessageData;
import com.github.vgaj.phonehomemonitor.data.MonitorData;
import com.github.vgaj.phonehomemonitor.data.RemoteAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
        sb.append("<h3>Unusual Data Sent to Hosts</h3>");
        ArrayList<Map.Entry<RemoteAddress, DataForAddress>> entries = monitorData.getCopyOfRawData();
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

            boolean found = false;
            //sb.append(e.getKey().getHostString() + " (" + e.getValue().getTotalBytes() + " total bytes, " + e.getValue().getMinuteBlockCount() + " times)" + "<br/>");

            // Note the 2 calls below will sort both sort this list
            List<Map.Entry<Long, Integer>> dataForAddress = monitorData.getCopyOfPerMinuteData(e.getKey());

            Optional<Long> minInterval = dataAnalyser.minimumIntervalBetweenData(dataForAddress);
            if (minInterval.isPresent() && minInterval.get() > 1)
            {
                sb.append(e.getKey().getHostString() + " (" + e.getValue().getTotalBytes() + " total bytes, " + e.getValue().getMinuteBlockCount() + " times)" + "<br/>");
                found = true;
                sb.append("&nbsp;&nbsp; Minimum interval between data: ").append(minInterval.get()).append("<br/>");
            }

            Map<Integer,Integer> dataOfSameSize = dataAnalyser.getDataOfSameSize(dataForAddress);
            if (dataOfSameSize.size() > 0)
            {
                if (!found)
                {
                    sb.append(e.getKey().getHostString() + " (" + e.getValue().getTotalBytes() + " total bytes, " + e.getValue().getMinuteBlockCount() + " times)" + "<br/>");
                }
                found = true;
                dataOfSameSize.entrySet().forEach(e1 -> sb.append("&nbsp;&nbsp;").append(e1.getKey()).append(" bytes ").append(e1.getValue()).append(" times<br/>"));

            }
            if (found)
            {
                //sb.append(e.getValue().getPerMinuteDataForDisplay());
            }
        });

        // The messages
        sb.append("<br/><h3>Last few messages</h3>");
        messageData.getMessages().stream().forEach(m -> sb.append(m).append("<br/>"));
        return sb.toString();
    }
}
