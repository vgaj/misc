package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.MonitorData;
import com.github.vgaj.phonehomemonitor.data.RemoteAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

// TODO: comment classes
@Component
public class Analyser
{
    @Autowired
    private MonitorData monitorData;

    @Value("${phm.minimum.interval.minutes}")
    private Integer minIntervalMinutes;

    // The minimum number of transmissions at an interval that is of interest
    @Value("${phm.minimum.count.at.interval}")
    private Integer minCountAtInterval;

    @Value("${phm.display.same.size}")
    private Boolean showSameSizeData;

    /**
     * This is the logic which analyses the data for a given host
     * @param address The address to do the analysis for
     * @return Structure containing the results of the analysis
     */
    public AnalysisResult analyse(RemoteAddress address)
    {
        AnalysisResult result = new AnalysisResult();
        result.setMinimalCriteriaMatch(false);

        // List of:
        //  - time
        //  - length of data at that time
        // Note that both calls below will sort this list
        List<Map.Entry<Long, Integer>> dataForAddress = monitorData.getCopyOfPerMinuteData(address);

        // Map of interval (minutes) to list to lengths of data at this interval
        Map<Integer,List<Integer>> intervalsBetweenData = getIntervalsBetweenData(dataForAddress);

        // TODO: +/- 1 minute should be the same interval

        // TODO: start capturing more data when it is interesting

        //=============
        // Pre-criteria: We are only interested in looking at hosts where every interval between
        // data is greater than the configured minimum.
        // This should reduce web browsing traffic getting captured.
        if (intervalsBetweenData.size() > 0 &&
                intervalsBetweenData.entrySet().stream().allMatch(entryForFrequency -> entryForFrequency.getKey() >= minIntervalMinutes))
        {
            result.setMinimalCriteriaMatch(true);

            //=============
            // Criteria 1.1: All transfers are at the same interval
            if (intervalsBetweenData.size() == 1)
            {
                result.setAllTransfersAtSameInterval_c11((intervalsBetweenData.entrySet().stream().findFirst().get().getKey()));
            }

            //=============
            // Criteria 1.2: Repeated transfers at the same interval
            List<Map.Entry<Integer,List<Integer>>> sortedEntries = intervalsBetweenData.entrySet().stream()
                    .filter(e -> minCountAtInterval > 1 ? e.getValue().size() > minCountAtInterval : true)
                    .sorted((entry1, entry2) ->
                    {
                        Integer size1 = entry1.getValue().size();
                        Integer size2 = entry2.getValue().size();
                        return size1.compareTo(size2);
                    })
                    .collect(Collectors.toList());
            sortedEntries.forEach(entry -> result.addIntervalFrequency_c12(entry.getKey(), entry.getValue().size()));

            // TODO: Check if all the same interval (or most)
            // TODO: Check if average interval is roughly (total run time / number of times)
            // TODO: Check if last reading is less than 2 x Average interval ago

            //=============
            // Criteria 2.1: All data is of the same size
            Map<Integer, Long> dataFrequencies = getDataSizeFrequenciesFromRaw(dataForAddress);
            if (dataFrequencies.size() == 1)
            {
                int sameSizeBytes = dataFrequencies.entrySet().stream().findFirst().get().getKey();
                result.setAllDataIsSameSize_c21(sameSizeBytes);
            }
            // TODO: Check if all sizes are similar - look at Std Dev

            //=============
            // Criteria 2.2: Repeated transfers of the same size
            Map<Integer, Long> dataOfSameSize = getDataOfSameSizeFromRaw(dataForAddress);
            if (dataOfSameSize.size() > 0 && showSameSizeData)
            {
                dataOfSameSize.entrySet().forEach(e1 -> result.addTransferSizeFrequency_c22(e1.getKey(),e1.getValue()));
            }

        }
        return result;
    }

    /**
     * Helper to get a map of interval (minutes) to list to lengths of data at this interval
     * @param dataForAddress Raw data for the destination
     */
    private  Map<Integer,List<Integer>> getIntervalsBetweenData(List<Map.Entry<Long, Integer>> dataForAddress)
    {
        Map<Integer,List<Integer>> results = new HashMap<>();

        Collections.sort(dataForAddress, new Comparator<Map.Entry<Long, Integer>>()
        {
            @Override
            public int compare(Map.Entry<Long, Integer> e1, Map.Entry<Long, Integer> e2)
            {
                return e1.getKey().compareTo(e2.getKey());
            }
        });

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

    /**
     * Get a map of data length to number of transfers of that length
     * @param dataSizes Raw data - data sizes sent to the destination
     */
    private Map<Integer,Long> getDataSizeFrequencies(List<Integer> dataSizes)
    {
        return dataSizes.stream().collect(Collectors.groupingBy(s -> s, Collectors.counting()));
    }
    private Map<Integer,Long> getDataSizeFrequenciesFromRaw(List<Map.Entry<Long, Integer>> dataForAddress)
    {
        return getDataSizeFrequencies( dataForAddress.stream().map(e -> e.getValue()).collect(Collectors.toList()));
    }

    /**
     * Get data of the same size.
     * Get instances where data of the same size has been sent multiple times
     * @param dataForAddress Raw data for destination
     */
    private Map<Integer,Long> getDataOfSameSizeFromRaw(List<Map.Entry<Long, Integer>> dataForAddress)
    {
        List<Integer> dataSizes =  dataForAddress.stream().map(e -> e.getValue()).collect(Collectors.toList());
        Map<Integer,Long> dataFrequencies = getDataSizeFrequencies(dataSizes);
        return dataFrequencies.entrySet().stream().filter(e -> e.getValue() > 1).collect(Collectors.toMap(e-> e.getKey(), e -> e.getValue()));
    }

}
