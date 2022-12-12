package com.github.vgaj.jnaudp;

import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface NetLibC extends Library
{
    int socket(int domain, int type, int protocol) throws LastErrorException;
    long sendto(int socket, Pointer data, int dataLength, int flags,
                Pointer address, int addressLength) throws LastErrorException;
}
