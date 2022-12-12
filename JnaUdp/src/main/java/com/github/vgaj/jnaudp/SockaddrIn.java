package com.github.vgaj.jnaudp;

import com.sun.jna.Structure;
import lombok.Getter;

@Structure.FieldOrder({"addressFamily", "port", "address", "pad"})
public class SockaddrIn  extends Structure
{
    @Getter
    private int length = 16;

    public short addressFamily;
    public byte[] port = new byte[2];
    public byte[] address = new byte[4];
    public byte[] pad = new byte[getLength()-8];

};
