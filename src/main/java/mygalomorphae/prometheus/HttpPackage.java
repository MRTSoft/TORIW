package mygalomorphae.prometheus;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpPackage {
    public HttpHeader Header;
    public String Content;
    public boolean HasErrors = false;

    public HttpPackage(){
        Header = new HttpHeader();
        Content = "";
    }

    public static HttpPackage GetPackageFromRawData(BufferedReader dataStream){
        boolean processingHeader = true;
        HttpPackage reply = new HttpPackage();
        try {
            String statusLine = dataStream.readLine();
            reply.Header.setStatusLIne(statusLine);
            while (processingHeader) {
                //Do stuff with the header
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            reply.HasErrors = true;
        }
        return reply;
    }

    public static HttpPackage CreateGetRequest(String path){
        HttpPackage req = new HttpPackage();
        req.Header = new HttpHeader(path);
        return req;
    }

    public String toString(){
        return Header.toString() + Content.toString();
    }
}
