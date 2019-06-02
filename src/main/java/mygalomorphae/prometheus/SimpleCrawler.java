package mygalomorphae.prometheus;

import gombak.Parser;
import mygalomorphae.dns.DNSClient;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


public class SimpleCrawler {
    public static Integer DEFAULT_LINK_LIMIT = 500;
    public static Integer DEFAULT_TIME_LIMIT = 90;

    private Queue<URL> mQueue;
    private HashSet<URL> mVisited;
    private Hashtable<String, REPFilter> mVisitedDomains;
    private Integer mLinkLimit;
    private Integer mTimeLimitSeconds;

    private class MyURL extends Object {
        private String mData = null;

        public MyURL(String link) {
            mData = link;
        }

        public boolean equals(Object anObject){
            if (anObject instanceof MyURL){
                return Objects.equals(mData, ((MyURL) anObject).mData);
            }
            return false;
        }

        public int hashCode() {
            if (mData == null){
                return 0;
            }
            return mData.hashCode();
        }
    }

    private SimpleCrawler(){
        mQueue = new LinkedList<>();
        mVisited = new HashSet<>();
        mVisitedDomains = new Hashtable<>();
    }

    public Integer getLinkLimit() {
        return mLinkLimit;
    }

    public void setLinkLimit(Integer linkLimit) {
        this.mLinkLimit = linkLimit;
        if (mLinkLimit == null || mLinkLimit < 1){
            mLinkLimit = DEFAULT_LINK_LIMIT;
        }
    }

    public Queue<URL> getQueue() {
        return mQueue;
    }

    public Integer getTimeLimitSeconds() {
        return mTimeLimitSeconds;
    }

    public void setTimeLimitSeconds(Integer timeLimitSeconds) {
        this.mTimeLimitSeconds = timeLimitSeconds;
        if (mTimeLimitSeconds == null || mTimeLimitSeconds < 1){
            mTimeLimitSeconds = DEFAULT_TIME_LIMIT;
        }
    }
    File mStorageLocation = null;

    public SimpleCrawler(File workingDirectory, List<URL> seed, Integer maxLinks, Integer maxTimeSeconds) {

        mQueue = new LinkedList<>();
        mVisited = new HashSet<>();
        mVisitedDomains = new Hashtable<>();

        mQueue.addAll(seed);
        mLinkLimit = maxLinks;
        mTimeLimitSeconds = maxTimeSeconds;
        mStorageLocation = workingDirectory;

        setLinkLimit(maxLinks);
        setTimeLimitSeconds(maxTimeSeconds);
    }

    private SimpleHttpClient mClient = null;
    //TODO

    private String addIndexFile(String path){
        if (path.endsWith("/")){
            return path+"index.html";
        }
        return path;
    }

    private void processLink(URL link) throws Exception {
        StringBuilder path = new StringBuilder();
        path.append(mStorageLocation.getAbsolutePath());
        path.append("/").append(link.getHost()).append("/");
        path.append(link.getPath());

        File dst = new File( addIndexFile(path.toString()) );
        if (!dst.getParentFile().exists()){
            dst.getParentFile().mkdirs();
        }
        mVisited.add(new URL(addIndexFile(link.toString())));

        //Check robots.txt
        if (!canRequestLink(link)){
            return;
        }

        HttpPackage reply = mClient.GetRequest(link);
        String content = null;
        if (reply.Header.StatusCode == 200){
            content = reply.Content;
        }
        else {
            if (reply.Header.StatusCode > 299 && reply.Header.StatusCode < 400){
                //TODO handle redirect
            }
        }
        if (content == null){
            return;
        }
        PrintStream writer = new PrintStream(new FileOutputStream(dst) );
        writer.write(content.getBytes());
        writer.close();

        //Extract new links and add them to the queue
        StringBuilder title = new StringBuilder(); // out
        StringBuilder description = new StringBuilder(); // out
        StringBuilder keywords = new StringBuilder(); // out
        StringBuilder robots = new StringBuilder(); // out
        ArrayList<String> externalURLs = new ArrayList<>(); // out
        StringBuilder TextPageContent = new StringBuilder();
        String absPath = link.toString();
        int lastSlash = absPath.lastIndexOf('/');
        absPath = absPath.substring(0, lastSlash + 1);
        Parser.ParseDocumentFromFile(dst.getAbsolutePath(), // in
                absPath, // in
                title, // out
                description, // out
                keywords, // out
                robots, // out
                externalURLs, // out
                TextPageContent);

        String[] robotsDirectives = robots.toString().split(",");
        boolean canIndex = true;
        boolean canFollow = true;
        for(String directive : robotsDirectives){
            if (directive.trim().equalsIgnoreCase("none")){
                canIndex = false;
                canFollow = false;
            }
            if (directive.trim().equalsIgnoreCase("noindex")) {
                canIndex = false;
            }
            if (directive.trim().equalsIgnoreCase("nofollow")) {
                canFollow = false;
            }
        }

        if (canFollow) {
            for (String nLink : externalURLs) {
                URL extractedLink = new URL(nLink);
                //if (Objects.equals(extractedLink.getHost(), link.getHost())) { //Only process links from the same domain
                    if ((!URLInQueue(extractedLink)) && (!URLVisited(extractedLink))) {
                        mQueue.add(new URL(addIndexFile(nLink)));
                    }
                //}
            }
        }
        if (!canIndex){
            dst = new File( addIndexFile(path.toString()) );
            dst.delete();
        }
    }

    private boolean canRequestLink(URL link){
        REPFilter filter = null;
        String host = link.getHost();
        if (!mVisitedDomains.containsKey(host)){
            //Request robots.txt
            URL robots_txt = null;
            try {
                robots_txt = new URL("http://" + link.getHost() + "/robots.txt");

            }
            catch (Exception ex) {
                return true;//No robots.txt found
            }
            HttpPackage rPackage = mClient.GetRequest(robots_txt);
            if (rPackage.Header.StatusCode != 200){
                return true;
            }
            filter = new REPFilter();
            filter.SetRules(rPackage.Content);
            mVisitedDomains.put(host, filter);
        }

        filter = mVisitedDomains.get(host);
        if (filter == null){
            return true;
        }
        return filter.CanAccess(link);
    }

    private boolean URLVisited(URL url) {
        for(URL link : mVisited){
            if (Objects.equals(link.toString(), url.toString()) ){
                return true;
            }
        }
        return false;
    }

    private boolean URLInQueue(URL url) {
        for(URL link : mQueue){
            if (Objects.equals(link.toString(), url.toString()) ){
                return true;
            }
        }
        return false;
    }

    public void start(){
        if (mClient == null) {
            mClient = new SimpleHttpClient(new DNSClient());
        }
        long timer = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date startDate = new Date(timer);
        System.out.println(sdf.format(startDate));
        System.out.println();
        long elapsedMilliseconds = 0;
        long processedLinks = 0;

        while (
                (mTimeLimitSeconds > elapsedMilliseconds/1000) &&
                (mLinkLimit > processedLinks) &&
                (mQueue.isEmpty() == false) )
        {
            URL cLink = mQueue.remove();
            try {
                processLink(cLink);
            }
            catch (Exception ex){
                System.err.println("Error on getting " + cLink.toString());
                ex.printStackTrace();
            }
            long now = System.currentTimeMillis();
            elapsedMilliseconds += (now - timer);
            timer = now;
            processedLinks++;
            System.out.println("Processed " + cLink.toString());
        }

        if (mQueue.isEmpty()){
            System.out.println("Queue is empty");
        }
        if (mTimeLimitSeconds < (elapsedMilliseconds/1000)) {
            System.out.println("Time limit expired");
        }
        if (mLinkLimit < processedLinks){
            System.out.println("Link count exceeded");
        }
        System.out.println(sdf.format(timer));
        System.out.println(String.format("Processed %d links in %f seconds", processedLinks, elapsedMilliseconds/1000.0));
    }

    public static void main (String [] args){
        LoadConfig("./data/gombak.conf");
        if (_properties.containsKey("app.logs")){
            try {
                FileOutputStream stderr = new FileOutputStream(new File(_properties.getProperty("app.logs")));
                PrintStream ps = new PrintStream(stderr);
                System.setErr(ps);
                System.setOut(ps);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
        try {
            LinkedList<URL> startingPoints = new LinkedList<>();
            String output_folder_path = _properties.getProperty("crawler.output");
            if (output_folder_path == null)
                output_folder_path = "./crawler_data";
            startingPoints.add( new URL(_properties.getProperty("crawler.starting_point")) );
            File wd = new File(new File(output_folder_path).getCanonicalPath());
            REPFilter.UserAgent = _properties.getProperty("crawler.rep_agent");
            HttpHeader.UserAgent = _properties.getProperty("crawler.agent");
            if ( !wd.exists() ){
                wd.mkdirs();
            }
            if (wd.isDirectory() == false){
                throw new Exception("Bad path!");
            }
            Integer maxLinks = null;
            Integer maxSeconds = null;

            try {
                maxLinks = Integer.parseInt(_properties.getProperty("crawler.max_links"));
                maxSeconds = Integer.parseInt(_properties.getProperty("crawler.max_seconds"));
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

            SimpleCrawler crawler = new SimpleCrawler(wd, startingPoints, maxLinks, maxSeconds );
            crawler.start();
        }
        catch (Exception ex){
            ex.printStackTrace();
            return;
        }
    }

    private static Properties _properties;

    private static void LoadConfig(String configFile){
        _properties = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(configFile);
        } catch (FileNotFoundException ex) {
            System.out.println("Config file not found!");
            ex.printStackTrace();
        }
        try {
            _properties.load(is);
        } catch (IOException ex) {
            System.out.println("Failed to load config file! (BAD format)");
            ex.printStackTrace();
        }
        //System.out.println("=====================================");
        //System.out.println(_properties.getProperty("app.name"));
        //System.out.println(_properties.getProperty("app.version"));
    }

}
