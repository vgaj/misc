package com.github.vgaj.phonehomemonitor;

import com.github.vgaj.phonehomemonitor.logic.Analyser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class DataAnalyserTests
{
	// TODO: move to test
	@Test
	void dataOfSameSize()
	{
		List<Map.Entry<Long, Integer>> data = new ArrayList<>();
		long time = 1L;
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(time++,100));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(time++,200));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(time++,300));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(time++,100));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(time++,200));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(time++,200));
		Map<Integer,Integer> result = new Analyser().getDataOfSameSize(data);
		assert result.size() == 2;
		assert result.get(100) == 2;
		assert result.get(200) == 3;
	}

	@Test
	void noDataOfSameSize()
	{
		List<Map.Entry<Long, Integer>> data = new ArrayList<>();
		long time = 1L;
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(time++,100));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(time++,200));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(time++,300));
		Map<Integer,Integer> result = new Analyser().getDataOfSameSize(data);
		assert result.size() == 0;
	}

	@Test
	void getIntervals()
	{
		List<Map.Entry<Long, Integer>> data = new ArrayList<>();
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(1L,100));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(11L,100)); // gap = 10
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(21L,100)); // gap = 10
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(26L,100)); // gap = 5
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(36L,100)); // gap = 10
		Map<Integer,List<Integer>> result = new Analyser().getIntervalsBetweenData(data);
		assert result.size() == 2;
		assert result.get(5).size() == 1;
		assert result.get(10).size() == 3;
	}

	@Test
	void oneEntrySoNoIntervals()
	{
		List<Map.Entry<Long, Integer>> data = new ArrayList<>();
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(1L,100));
		Map<Integer,List<Integer>> result = new Analyser().getIntervalsBetweenData(data);
		assert result.isEmpty();
	}
}
