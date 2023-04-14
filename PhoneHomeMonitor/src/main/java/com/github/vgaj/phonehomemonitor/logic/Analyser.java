package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.DataForAddress;
import com.github.vgaj.phonehomemonitor.data.MonitorData;
import com.github.vgaj.phonehomemonitor.data.RemoteAddress;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

// TODO: comment classes
@Component
public class Analyser
{
    @Getter
    public class AnalysisResult
    {
        private boolean criteriaMatch;
        private List<String> intervalsBetweenData = new ArrayList<>();
        private List<String> repeatedDataSizes = new ArrayList<>();
    }

    @Autowired
    private MonitorData monitorData;

    // TODO: Move to YAML configuration?
    @Value("${phm.minimum.interval.minutes}")
    private Integer minIntervalMinutes;

    // The minimum number of transmissions at an interval that is of interest
    @Value("${phm.minimum.count.at.interval}")
    private Integer minCountAtInterval;

    @Value("${phm.display.same.size}")
    private Boolean showSameSizeData;

    public AnalysisResult analyse(Map.Entry<RemoteAddress, DataForAddress> entryForAddress)
    {
        AnalysisResult result = new AnalysisResult();
        result.criteriaMatch = false;

        // Note that both calls below will sort this list
        List<Map.Entry<Long, Integer>> dataForAddress = monitorData.getCopyOfPerMinuteData(entryForAddress.getKey());

        Map<Integer,List<Integer>> intervalsBetweenData = getIntervalsBetweenData(dataForAddress);

        // TODO: Check if all (or most) are the same size
        // TODO: Check if all the same interval (or most)
        // TODO: Check if average interval is roughly (total run time / number of times)
        // TODO: Check if last reading is less than 2 x Average interval ago

        // TODO: +/- 1 minute should be the same interval
        // TODO: If same interval + same size is more interesting

        // TODO: start capturing data when it is interesting


        if (intervalsBetweenData.size() > 0 && intervalsBetweenData.entrySet().stream()
                .allMatch(entryForFrequency -> entryForFrequency.getKey() >= minIntervalMinutes))
        {
            result.criteriaMatch = true;

            intervalsBetweenData.entrySet().stream()
                    .filter(e -> minCountAtInterval > 1 ? e.getValue().size() >  minCountAtInterval : true)
                    .sorted((entry1, entry2) ->
                    {
                        Integer size1 = entry1.getValue().size();
                        Integer size2 = entry2.getValue().size();
                        return size1.compareTo(size2);
                    })
                    .forEach(entry -> result.intervalsBetweenData.add(entry.getKey() + " min, " + entry.getValue().size() + " times"));


            // Note that we are only looking at repeats sizes if there are large intervals
            Map<Integer,Integer> dataOfSameSize = getDataOfSameSize(dataForAddress);
            if (dataOfSameSize.size() > 0 && showSameSizeData)
            {
                dataOfSameSize.entrySet().forEach(e1 -> result.repeatedDataSizes.add(e1.getKey() + " bytes " + e1.getValue() + " times"));
            }
        }
        return result;
    }

    public Map<Integer,Integer> getDataOfSameSize(List<Map.Entry<Long, Integer>> dataForAddress)
    {
        Map<Integer,Integer> results = new HashMap<>();

        Collections.sort(dataForAddress, new Comparator<Map.Entry<Long, Integer>>()
        {
            @Override
            public int compare(Map.Entry<Long, Integer> e1, Map.Entry<Long, Integer> e2) {
                return e1.getValue().compareTo(e2.getValue());
            }
        });

        int lastByteCount = -1;
        for (var e : dataForAddress)
        {
            if (e.getValue() == lastByteCount)
            {
                results.put(lastByteCount, results.getOrDefault(lastByteCount, 1) + 1);
            }
            lastByteCount = e.getValue();
        }
        return results;
    }

    public Map<Integer,List<Integer>> getIntervalsBetweenData(List<Map.Entry<Long, Integer>> dataForAddress)
    {
        // Map interval (minutes) to list to lengths of data at this interval
        Map<Integer,List<Integer>> results = new HashMap<>();

        // TODO: Make a copy of the list.  Not strictly necessary but more correct.
        Collections.sort(dataForAddress, new Comparator<Map.Entry<Long, Integer>>()
        {
            @Override
            public int compare(Map.Entry<Long, Integer> e1, Map.Entry<Long, Integer> e2)
            {
                return e1.getKey().compareTo(e2.getKey());
            }});

        long lastRequest = -1;
        for (var e : dataForAddress)
        {
            if (lastRequest != -1)
            {
                int interval = (int) (e.getKey() - lastRequest);
                results.putIfAbsent(interval, new ArrayList<>());
                results.get(interval).add(e.getValue());
            }
            lastRequest = e.getKey();
        }
        return results;
    }

}
