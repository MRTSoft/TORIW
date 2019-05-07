package mygalomorphae.prometheus;

import com.sun.jndi.dns.DnsClient;
import mygalomorphae.dns.DNSClient;
import mygalomorphae.dns.Utils.LabelUtil;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class SimpleHttpClient {
    private DNSClient mDNS;

    public static void main(String argv[]) throws Exception {
        DNSClient dns = new DNSClient();
        SimpleHttpClient client = new SimpleHttpClient(dns);
        String simpleOutput = client.simpleRequest(new URL("http://www.iana.org/domains/reserved"));
        System.out.println( "==================================================" );
        System.out.println( simpleOutput );
    }

    public SimpleHttpClient(DNSClient dns){
        mDNS = dns;
    }

    public static int HTTP_DEFAULT_PORT = 80;

    public String simpleRequest(URL path){
        String document = null;

        HttpPackage request = HttpPackage.CreateGetRequest(path);

        InetAddress addr = null;
        if (mDNS != null){
            addr = mDNS.getAddressOf(path.getHost());
        } else {
            try {
                addr = Inet4Address.getByName(path.getHost());
            }
            catch (UnknownHostException uhe){
                uhe.printStackTrace();
                return null;
            }
        }
        try {
            Socket httpSocket = new Socket(addr, HTTP_DEFAULT_PORT);
            DataOutputStream outToServer = new DataOutputStream(httpSocket.getOutputStream());
            outToServer.write(request.toString().getBytes());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(httpSocket.getInputStream()));
            HttpPackage reply = HttpPackage.GetPackageFromRawData(inFromServer);
            httpSocket.close();
            //TODO a keep-alive mechanism for subsequent calls
            if (!reply.HasErrors){
                document = reply.Content;
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
            return null;
        }

        return document;
    }
}
