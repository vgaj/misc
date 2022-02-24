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

    public String getSourceAddress()
    {
        return getAddressAtOffset(14+12);
    }
    public String getDestAddress()
    {
        return getAddressAtOffset(14+16);
    }
    public String getAddressAtOffset(int offset)
    {
        StringBuilder ip = new StringBuilder();
        ip.append(Byte.toUnsignedInt(pcapPacket.getRawData()[offset++]));
        ip.append(".");
        ip.append(Byte.toUnsignedInt(pcapPacket.getRawData()[offset++]));
        ip.append(".");
        ip.append(Byte.toUnsignedInt(pcapPacket.getRawData()[offset++]));
        ip.append(".");
        ip.append(Byte.toUnsignedInt(pcapPacket.getRawData()[offset]));
        return ip.toString();
    }

    public int getLength()
    {
        return pcapPacket.getOriginalLength();
    }
}
