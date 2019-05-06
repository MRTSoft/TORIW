package mygalomorphae.dns;

import java.io.*;
import java.net.*;

import mygalomorphae.dns.Package.DNSPackage;
import mygalomorphae.dns.Utils.BitOperations;

public class DNSClient {
    private static final byte[] DNSServerIP = {8,8,8,8};
    private static final byte[] LocalIP = {0,0,0,0};
    private static final int DNSPort = 53;
    public static void main(String args[]){
        DNSPackage request = DNSPackage.CreateSimpleQuery("www.tuiasi.ro", true);

        byte[] rawByte = request.serializeContent();
        byte[] recvPackage = new byte[1024];
        System.out.println("==== Request ====");
        BitOperations.printBytes(rawByte);
        System.out.println("---- ------- ----");
        //return;

        try {
            DatagramSocket socket = new DatagramSocket(DNSPort, Inet4Address.getByAddress(LocalIP));
            DatagramPacket requestPkg = new DatagramPacket(rawByte, rawByte.length, Inet4Address.getByAddress(DNSServerIP), DNSPort);
            socket.send(requestPkg);
            DatagramPacket response = new DatagramPacket(recvPackage, recvPackage.length);
            socket.receive(response);
            System.out.println("==== Response ====");
            BitOperations.printBytes(recvPackage);
            socket.close();
            DNSPackage reply = new DNSPackage(recvPackage);
            if (reply.IPv4Address != null){
                BitOperations.PrintIpAddress(reply.IPv4Address);
                System.out.println(String.format(" is the address of %s", reply.getQuery()));
            }
        }
        catch (Exception se){
            se.printStackTrace();
            return;
        }

    }
}
