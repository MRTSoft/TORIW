package gombak;

import java.io.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

public class Engine {
    private IndexData _index;
    private SearchEngine _search;

    public Engine() throws Exception{
        _index = new IndexData();
        _search = new SearchEngine();
    }


    /// Parse an entire folder and create the index
    /// @param indexLocation Where to store the index of the parsed files
    /// @param path Path of the folder that we want to parse
    /// @param parseSubfolder setting this to true will enable recursive parsing
    public void ParseFolder(String path, Boolean parseSubfolders, Boolean isVerbose) throws IOException {
        Queue<File> pQueue = new LinkedList<File>();
        Queue<String> processedDocuments = new LinkedList<>();

        // Enqueue all files in the folder (including files)
        File inFolder = new File(path);
        if (inFolder.isDirectory()) {
            for (File f : inFolder.listFiles()) {
                pQueue.add(f);
            }
        } else {
            throw new FileNotFoundException("Not found " + inFolder.getAbsolutePath());
        }
        //Process queue
        while (!pQueue.isEmpty()) {
            File f = pQueue.remove();
            if (f.isDirectory() && parseSubfolders) {
                for (File sf : f.listFiles()) {
                    pQueue.add(sf);
                }
            }
            if (f.isFile()) {
                //Parse the file
                if (isVerbose)
                    System.out.println("Parsing " + f.getAbsolutePath());
                ParseDocument(f.getAbsolutePath());
                processedDocuments.add(f.getAbsolutePath());
            }
        }
        System.out.println("Generating reverse index. Please wait...");
        _index.generateReverseIndex(processedDocuments);
        _search = new SearchEngine();
    }

    //Parse a text file
    public void ParseDocument(String doc){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(doc)));
            Hashtable<String, Integer> words = Parser.getWordsStatsFromStream(reader);
            _index.AddDirectEntry(doc, words);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /// Run a query against the index
    public Result[] Query(String query){
        if (_search == null){
            return new Result[0];
        }
        return _search.RunQuery(query).SortedResults;
    }

    ///Clears the index
    public void ClearIndex(){
        _index.clear();
    }
}
