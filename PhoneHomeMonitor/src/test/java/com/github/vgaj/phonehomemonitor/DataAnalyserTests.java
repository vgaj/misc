package com.github.vgaj.phonehomemonitor;

import com.github.vgaj.phonehomemonitor.logic.DataAnalyser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class DataAnalyserTests
{
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
		Map<Integer,Integer> result = new DataAnalyser().getDataOfSameSize(data);
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
		Map<Integer,Integer> result = new DataAnalyser().getDataOfSameSize(data);
		assert result.size() == 0;
	}

	@Test
	void minimumInterval()
	{
		List<Map.Entry<Long, Integer>> data = new ArrayList<>();
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(1L,100));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(11L,100));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(21L,100));
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(26L,100)); // gap = 5
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(36L,100));
		Optional<Long> result = new DataAnalyser().minimumIntervalBetweenData(data);
		assert result.isPresent();
		assert result.get() == 5;
	}

	@Test
	void noMinimumInterval()
	{
		List<Map.Entry<Long, Integer>> data = new ArrayList<>();
		data.add(new AbstractMap.SimpleEntry<Long, Integer>(1L,100));
		Optional<Long> result = new DataAnalyser().minimumIntervalBetweenData(data);
		assert result.isEmpty();
	}
}
