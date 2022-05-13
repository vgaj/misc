package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.DataForAddress;
import com.github.vgaj.phonehomemonitor.data.MessageData;
import com.github.vgaj.phonehomemonitor.data.MonitorData;
import com.github.vgaj.phonehomemonitor.data.RemoteAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    // TODO: Move business logic out, including use of config parameters

    // TODO: Move to YAML configuration?
    @Value("${phm.minimum.interval.minutes}")
    private Integer minIntervalMinutes;

    // The minimum number of transmissions at an interval that is of interest
    @Value("${phm.minimum.count.at.interval}")
    private Integer minCountAtInterval;

    @Value("${phm.display.maximum.data}")
    private Integer maxDataToShow;

    @Value("${phm.display.same.size}")
    private Boolean showSameSizeData;

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

        entries.forEach( entryForAddress ->
        {
            // Note that both calls below will sort this list
            List<Map.Entry<Long, Integer>> dataForAddress = monitorData.getCopyOfPerMinuteData(entryForAddress.getKey());

            Map<Integer,List<Integer>> intervalsBetweenData = dataAnalyser.getIntervalsBetweenData(dataForAddress);
            if (intervalsBetweenData.size() > 0 && intervalsBetweenData.entrySet().stream()
                    .allMatch(entryForFrequency -> entryForFrequency.getKey() >= minIntervalMinutes))
            {
                populateHostRow(sb, entryForAddress);
                sb.append("intervals between data: ").append("<br/>");
                intervalsBetweenData.entrySet().stream()
                        .filter(e -> minCountAtInterval > 1 ? e.getValue().size() >  minCountAtInterval : true)
                        .sorted((entry1, entry2) ->
                        {
                            Integer size1 = entry1.getValue().size();
                            Integer size2 = entry2.getValue().size();
                            return size1.compareTo(size2);
                        })
                        .forEach(entry -> sb.append("&nbsp;&nbsp;")
                                .append(entry.getKey())
                                .append(" min, ")
                                .append(entry.getValue().size())
                                .append(" times<br/>"));

                // TODO: Move business logic out of this class

                // TODO: Check if all (or most) are the same size
                // TODO: Check if all the same interval (or most)
                // TODO: Check if average interval is roughly (total run time / number of times)
                // TODO: Check if last reading is less than 2 x Average interval ago

                // TODO: start capturing data when it is interesting

                // Note that we are only looking at repeats sizes if there are large intervals
                Map<Integer,Integer> dataOfSameSize = dataAnalyser.getDataOfSameSize(dataForAddress);
                if (dataOfSameSize.size() > 0 && showSameSizeData)
                {
                    sb.append("repeated data sizes: ").append("<br/>");
                    dataOfSameSize.entrySet().forEach(e1 -> sb.append("&nbsp;&nbsp;").append(e1.getKey()).append(" bytes ").append(e1.getValue()).append(" times<br/>"));
                }

                if (maxDataToShow > 0)
                {
                    sb.append("last ").append(maxDataToShow).append(" data points: ").append("<br/>");
                    sb.append(entryForAddress.getValue().getPerMinuteDataForDisplay(maxDataToShow));
                }
            }

        });

        // The messages
        sb.append("<br/><h3>Last few messages</h3>");
        messageData.getMessages().stream().forEach(m -> sb.append(m).append("<br/>"));
        return sb.toString();
    }

    private void populateHostRow(StringBuffer sb, Map.Entry<RemoteAddress, DataForAddress> e)
    {
        sb.append("<b>")
                .append(e.getKey().getHostString())
                .append("</b> (")
                .append(e.getValue().getTotalBytes())
                .append(" total bytes, ")
                .append(e.getValue().getMinuteBlockCount())
                .append(" times) <br/>");
    }
}
