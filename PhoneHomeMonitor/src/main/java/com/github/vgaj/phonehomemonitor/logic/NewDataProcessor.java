package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.MessageData;
import com.github.vgaj.phonehomemonitor.data.MonitorData;
import com.github.vgaj.phonehomemonitor.data.NewDataEvent;
import com.github.vgaj.phonehomemonitor.util.PcapPacketHelper;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.pcap4j.core.PcapPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * Consumes new captured data via a Distruptor
 */
@Component
public class NewDataProcessor
{
    private static final int BUFFER_SIZE = 65536;
    private static final boolean DEBUG_LOG = false;

    private Disruptor<NewDataEvent> disruptor;

    @Autowired
    private MessageData messageData;

    @Autowired
    private MonitorData monitorData;

    @Autowired
    private PcapPacketHelper pcapHelper;

    private void onEvent(NewDataEvent newDataEvent, long sequence, boolean endOfBatch) throws Exception
    {
        monitorData.addData(newDataEvent.getHost(), newDataEvent.getLength(), newDataEvent.getEpochMinute());
    }

    private void translate(NewDataEvent event, long sequence, PcapPacket pcapPacket)
    {
        if (!pcapHelper.isIpv4(pcapPacket))
        {
            messageData.addMessage("Not IPv4");
            return;
        }
        if (DEBUG_LOG)
        {
            messageData.addMessage(pcapHelper.getSourceHost(pcapPacket).getAddressString() + " -> " + pcapHelper.getDestHost(pcapPacket).getAddressString() + " (" + pcapHelper.getLength(pcapPacket) + " bytes)");
        }
        event.setHost(pcapHelper.getDestHost(pcapPacket));
        event.setLength(pcapHelper.getLength(pcapPacket));
        event.setEpochMinute(pcapHelper.getEpochMinute(pcapPacket));
    }

    /**
     * Creates and starts the disruptor
     */
    @PostConstruct
    public void init()
    {
        disruptor = new Disruptor<>(NewDataEvent::new, BUFFER_SIZE, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(this::onEvent);
        disruptor.start();
        messageData.addMessage("Started new data processor");
    }

    /**
     * Queue a captured packet to be processed
     * @param pcapPacket Data that was captured
     */
    public void processNewData(PcapPacket pcapPacket)
    {
        disruptor.getRingBuffer().publishEvent(this::translate, pcapPacket);
    }

}