package com.github.vgaj.phonehomemonitor;

import org.pcap4j.core.*;

import java.util.Optional;

import static org.pcap4j.core.PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;

public class MonitorTask implements Runnable
{
    private MonitorData data = new MonitorData();
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
                    data.addMessage("Could not find NIC");
                    return;
                }
                nif = optInt.get();
                data.addMessage("Using " + nif.getName());
            }
            catch (PcapNativeException e)
            {
                data.addMessage(e.toString());
                return;
            }

            PcapHandle handle = nif.openLive(65536, PROMISCUOUS, 100);
            String filter = "tcp dst port 80 or 443";
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);


            PacketListener listener =
                    pcapPacket -> {
                        PcapPacketHelper pcapHelper = new PcapPacketHelper(pcapPacket);
                        if (!pcapHelper.isIpv4())
                        {
                            data.addMessage("Not IPv4");
                            return;
                        }
                        data.addMessage(pcapHelper.getSourceAddress() + " -> " + pcapHelper.getDestAddress() + " (" + pcapHelper.getLength() + ")");
                    };
            // TODO: Make this continuous
            handle.loop(10000, listener);
            handle.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            data.addMessage(e.toString());
        }

    }
    public String getMsg()
    {
        return data.getData();
    }
}
