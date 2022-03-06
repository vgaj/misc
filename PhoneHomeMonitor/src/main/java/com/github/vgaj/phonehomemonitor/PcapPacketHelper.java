package com.github.vgaj.phonehomemonitor;

import org.pcap4j.core.PcapPacket;

public class PcapPacketHelper
{
    private final PcapPacket pcapPacket;
    public PcapPacketHelper(PcapPacket pcapPacket)
    {
        this.pcapPacket = pcapPacket;
    }

    public boolean isIpv4()
    {
        return (pcapPacket.getRawData()[14]>>4 == 4);
    }

    public Host getSourceHost()
    {
        return getHostAtOffset(14+12);
    }
    public Host getDestHost()
    {
        return getHostAtOffset(14+16);
    }
    private Host getHostAtOffset(int offset)
    {
        // Want data to be stored on the stack
        byte octet1,octet2,octet3,octet4;
        octet1 = pcapPacket.getRawData()[offset++];
        octet2 = pcapPacket.getRawData()[offset++];
        octet3 = pcapPacket.getRawData()[offset++];
        octet4 = pcapPacket.getRawData()[offset++];
        return new Host(octet1,octet2,octet3,octet4);
    }
    public int getLength()
    {
        return pcapPacket.getOriginalLength();
    }
}
