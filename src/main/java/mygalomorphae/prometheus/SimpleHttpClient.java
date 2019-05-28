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
        String simpleOutput = client.simpleRequest(new URL("http://riweb.tibeica.com/robots.txt"));
        System.out.println( "==================================================" );
        System.out.println( simpleOutput );
        REPFilter filter = new REPFilter();
        filter.SetRules(simpleOutput);

        URL u = new URL("http://riweb.tibeica.com/tests/l1_basic");

        System.out.println(REPFilter.UserAgent + " can access: http://riweb.tibeica.com/tests/l1_basic -- "  + filter.CanAccess(u));

        REPFilter.UserAgent="tudorel";

        System.out.println(REPFilter.UserAgent + " can access: http://riweb.tibeica.com/tests/l1_basic -- "  + filter.CanAccess(u));
    }

    public SimpleHttpClient(DNSClient dns){
        mDNS = dns;
    }

    public static int HTTP_DEFAULT_PORT = 80;

    public String simpleRequest(URL path){
        String document = null;

        HttpPackage request = HttpPackage.CreateGetRequest(path);
        HttpPackage reply = GetRequest(path);

        if (!reply.HasErrors){
            document = reply.Content;
        }

        return document;
    }

    public HttpPackage GetRequest(URL link){

        HttpPackage request = HttpPackage.CreateGetRequest(link);
        HttpPackage reply = null;

                InetAddress addr = null;
        if (mDNS != null){
            addr = mDNS.getAddressOf(link.getHost());
        } else {
            try {
                addr = Inet4Address.getByName(link.getHost());
            }
            catch (UnknownHostException uhe){
                uhe.printStackTrace();
                return null;
            }
        }
        try {
            Socket httpSocket = new Socket(addr, link.getPort() == -1 ? HTTP_DEFAULT_PORT : link.getPort());
            DataOutputStream outToServer = new DataOutputStream(httpSocket.getOutputStream());
            outToServer.write(request.toString().getBytes());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(httpSocket.getInputStream()));
            reply = HttpPackage.GetPackageFromRawData(inFromServer);
            httpSocket.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
            return null;
        }

        return reply;
    }
}
