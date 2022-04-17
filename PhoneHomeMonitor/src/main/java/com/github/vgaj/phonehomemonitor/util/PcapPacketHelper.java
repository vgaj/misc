package com.github.vgaj.phonehomemonitor.util;

import com.github.vgaj.phonehomemonitor.data.RemoteAddress;
import org.pcap4j.core.PcapPacket;

/**
 * Functionality to parse pcap data
 */
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

    public RemoteAddress getSourceHost()
    {
        return getHostAtOffset(14+12);
    }
    public RemoteAddress getDestHost()
    {
        return getHostAtOffset(14+16);
    }
    private RemoteAddress getHostAtOffset(int offset)
    {
        // Want data to be stored on the stack
        byte octet1,octet2,octet3,octet4;
        octet1 = pcapPacket.getRawData()[offset++];
        octet2 = pcapPacket.getRawData()[offset++];
        octet3 = pcapPacket.getRawData()[offset++];
        octet4 = pcapPacket.getRawData()[offset++];
        return new RemoteAddress(octet1,octet2,octet3,octet4);
    }
    public int getLength()
    {
        return pcapPacket.getOriginalLength();
    }

    public long getEpochMinute()
    {
        return pcapPacket.getTimestamp().getEpochSecond() / 60;
    }
}
