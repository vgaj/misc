package com.github.vgaj.phonehomemonitor.result;

import java.util.List;
import java.util.Map;

public interface AnalysisResult
{
    boolean isMinimalCriteriaMatch();
    boolean areAllIntervalsTheSame_c11();
    boolean areSomeIntervalsTheSame_c12();
    boolean areAllTransfersTheSameSize_c21();
    boolean areSomeTransfersTheSameSize_c22();


    int getIntervalOfAllTransfers_c11();
    int getSizeOfAllTransfers_c21();

    List<Map.Entry<Integer, Integer>> getRepeatedIntervals_c12();

    List<Map.Entry<Integer, Long>> getRepeatedTransferSizes_c22();
}
