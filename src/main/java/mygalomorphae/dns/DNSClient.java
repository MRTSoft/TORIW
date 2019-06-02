package mygalomorphae.dns;

import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.Hashtable;

import mygalomorphae.dns.Package.DNSPackage;
import mygalomorphae.dns.Utils.BitOperations;

public class DNSClient {
    private class RecordEntry{
        public Inet4Address address;
        public Instant expiry;

        public RecordEntry() {
            address = null;
            expiry = null;
        }
    }

    private static final byte[] DNSServerIP = {8,8,8,8};
    private static final byte[] LocalIP = {0,0,0,0};
    private static final int DNSPort = 53;
    private Hashtable<String, RecordEntry> cache = new Hashtable<>();
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

    public DNSClient(){

    }

    public InetAddress getAddressOf(String server){
        Inet4Address cacheRecord = getCacheRecord(server);
        if (cacheRecord != null){
            return cacheRecord;
        }
        DNSPackage request = DNSPackage.CreateSimpleQuery(server, true);
        byte[] rawByte = request.serializeContent();
        byte[] recvPackage = new byte[1024];
        //System.out.println("==== Request ====");
        //BitOperations.printBytes(rawByte);
        //System.out.println("---- ------- ----");

        try {
            //TODO caching for known adresses
            DatagramSocket socket = new DatagramSocket(9996, Inet4Address.getByAddress(LocalIP));
            socket.setSoTimeout(1000);
            DatagramPacket requestPkg = new DatagramPacket(rawByte, rawByte.length, Inet4Address.getByAddress(DNSServerIP), DNSPort);
            socket.send(requestPkg);
            DatagramPacket response = new DatagramPacket(recvPackage, recvPackage.length);
            socket.receive(response);
            //System.out.println("==== Response ====");
            //BitOperations.printBytes(recvPackage);
            socket.close();
            DNSPackage reply = new DNSPackage(recvPackage);
            if (reply.IPv4Address != null){
                //BitOperations.PrintIpAddress(reply.IPv4Address);
                //System.out.println(String.format(" is the address of %s", reply.getQuery()));
                setCacheRecord(server, (Inet4Address) Inet4Address.getByAddress(reply.IPv4Address), reply.TimeToLive);
                return Inet4Address.getByAddress(reply.IPv4Address);
            }
        }
        catch (Exception se){
            System.err.println("Unable to resolve ip address of " + server);
            se.printStackTrace();
            return null;
        }
        return null;
    }

    private Inet4Address getCacheRecord(String host){
        if (cache.containsKey(host)){
            RecordEntry re = cache.get(host);
            if (re.expiry.isAfter(Instant.now())){
                return re.address;
            } else {
              cache.remove(host);
            }
        }
        return null;
    }

    private void setCacheRecord(String host, Inet4Address address, int ttl){
        RecordEntry re = new RecordEntry();
        re.address = address;
        re.expiry = Instant.now().plusSeconds(ttl);
        cache.put(host, re);
        System.out.println("Added :" + host + " in DNS cache");
    }
}
