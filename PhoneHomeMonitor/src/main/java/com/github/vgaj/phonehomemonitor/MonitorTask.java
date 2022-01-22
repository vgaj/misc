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
        try {
            PcapNetworkInterface nif = null;
            try {
                Optional<PcapNetworkInterface> optInt = Pcaps.findAllDevs().stream()
                        .filter(i -> !i.isLoopBack()
                                && !i.getAddresses().isEmpty()
                                && !i.getName().equalsIgnoreCase("any")
                                && i.isRunning()
                                && i.isUp())
                        .findFirst();
                if (optInt.isEmpty())
                {
                    data.append("Could not find NIC");
                    return;
                }
                nif = optInt.get();
                data.append("Using " + nif.getName());
                data.addBreak();
            } catch (PcapNativeException e) {
                data.append(e.toString());
                return;
            }

            PcapHandle handle = nif.openLive(65536, PROMISCUOUS, 100);
            String filter = "tcp dst port 80 or 443";
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);


            PacketListener listener =
                    new PacketListener() {
                        @Override
                        public void gotPacket(PcapPacket pcapPacket) {
                            if (pcapPacket.getRawData()[14]>>4 != 4)
                            {
                                data.append("Not IPv4");
                                data.addBreak();
                                return;
                            }

                            // TODO Extract method
                            int sourceStart = 14+12;
                            data.append(Byte.toUnsignedInt(pcapPacket.getRawData()[sourceStart++]));
                            data.append(".");
                            data.append(Byte.toUnsignedInt(pcapPacket.getRawData()[sourceStart++]));
                            data.append(".");
                            data.append(Byte.toUnsignedInt(pcapPacket.getRawData()[sourceStart++]));
                            data.append(".");
                            data.append(Byte.toUnsignedInt(pcapPacket.getRawData()[sourceStart]));
                            data.append(" -> ");
                            int destStart = 14+16;
                            data.append(Byte.toUnsignedInt(pcapPacket.getRawData()[destStart++]));
                            data.append(".");
                            data.append(Byte.toUnsignedInt(pcapPacket.getRawData()[destStart++]));
                            data.append(".");
                            data.append(Byte.toUnsignedInt(pcapPacket.getRawData()[destStart++]));
                            data.append(".");
                            data.append(Byte.toUnsignedInt(pcapPacket.getRawData()[destStart]));
                            data.append(" (" + pcapPacket.getOriginalLength() + ")");
                            data.addBreak();
                        }
                    };
            handle.loop(10, listener);
            handle.close();
        } catch (Exception e) {
            e.printStackTrace();
            data.append(e.toString());
        }

    }
    public String getMsg()
    {
        return data.getData();
    }
}
