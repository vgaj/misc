package com.github.vgaj.phonehomemonitor.result;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class AnalysisResultImpl implements AnalysisResult {
    // TODO: Introduce the concept of a score
    // TODO: If same interval + same size is more interesting

    @Getter
    @Setter
    private boolean minimalCriteriaMatch;

    /**
     * c11 - If all intervals are the same then this will be set
     */
    private OptionalInt c11AllTransfersAtSameInterval = OptionalInt.empty();
    public void setAllTransfersAtSameInterval_c11(int interval)
    {
        c11AllTransfersAtSameInterval = OptionalInt.of(interval);
    }
    @Override
    public int getIntervalOfAllTransfers_c11()
    {
        return c11AllTransfersAtSameInterval.getAsInt();
    }
    @Override
    public boolean areAllIntervalsTheSame_c11()
    {
        return c11AllTransfersAtSameInterval.isPresent();
    }

    /**
     * c21 - If all transfers are the same size then this will be set
     */
    private OptionalInt c21AllDataIsSameSize = OptionalInt.empty();
    public void setAllDataIsSameSize_c21(int sizeInBytes)
    {
        c21AllDataIsSameSize = OptionalInt.of(sizeInBytes);
    }
    @Override
    public int getSizeOfAllTransfers_c21()
    {
        return c21AllDataIsSameSize.getAsInt();
    }
    @Override
    public boolean areAllTransfersTheSameSize_c21()
    {
        return c21AllDataIsSameSize.isPresent();
    }

    /**
     * c12 - List of: interval in minutes -> number of times
     */
    private List<Map.Entry<Integer,Integer>> c12IntervalsBetweenData = new ArrayList<>();
    public void addIntervalFrequency_c12(int intervalMinutes, int numberOfTimes)
    {
        c12IntervalsBetweenData.add(Map.entry(intervalMinutes, numberOfTimes));
    }
    @Override
    public List<Map.Entry<Integer,Integer>> getRepeatedIntervals_c12()
    {
        return c12IntervalsBetweenData;
    }
    @Override
    public boolean areSomeIntervalsTheSame_c12()
    {
        return c12IntervalsBetweenData.size() > 0;
    }

    /**
     * c22 - List of: sizes of transfers in bytes -> number to times
     */
    private List<Map.Entry<Integer,Long>> c22SomeDataIsSameSizeMessages = new ArrayList<>();
    public void addTransferSizeFrequency_c22(int transferSizeBytes, long numberOfTimes)
    {
        c22SomeDataIsSameSizeMessages.add(Map.entry(transferSizeBytes, numberOfTimes));
    }
    @Override
    public List<Map.Entry<Integer,Long>> getRepeatedTransferSizes_c22()
    {
        return c22SomeDataIsSameSizeMessages;
    }
    @Override
    public boolean areSomeTransfersTheSameSize_c22()
    {
        return c22SomeDataIsSameSizeMessages.size() > 0;
    }

}