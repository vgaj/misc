package com.github.vgaj.phonehomemonitor;

public class MonitorData
{
    private StringBuffer sb = new StringBuffer();
    public void append(String s)
    {
        sb.append(s);
    }

    public void append(int i)
    {
        sb.append(i);
    }

    public void addBreak()
    {
        sb.append("<br>");
    }

    public String getData()
    {
        return sb.toString();
    }
}
