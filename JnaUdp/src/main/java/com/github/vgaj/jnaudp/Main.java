package com.github.vgaj.jnaudp;

import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        String destination = "127.0.0.1";
        short port = 9999;
        if (args.length > 0)
        {
            destination = args[0];
        }
        if (args.length > 1)
        {
            port = Short.parseShort(args[1]);
        }

        System.out.printf("Type a message to send via UDP to %s on port %d\n", destination, port);
        Scanner in = new Scanner(System.in);
        String message = in.nextLine();

        NetLib lib = new NetLib();
        lib.SendUdpDatagram(destination, port, message);
    }
}