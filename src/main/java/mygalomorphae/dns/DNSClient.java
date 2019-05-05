package mygalomorphae.dns;

import java.io.*;
import java.net.*;

import mygalomorphae.dns.Package.DNSPackage;
import mygalomorphae.dns.Utils.BitOperations;

public class DNSClient {
    private static final byte[] DNSServerIP = {8,8,8,8};
    private static final int DNSPort = 53;
    public static void main(String args[]){
        DNSPackage request = DNSPackage.CreateSimpleQuery("www.google.com");
        byte[] rawByte = request.serializeContent();
        byte[] recvPackage = new byte[1024];
        System.out.println("==== Request ====");
        BitOperations.printBytes(rawByte);
        System.out.println("---- ------- ----");
        return;
        /*
        try {
            DatagramSocket socket = new DatagramSocket(DNSPort, Inet4Address.getByAddress(DNSServerIP));
            DatagramPacket requestPkg = new DatagramPacket(rawByte, rawByte.length, Inet4Address.getByName("localhost"), 1996);
            socket.send(requestPkg);
            DatagramPacket response = new DatagramPacket(recvPackage, recvPackage.length);
            socket.receive(response);
            BitOperations.printBytes(recvPackage);
        }
        catch (Exception se){
            se.printStackTrace();
            return;
        }
        */
    }
}
