package com.github.vgaj.phonehomemonitor.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class RemoteAddress
{
    private final byte octet1,octet2,octet3,octet4;
    private String hostname = null;

    public RemoteAddress(byte octet1, byte octet2, byte octet3, byte octet4)
    {
        this.octet1 = octet1;
        this.octet2 = octet2;
        this.octet3 = octet3;
        this.octet4 = octet4;
    }

    public String getAddressString()
    {
        StringBuilder ip = new StringBuilder();
        ip.append(Byte.toUnsignedInt(octet1));
        ip.append(".");
        ip.append(Byte.toUnsignedInt(octet2));
        ip.append(".");
        ip.append(Byte.toUnsignedInt(octet3));
        ip.append(".");
        ip.append(Byte.toUnsignedInt(octet4));
        return ip.toString();
    }

    public String getHostString()
    {
        return (hostname != null) ? hostname : getAddressString();
    }
     public String lookupAndGetHostString()
     {
        if (hostname == null)
        {
            try
            {
                InetAddress addr = InetAddress.getByAddress(new byte[]{octet1, octet2, octet3, octet4});
                hostname = addr.getHostName();
                hostname += "/";
            }
            catch (UnknownHostException e)
            {
                // TODO: log
                //e.printStackTrace();
            }
            hostname += getAddressString();
        }
        return hostname;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteAddress host = (RemoteAddress) o;
        return octet1 == host.octet1 && octet2 == host.octet2 && octet3 == host.octet3 && octet4 == host.octet4;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(octet1, octet2, octet3, octet4);
    }

}
