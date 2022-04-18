package com.github.vgaj.phonehomemonitor.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RemoteAddress implements Comparable
{
    // TODO: Store in array
    private final byte octet1,octet2,octet3,octet4;
    private String hostname = null;
    private String reverseHostname = null;

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
                if (hostname != null)
                {
                    List<String> parts = Arrays.asList(hostname.split("\\."));
                    Collections.reverse(parts);
                    reverseHostname = String.join(".", parts);
                }

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

    @Override
    public int compareTo(Object o)
    {
        if (this == o || o == null || getClass() != o.getClass())
        {
            return 0;
        }
        RemoteAddress other = (RemoteAddress) o;
        if (this.reverseHostname == null || other.reverseHostname == null)
        {
            return 0;
        }
        else
        {
            return this.reverseHostname.compareTo(other.reverseHostname);
        }
    }
}
