package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.DataForAddress;
import com.github.vgaj.phonehomemonitor.data.MessageData;
import com.github.vgaj.phonehomemonitor.data.MonitorData;
import com.github.vgaj.phonehomemonitor.data.RemoteAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class is responsible for generating the HTML of the result of the analysis
 * getDisplayContent() returns the full HTML content to display to the user
 */
@Component
public class Presentation
{
    @Autowired
    private MonitorData monitorData;

    @Autowired
    private MessageData messageData;

    @Autowired
    private Analyser analyser;

    // TODO: Move to YAML configuration?
    @Value("${phm.display.maximum.data}")
    private Integer maxDataToShow;

    // TODO: Look at Thymeleaf to generate content
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
            AnalysisResult result = analyser.analyse(entryForAddress.getKey());
            if (result.isMinimalCriteriaMatch())
            {
                populateHostRow(sb, entryForAddress);

                if (result.areAllIntervalsTheSame_c11())
                {
                    sb.append("- all intervals are " + result.getAllTransfersAtSameInterval_c11() + " minutes").append("<br/>");
                }

                if (result.areSomeIntervalsTheSame_c12())
                {
                    sb.append("- intervals between data: ").append("<br/>");
                    result.getRepeatedIntervalsBetweenData_c12().forEach(r ->
                            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;")
                                    .append(r.getKey())
                                    .append(" min, ")
                                    .append(r.getValue())
                                    .append(" times")
                                    .append("<br/>"));

                }
                if (result.areAllTransfersTheSameSize_c21())
                {
                    sb.append("- all transfers are " + result.getAllDataIsSameSize_c21() + " bytes").append("<br/>");
                }
                if (result.areSomeTransfersTheSameSize_c22())
                {
                    sb.append("- repeated data sizes: ").append("<br/>");
                    result.getRepeatedTransferSizes_c22().forEach(r ->
                            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;")
                                    .append(r.getKey())
                                    .append(" bytes, ")
                                    .append(r.getValue())
                                    .append(" times")
                                    .append("<br/>"));
                }
                if (maxDataToShow > 0)
                {
                    sb.append("- last ").append(maxDataToShow).append(" data points: ").append("<br/>");
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
