package com.github.vgaj.phonehomemonitor.logic;

import com.github.vgaj.phonehomemonitor.data.MonitorData;
import com.github.vgaj.phonehomemonitor.util.PcapPacketHelper;
import org.pcap4j.core.*;

import java.util.Optional;

import static org.pcap4j.core.PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;

/**
 * Retrieves captured data from Pcap4j
 */
public class MonitorTask implements Runnable
{
    boolean DEBUG_LOG = true;
    private final MonitorData monitorData;

    public MonitorTask(MonitorData monitorData)
    {
        this.monitorData = monitorData;
    }

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
                    monitorData.addMessage("Could not find NIC");
                    return;
                }
                nif = optInt.get();
                monitorData.addMessage("Using " + nif.getName());
            }
            catch (PcapNativeException e)
            {
                monitorData.addMessage(e.toString());
                return;
            }

            PcapHandle handle = nif.openLive(65536, PROMISCUOUS, 100);

            // TODO: Make configurable
            String filter = "tcp dst port 80 or 443";
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);


            PacketListener listener =
                    pcapPacket -> {
                        PcapPacketHelper pcapHelper = new PcapPacketHelper(pcapPacket);
                        if (!pcapHelper.isIpv4())
                        {
                            monitorData.addMessage("Not IPv4");
                            return;
                        }
                        // TODO: Queue on a Disruptor to avoid blocking the caller for too long
                        if (DEBUG_LOG)
                        {
                            monitorData.addMessage(pcapHelper.getSourceHost().getAddressString() + " -> " + pcapHelper.getDestHost().getAddressString() + " (" + pcapHelper.getLength() + " bytes)");
                        }
                        monitorData.addData(pcapHelper.getDestHost(), pcapHelper.getLength(), pcapHelper.getEpochMinute());
                    };
            handle.loop(-1, listener);
            handle.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            monitorData.addMessage(e.toString());
        }
    }
}
