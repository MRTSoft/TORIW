package gombak;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class IndexData{
    //public Hashtable<String, Hashtable<String, Integer>> _index;
    //public Hashtable<String, WordRecord> _reverseIndex;
    private MongoAdapter _adapter;
    public IndexData() throws Exception{
        _adapter = new MongoAdapter();
    }


    public void generateReverseIndex(){
        //TODO Make this parallel

        ArrayList<String> allDocNames = _adapter.getAllDocumentNames();
        for(String doc : allDocNames){
            Hashtable<String, Integer> entry = _adapter.getDocument(doc);
            for(String word : entry.keySet()){
                _adapter.incrementReverseIndexEntry(word,doc);
            }
        }
        //Calculate df and idf

        //DocumentFreq
        Double totalDocs = _adapter.getGrandTotalDocuments();
        ArrayList<String> allTerms = _adapter.getAllTermNames();
        for(String word : allTerms){
            double idf = Math.log10(totalDocs/(1.0+ _adapter.getNoOfDocumentsForWord(word))); // how important each term is
            _adapter.setIDF(word, idf);
        }
    }

    public void AddDirectEntry(String key, Hashtable<String,Integer> entry){
        _adapter.addDirectIndexEntry(key, entry);
    }

}
