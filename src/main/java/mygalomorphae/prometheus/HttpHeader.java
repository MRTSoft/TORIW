package mygalomorphae.prometheus;

import java.net.URL;
import java.util.Hashtable;
import java.util.Objects;

public class HttpHeader {
    public static String GET_FORMAT = "GET %s HTTP/1.1";

    public Hashtable<String, String> Headers;
    public boolean HasErrors = false;
    public Integer StatusCode = null;
    public String StatusLine = null;
    public String HttpMethod = null;
    private String mHost;
    private String mResource;

    public HttpHeader(){
        Headers = new Hashtable<>(  );
        Headers.put( "Host", "www.example.com" );
        Headers.put( "User-Agent" , "robot" );
        Headers.put( "Content-length", "0" );
        Headers.put( "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
    }

    public HttpHeader( URL path ) throws Exception {
        Headers = new Hashtable<>(  );
        Headers.put( "user-agent" , "robot" );
        Headers.put( "content-length", "0" );
        Headers.put( "accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
        Headers.put( "host", path.getHost() );
        mHost = path.getHost();
        String resource = path.getPath();
        String query = path.getQuery();
        if (query!=null && query.length()>0){
            resource = String.format( "%s?%s", resource, query );
        }
        setStatusLine(String.format( GET_FORMAT, resource ) );
    }

    public void setStatusLine( String line ) throws Exception {
        String[] components = line.split( " " );
        if ( components[0].startsWith( "HTTP" ) ) { //This is a response message
            StatusCode = Integer.parseInt( components[1] );
        }
        else {
            //This is a request msg
            HttpMethod = components[0];
            mResource = components[1];
        }
        StatusLine = line;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(  );
        sb.append( StatusLine ).append( "\r\n" );
        for( String key : Headers.keySet() ){
            sb.append( String.format( "%s: %s", key, Headers.get( key ) ) ).append( "\r\n" );
        }
        sb.append( "\r\n" );

        return sb.toString();
    }
}
