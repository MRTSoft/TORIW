package mygalomorphae.prometheus;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

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
            reply.Header.setStatusLine(statusLine);
            while (processingHeader) {
                //Do stuff with the header
                String line = dataStream.readLine();
                if ( line.trim().isEmpty() ){
                    //end of header
                    //Start reading the body
                    processingHeader = false;
                    if (reply.Header.Headers.containsKey( "content-length" )){
                        int contentLen = Integer.parseInt( reply.Header.Headers.get( "content-length" ) );
                        StringBuilder sb = new StringBuilder(  );
                        for( int i=0; i<contentLen; ++i){
                            sb.append( (char)dataStream.read() );
                        }
                        reply.Content = sb.toString();
                    }
                } else {
                    String[] components = new String[2];
                    int separator = line.indexOf( ':' );
                    components[0] = line.substring( 0, separator );
                    components[1] = line.substring( separator+1 );
                    reply.Header.Headers.put( components[0].trim().toLowerCase(), components[1].trim().toLowerCase() );
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            reply.HasErrors = true;
        }
        return reply;
    }

    public static HttpPackage CreateGetRequest( URL path) {
        HttpPackage req = new HttpPackage();
        try {
            req.Header = new HttpHeader( path );
        }
        catch ( Exception ex ){
            ex.printStackTrace();
            req.Header.HasErrors = true;
        }
        return req;
    }

    public String toString(){
        return Header.toString() + Content;
    }
}
