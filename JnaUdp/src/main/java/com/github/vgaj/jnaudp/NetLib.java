package com.github.vgaj.jnaudp;

import com.sun.jna.Memory;
import com.sun.jna.Native;

import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NetLib
{
    NetLibC libC;
    NetLib()
    {
        libC = Native.load("c", NetLibC.class);
    }
    void SendUdpDatagram(String destination, short port, String message)
    {
        try
        {
            // Create Socket
            short AF_INET = 2;
            int SOCK_DGRAM = 2;
            int socketFd = libC.socket(AF_INET, SOCK_DGRAM, 0);

            // Set Address
            SockaddrIn address = new SockaddrIn();
            address.addressFamily = AF_INET;
            address.port = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(port).array();
            address.address = Inet4Address.getByName(destination).getAddress();
            address.write();

            // Send Message
            byte[] data = (message + System.lineSeparator()).getBytes();
            try (Memory dataMemory = new Memory(data.length))
            {
                dataMemory.write(0, data, 0, data.length);
                long byteSent = libC.sendto(socketFd, dataMemory, data.length, 0, address.getPointer(), address.getLength());
                System.out.printf("Sent %d bytes\n", byteSent);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}
