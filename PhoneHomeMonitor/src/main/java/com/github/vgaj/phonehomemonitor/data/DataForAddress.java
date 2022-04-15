package com.github.vgaj.phonehomemonitor.data;

public class DataForAddress
{
    private int totalBytes;

    public DataForAddress(int totalBytes)
    {
        this.totalBytes = totalBytes;
    }

    public void addBytes(int count)
    {
        totalBytes += count;
    }
    public int getTotalBytes()
    {
        return totalBytes;
    }
}
