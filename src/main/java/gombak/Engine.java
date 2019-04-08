package gombak;

import java.io.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

public class Engine {
    private IndexData _index;
    private Parser _parser;
    private SearchEngine _search;

    public Engine() {
        _index = new IndexData();
        _parser = new Parser();
        _search = null;
    }


    /// Parse an entire folder and create the index
    /// @param indexLocation Where to store the index of the parsed files
    /// @param path Path of the folder that we want to parse
    /// @param parseSubfolder setting this to true will enable recursive parsing
    public void ParseFolder(String path, Boolean parseSubfolders) throws IOException {
        Queue<File> pQueue = new LinkedList<File>();

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
                ParseDocument(f.getAbsolutePath());
            }
        }
        _index.generateReverseIndex();
        _search = new SearchEngine();
        _search.addIndexData(_index);
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

    public Result[] Query(String query){
        if (_search == null){
            return new Result[0];
        }
        return _search.RunQuery(query).SortedResults;
    }

}
