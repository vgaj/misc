package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.MessageData;
import com.github.vgaj.phonehomemonitor.data.MonitorData;
import com.github.vgaj.phonehomemonitor.util.PcapPacketHelper;
import org.pcap4j.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;

import static org.pcap4j.core.PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;

/**
 * Retrieves captured data from Pcap4j
 */
@Component
public class MonitorTask implements Runnable
{
    boolean DEBUG_LOG = false;

    @Autowired
    private MonitorData monitorData;

    @Autowired
    private MessageData messageData;

    private Thread monitorThread;

    private PcapHandle handle;

    @PostConstruct
    public void start()
    {
        monitorThread = new Thread(this);
        monitorThread.start();
    }

    @PreDestroy
    public void stop()
    {
        try
        {
            handle.breakLoop();
            monitorThread.join(5000);
        }
        catch (NotOpenException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Value("${phm.filter}")
    private String filter;

    @Override
    public void run()
    {
        try
        {
            PcapNetworkInterface nif = null;
            try
            {
                Optional<PcapNetworkInterface> optInt = Pcaps.findAllDevs().stream()
                        .filter(i -> !i.isLoopBack()
                                && !i.getAddresses().isEmpty()
                                && !i.getName().equalsIgnoreCase("any")
                                && i.isRunning()
                                && i.isUp())
                        .findFirst();
                if (optInt.isEmpty())
                {
                    messageData.addMessage("Could not find NIC");
                    return;
                }
                nif = optInt.get();
                messageData.addMessage("Using " + nif.getName());
            }
            catch (PcapNativeException e)
            {
                messageData.addMessage(e.toString());
                return;
            }

            handle = nif.openLive(65536, PROMISCUOUS, 100);

            messageData.addMessage("Using filter: " + filter);
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

            PacketListener listener =
                    pcapPacket -> {
                        PcapPacketHelper pcapHelper = new PcapPacketHelper(pcapPacket);
                        if (!pcapHelper.isIpv4())
                        {
                            messageData.addMessage("Not IPv4");
                            return;
                        }
                        // TODO: Queue on a Disruptor to avoid blocking the caller for too long
                        if (DEBUG_LOG)
                        {
                            messageData.addMessage(pcapHelper.getSourceHost().getAddressString() + " -> " + pcapHelper.getDestHost().getAddressString() + " (" + pcapHelper.getLength() + " bytes)");
                        }
                        monitorData.addData(pcapHelper.getDestHost(), pcapHelper.getLength(), pcapHelper.getEpochMinute());
                    };
            handle.loop(-1, listener);
            handle.close(); // This won't normally get called
        }
        catch (InterruptedException e)
        {
        }
        catch (Exception e)
        {
            e.printStackTrace();
            messageData.addMessage(e.toString());
        }
    }
}
